package com.sparkingpathshala.app.core

import android.content.Context
import android.os.Build
import android.util.Log
import com.sparkingpathshala.app.BuildConfig
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Global Uncaught Exception Handler for Sparking Pathshala.
 * Captures, logs, and safely preserves diagnostic crash logs before app termination,
 * preventing silent crashes and assisting rapid bug remediation on diverse Android devices (₹10k to ₹50k).
 */
class AppCrashHandler(
    private val context: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    companion object {
        private const val TAG = "AppCrashHandler"
        private const val CRASH_LOG_FILE = "crash_reports.log"
        private const val MAX_LOG_SIZE_BYTES = 512 * 1024L // 512 KB log cap
        @Volatile private var lastCrashTime = 0L

        fun install(context: Context) {
            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (defaultHandler !is AppCrashHandler) {
                Thread.setDefaultUncaughtExceptionHandler(AppCrashHandler(context.applicationContext, defaultHandler))
            }
        }

        fun getLatestCrashLog(context: Context): String? {
            return try {
                val file = File(context.filesDir, CRASH_LOG_FILE)
                if (file.exists() && file.length() > 0) file.readText() else null
            } catch (_: Exception) {
                null
            }
        }

        fun clearCrashLog(context: Context) {
            try {
                val file = File(context.filesDir, CRASH_LOG_FILE)
                if (file.exists()) file.delete()
            } catch (_: Exception) {}
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            logCrash(thread, throwable)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write crash dump", e)
        }

        val now = System.currentTimeMillis()
        val isCrashLoop = (now - lastCrashTime) < 4000L
        lastCrashTime = now

        // Prevent abrupt app termination: Gracefully recover activity if not in a tight restart loop
        if (!isCrashLoop) {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    putExtra("recovered_from_crash", true)
                }
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                    android.os.Process.killProcess(android.os.Process.myPid())
                    System.exit(10)
                    return
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to launch recovery intent", e)
            }
        }

        // Forward to default platform handler to finish cleanly on repeat crashes
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun logCrash(thread: Thread, throwable: Throwable) {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val stackTrace = sw.toString()

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        val logEntry = buildString {
            appendLine("================ CRASH REPORT ================")
            appendLine("Timestamp: $timestamp")
            appendLine("App Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine("Android OS: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})")
            val threadIdentifier = try {
                thread.name
            } catch (_: Exception) { "unknown" }
            appendLine("Thread: $threadIdentifier")
            appendLine("Exception: ${throwable::class.java.name}: ${throwable.message}")
            appendLine("Stack Trace:")
            appendLine(stackTrace)
            appendLine("==============================================")
            appendLine()
        }

        Log.e(TAG, "Uncaught Exception in thread [${thread.name}]: ${throwable.message}", throwable)

        val logFile = File(context.filesDir, CRASH_LOG_FILE)
        try {
            // Trim file if exceeding maximum allowed size
            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE_BYTES) {
                logFile.delete()
            }
            FileWriter(logFile, true).use { writer ->
                writer.append(logEntry)
            }
        } catch (_: Exception) {}
    }
}
