package com.sparkingpathshala.app

import android.app.Application
import com.sparkingpathshala.app.data.local.AppDatabase
import com.sparkingpathshala.app.data.remote.ApiService
import androidx.core.content.edit
import com.sparkingpathshala.app.data.repository.AppRepository
import com.sparkingpathshala.app.data.repository.AppRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SparkingPathshalaApplication : Application() {
    lateinit var repository: AppRepository
    lateinit var database: AppDatabase
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        com.sparkingpathshala.app.core.AppCrashHandler.install(this)
        
        database = AppDatabase.getDatabase(this)
        
        val certificatePinner = okhttp3.CertificatePinner.Builder()
            // Primary leaf pin (expires Dec 2026)
            .add("api.sparkingpathshala.in", "sha256/G3hbzJIdYpN47gMjA+ZBRDzj+PKMTEIXSn2fyzjKj2U=")
            // Let's Encrypt Intermediate (YE1)
            .add("api.sparkingpathshala.in", "sha256/brzvtCELCIZUo4sD/qPX0ccRtPsd3DY6RfmxpOU9oB4=")
            // Intermediate Let's Encrypt R10 / Cross-Signed Backup
            .add("api.sparkingpathshala.in", "sha256/sCkq5UWXjg+7mKu9lMhhYF5bGLsy7VI/UNW3tccdR7w=")
            // Backup Root ISRG Root X2
            .add("api.sparkingpathshala.in", "sha256/diGVwiVYbubAI3RW4hB9xU8e/CH2GnkuvVFZE8zmgzI=")
            // Backup Root ISRG Root X1
            .add("api.sparkingpathshala.in", "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=")
            // Google Trust Services (GTS) Root R1 - Cloudflare Universal SSL secondary CA fallback
            .add("api.sparkingpathshala.in", "sha256/hxqRlPTu1bMS/0DITB1SSu0vd4u/8l8TjPgfaAp63Gc=")
            // Google Trust Services (GTS) Root R2 - Cloudflare Universal SSL secondary CA fallback
            .add("api.sparkingpathshala.in", "sha256/Vfd95BwDeSQo+NUYxVEEIlvkOlWY2SalKK1lPhzOx78=")
            .build()

        val userAgent = "SparkingPathshala-Android/${BuildConfig.VERSION_NAME} (Android ${android.os.Build.VERSION.RELEASE}; ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL})"

        val headerInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("User-Agent", userAgent)
                .header("Accept", "application/json, text/plain, */*")
            chain.proceed(requestBuilder.build())
        }

        val cloudflareErrorInterceptor = okhttp3.Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val contentType = response.body?.contentType()?.toString() ?: ""
            // When Cloudflare returns HTML error/challenge instead of JSON, convert to clean JSON response
            if (!response.isSuccessful && contentType.contains("text/html", ignoreCase = true)) {
                val code = response.code
                val friendlyMsg = when (code) {
                    524 -> "Server processing timed out on Cloudflare. Please retry."
                    520, 521, 522 -> "Backend server is temporarily unreachable. Please retry shortly."
                    523 -> "Origin server is unreachable via Cloudflare network. Please check back shortly."
                    525, 526 -> "Cloudflare SSL handshake with backend failed. Please try again shortly."
                    403 -> "Request blocked by network security shield. Please verify your connection or disable VPN."
                    429 -> "Too many requests. Please wait a moment and try again."
                    else -> "Cloudflare edge response error (HTTP $code). Please try again."
                }
                val jsonBody = org.json.JSONObject().put("error", friendlyMsg).toString()
                val newBody = jsonBody.toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull())
                return@Interceptor response.newBuilder()
                    .body(newBody)
                    .build()
            }
            response
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            if (response.code == 401) {
                val path = request.url.encodedPath
                val hasAuth = !request.header("Authorization").isNullOrBlank()
                val isAuthEndpoint = path.contains("api/auth/login") || path.contains("api/auth/signup")

                if (hasAuth && !isAuthEndpoint) {
                    // Centralized 401 auto-logout: Server rejected token (expired after 7 days, revoked, or password updated)
                    applicationScope.launch {
                        try {
                            database.userDao().clearUser()
                            database.feeDao().clearFees()
                            database.resultDao().clearResults()
                            database.attendanceDao().clearAttendance()
                            database.studyMaterialDao().clearNonDownloadedMaterials()
                            database.quizDao().clearQuizzes()
                            database.questionDao().clearQuestions()
                            database.homeworkDao().clearNonDownloadedHomework()
                            database.notificationDao().clearNotifications()
                            com.sparkingpathshala.app.core.AuthSessionManager.notifySessionExpired()
                        } catch (e: Exception) {
                            // Suppress
                        }
                    }
                }
            }
            response
        }

        val httpCacheDirectory = java.io.File(cacheDir, "http_cache")
        val httpCache = okhttp3.Cache(httpCacheDirectory, 25L * 1024L * 1024L) // 25 MB Cache

        val cacheControlInterceptor = okhttp3.Interceptor { chain ->
            var request = chain.request()
            // For GET requests, serve from cache immediately if offline
            if (request.method.equals("GET", ignoreCase = true) && !com.sparkingpathshala.app.security.SecurityUtils.isNetworkAvailable(this)) {
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                    .build()
            }
            chain.proceed(request)
        }

        val connectionPool = okhttp3.ConnectionPool(8, 5, java.util.concurrent.TimeUnit.MINUTES)
        val okHttpClientBuilder = OkHttpClient.Builder()
            .cache(httpCache)
            .connectionPool(connectionPool)
            .certificatePinner(certificatePinner)
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(25, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(25, java.util.concurrent.TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(cacheControlInterceptor)
            .addInterceptor(headerInterceptor)
            .addInterceptor(cloudflareErrorInterceptor)
            .addInterceptor(authInterceptor)

        val okHttpClient = okHttpClientBuilder.build()
            
        com.sparkingpathshala.app.core.AppMemoryManager.init(this, okHttpClient)
            
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.sparkingpathshala.in/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        val apiService = retrofit.create(ApiService::class.java)
        
        repository = AppRepositoryImpl(database, apiService, okHttpClient)

        // Session migration: Only clear session if migrating from legacy pre-v2 builds (versionCode 1).
        // For v2.0+ (versionCode >= 2) and current build 18, logged-in sessions and hardware tokens are preserved.
        val securePrefs = com.sparkingpathshala.app.security.SecurityUtils.getEncryptedSharedPreferences(this, "app_secure_prefs")
        val lastVersion = securePrefs.getInt("version_code", -1)
        val currentVersion = try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) {
            BuildConfig.VERSION_CODE
        }

        if (lastVersion in 1..1) {
            applicationScope.launch {
                try {
                    repository.logout()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (lastVersion != currentVersion) {
            securePrefs.edit { putInt("version_code", currentVersion) }
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        com.sparkingpathshala.app.core.AppMemoryManager.onTrimMemory(level)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onLowMemory() {
        super.onLowMemory()
        com.sparkingpathshala.app.core.AppMemoryManager.onLowMemory()
    }
}
