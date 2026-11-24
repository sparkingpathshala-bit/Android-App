package com.sparkingpathshala.app.data.repository

import android.content.Context
import com.sparkingpathshala.app.data.local.*
import com.sparkingpathshala.app.data.remote.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import retrofit2.HttpException
import org.json.JSONObject
import com.sparkingpathshala.app.security.SecurityUtils

fun isQuizExpired24Hours(scheduledStart: String, createdAt: String? = null): Boolean {
    val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "dd/MM/yyyy HH:mm",
        "dd-MM-yyyy HH:mm",
        "dd/MM/yyyy",
        "yyyy-MM-dd"
    )
    val now = System.currentTimeMillis()
    val twentyFourHoursMs = 24L * 60 * 60 * 1000

    val candidates = listOfNotNull(scheduledStart.ifBlank { null }, createdAt?.ifBlank { null })
    if (candidates.isEmpty()) return false

    for (str in candidates) {
        val trimmed = str.trim()
        for (fmt in formats) {
            try {
                val sdf = java.text.SimpleDateFormat(fmt, java.util.Locale.US).apply {
                    timeZone = if (fmt.contains("'Z'")) java.util.TimeZone.getTimeZone("UTC") else java.util.TimeZone.getTimeZone("Asia/Kolkata")
                    isLenient = false
                }
                val pos = java.text.ParsePosition(0)
                val date = sdf.parse(trimmed, pos)
                if (date != null && pos.index == trimmed.length) {
                    return (now - date.time > twentyFourHoursMs)
                }
            } catch (_: Exception) {}
        }
    }
    return false
}

interface AppRepository {
    fun getUser(): Flow<UserEntity?>
    suspend fun login(email: String, password: String): Result<UserEntity>
    suspend fun signup(name: String, email: String, classSelection: String, mobile: String, password: String): Result<String>
    suspend fun logout()
    suspend fun deleteAccount(): Result<Unit>
    suspend fun updatePassword(current: String, new: String): Result<Unit>

    fun getFees(): Flow<List<FeeEntity>>
    suspend fun syncFees(): Result<Unit>
    suspend fun initializePayment(month: String, amount: Double): Result<String>
    suspend fun verifyPayment(txnRefId: String, utrNumber: String): Result<Unit>
    suspend fun getStudentTransactions(): Result<List<TransactionResponse>>

    fun getResults(): Flow<List<ResultEntity>>
    suspend fun syncResults(): Result<Unit>

    fun getAttendance(): Flow<List<AttendanceEntity>>
    suspend fun syncAttendance(): Result<Unit>

    fun getStudyMaterials(): Flow<List<StudyMaterialEntity>>
    suspend fun syncStudyMaterials(): Result<Unit>
    suspend fun downloadMaterial(context: Context, materialId: String): Result<Unit>
    suspend fun deleteDownloadedMaterial(materialId: String)
    suspend fun deleteDownloadedHomework(homeworkId: String)

    suspend fun requestOnDemand(subject: String, description: String): Result<String>

    fun getQuizzes(): Flow<List<QuizEntity>>
    fun getQuestions(quizId: String): Flow<List<QuestionEntity>>
    suspend fun saveQuestions(questions: List<QuestionEntity>)
    suspend fun deleteQuestionsByQuizId(quizId: String)
    suspend fun syncQuizzes(): Result<Unit>

    suspend fun getStudentDoubts(): Result<List<DoubtResponse>>
    suspend fun getAdminDoubts(): Result<List<DoubtResponse>>
    suspend fun getDoubtReplies(doubtId: String): Result<List<DoubtReplyResponse>>
    suspend fun submitDoubt(title: String, subject: String, message: String, fileName: String?, fileData: String?): Result<Unit>
    suspend fun submitDoubtReply(doubtId: String, message: String, fileName: String?, fileData: String?): Result<Unit>
    suspend fun updateDoubtStatus(doubtId: String, status: String): Result<Unit>
    suspend fun deleteDoubt(doubtId: String): Result<Unit>
    suspend fun submitQuiz(quizId: String, answers: Map<String, String>): Result<SubmitQuizResponse>
    
    suspend fun getAdminStudents(): Result<List<AdminStudentInfo>>
    suspend fun getAdminRecords(type: String): Result<List<AdminRecord>>
    suspend fun addAdminRecord(type: String, data: Map<String, Any>): Result<Unit>
    suspend fun updateAdminRecord(type: String, id: String, data: Map<String, Any>): Result<Unit>
    suspend fun deleteAdminRecord(type: String, id: String): Result<Unit>
    suspend fun uploadStudyMaterial(title: String, category: String, classSelection: String, subject: String, fileName: String, fileSize: String, fileData: String, allowedEmails: String? = null): Result<Unit>
    suspend fun deleteResource(id: String): Result<Unit>
    suspend fun updateLibraryResource(id: String, title: String, category: String, classSelection: String, subject: String, status: String, allowedEmails: String? = null): Result<Unit>
    suspend fun getAdminQuizzes(): Result<List<AdminQuizInfo>>
    suspend fun createManualQuiz(quiz: ManualQuizRequest): Result<Unit>
    suspend fun generateAiQuiz(quiz: AiQuizRequest): Result<Unit>
    suspend fun deleteQuiz(quizId: String): Result<Unit>
    suspend fun launchQuiz(quizId: String): Result<Unit>
    suspend fun launchMultipleQuizzes(quizIds: List<String>): Result<Unit>
    suspend fun deleteMultipleQuizzes(quizIds: List<String>): Result<Unit>
    
    suspend fun getPendingStudents(): Result<List<PendingStudentInfo>>
    suspend fun approveStudent(id: String): Result<Unit>
    suspend fun rejectStudent(id: String): Result<Unit>
    suspend fun deleteStudent(id: String): Result<Unit>
    suspend fun updateStudentStatus(id: String, status: String): Result<Unit>
    suspend fun updateStudent(id: String, data: Map<String, Any>): Result<Unit>
    
    suspend fun getAdminEnquiries(): Result<List<EnquiryInfo>>
    fun listenToEnquiriesStream(onUpdate: () -> Unit): okhttp3.Call?
    suspend fun updateAdminEnquiry(id: String, data: Map<String, Any>): Result<Unit>
    suspend fun deleteAdminEnquiry(id: String): Result<Unit>

    // Homework Management
    fun getHomework(classLevel: String): Flow<List<HomeworkEntity>>
    fun getAllHomework(): Flow<List<HomeworkEntity>>
    suspend fun uploadHomework(
        title: String,
        description: String,
        subject: String,
        classLevel: String,
        topic: String?,
        batch: String?,
        fileName: String,
        fileData: String,
        fileType: String,
        dueDate: String,
        maxMarks: Int
    ): Result<Unit>
    suspend fun deleteHomework(id: String): Result<Unit>
    suspend fun updateHomework(
        id: String,
        title: String,
        description: String,
        subject: String,
        classLevel: String,
        topic: String?,
        batch: String?,
        dueDate: String,
        maxMarks: Int
    ): Result<Unit>
    suspend fun downloadHomework(context: Context, homeworkId: String): Result<Unit>
    suspend fun purgeExpiredHomework(): Int

    // Guest Mode Telemetry
    suspend fun recordGuestTelemetry(
        guestId: String,
        deviceName: String,
        deviceModel: String,
        location: String,
        ipAddress: String
    )
    fun getGuestTelemetryList(): Flow<List<GuestTelemetryEntity>>
    suspend fun purgeExpiredGuests(): Int

    // Notifications Engine
    fun getStudentNotifications(role: String = "student", classLevel: String): Flow<List<NotificationEntity>>
    fun getAdminNotifications(): Flow<List<NotificationEntity>>
    fun getStudentUnreadCount(role: String = "student", classLevel: String): Flow<Int>
    fun getAdminUnreadCount(): Flow<Int>
    suspend fun markNotificationAsRead(id: String)
    suspend fun markAllNotificationsAsRead()
    suspend fun deleteNotification(id: String)
    suspend fun clearAllNotifications()
    suspend fun sendNotification(notification: NotificationEntity)
    suspend fun purgeExpiredNotifications(): Int

    suspend fun loginAsGuest(
        guestId: String,
        deviceName: String,
        deviceModel: String,
        location: String,
        ipAddress: String
    ): Result<UserEntity>

    suspend fun syncHomework(): Result<Unit>
    suspend fun syncNotifications(): Result<Unit>
    suspend fun generatePracticeQuiz(subject: String, numQuestions: Int = 10): Result<ActiveQuizResponse>
    suspend fun getQuizHistory(): Result<List<QuizHistoryItem>>
    suspend fun checkAppVersion(): Result<com.sparkingpathshala.app.data.remote.AppVersionResponse> = Result.success(com.sparkingpathshala.app.data.remote.AppVersionResponse())
}


class AppRepositoryImpl(
    private val db: AppDatabase,
    private val api: ApiService,
    private val okHttpClient: OkHttpClient
) : AppRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val userDao = db.userDao()
    private val feeDao = db.feeDao()
    private val resultDao = db.resultDao()
    private val attendanceDao = db.attendanceDao()
    private val studyMaterialDao = db.studyMaterialDao()
    private val quizDao = db.quizDao()
    private val questionDao = db.questionDao()
    private val homeworkDao = db.homeworkDao()
    private val guestTelemetryDao = db.guestTelemetryDao()
    private val notificationDao = db.notificationDao()
    private val dashboardSyncMutex = kotlinx.coroutines.sync.Mutex()
    private var lastDashboardSyncTime = 0L

    init {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                // Delete legacy announcement with stale 2024 timestamp if present
                notificationDao.deleteNotification("notif_welcome_announcement")

                notificationDao.insertNotificationIgnore(
                    NotificationEntity(
                        id = "notif_welcome_active",
                        recipientRole = "all",
                        targetClass = "All",
                        title = "Welcome to Sparking Pathshala!",
                        message = "Your learning dashboard is active. Access your study material, homework, doubts, and performance reports here.",
                        category = "UPDATE",
                        timestamp = System.currentTimeMillis(),
                        priority = "NORMAL",
                        isRead = false
                    )
                )
            } catch (e: Exception) {
                // Ignore in mocked tests or if DB not yet open
            }
        }
    }

    private fun maskErrorMessage(e: Throwable, fallback: String): String {
        val msg = e.localizedMessage ?: e.message ?: ""
        return when {
            e is java.net.UnknownHostException || msg.contains("Unable to resolve host", ignoreCase = true) || msg.contains("No address associated with hostname", ignoreCase = true) -> {
                "You are currently offline. Displaying saved local materials."
            }
            e is java.net.SocketTimeoutException || msg.contains("timeout", ignoreCase = true) -> {
                "Connection timed out. Please check your network and retry."
            }
            e is HttpException && e.code() == 401 -> {
                "Session expired. Please log in again."
            }
            e is HttpException && e.code() == 404 -> {
                "Requested content was not found."
            }
            msg.isNotBlank() && !msg.contains("api.sparkingpathshala.in") -> {
                msg
            }
            else -> fallback
        }
    }

    private suspend fun getAuthToken(): String {
        val user = userDao.getUserDirect() ?: return ""
        if (user.isGuest() || user.token.isBlank() || user.token.startsWith("guest_")) return ""
        val decryptedToken = com.sparkingpathshala.app.security.SecurityUtils.decrypt(user.token)
        if (decryptedToken.isBlank() || decryptedToken.startsWith("guest_")) return ""
        return "Bearer $decryptedToken"
    }

    override fun getUser(): Flow<UserEntity?> = userDao.getUserFlow().map { user ->
        if (user != null) {
            val maxSessionAgeMs = 7L * 24 * 60 * 60 * 1000L // Exactly 7 days (auto-logout policy)
            val now = System.currentTimeMillis()

            // Heal uninitialized or legacy timestamp (e.g. 0L from pre-migration records)
            val effectiveTimestamp = if (user.loginTimestamp <= 0L) {
                repositoryScope.launch {
                    try {
                        userDao.insertUser(user.copy(loginTimestamp = now))
                    } catch (_: Exception) {}
                }
                now
            } else {
                user.loginTimestamp
            }

            if (user.role != "guest" && (now - effectiveTimestamp > maxSessionAgeMs)) {
                // Clear DB session strictly after 7 days of token issuance
                repositoryScope.launch {
                    logout()
                }
                null
            } else {
                user.copy(
                    loginTimestamp = effectiveTimestamp,
                    token = com.sparkingpathshala.app.security.SecurityUtils.decrypt(user.token)
                )
            }
        } else {
            null
        }
    }

    override suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val cleanIdentifier = email.trim()
            val cleanPassword = password.trim()
            val response = api.login(LoginRequest(cleanIdentifier, cleanPassword))
            val status = response.user.status.lowercase()
            if (status == "pending_approval" || status == "pending") {
                return@withContext Result.failure(Exception("Your registration request is pending admin approval. You will be able to log in once the admin approves your account."))
            } else if (status == "inactive") {
                return@withContext Result.failure(Exception("Your account is locked or inactive. Please contact the administrator."))
            } else if (status == "rejected") {
                return@withContext Result.failure(Exception("Your registration request has been rejected. Please contact support."))
            }
            val userEntity = UserEntity(
                id = response.user.id,
                name = response.user.name,
                email = response.user.email,
                rollNo = "SP-${response.user.id}",
                classLevel = response.user.classSelection,
                token = com.sparkingpathshala.app.security.SecurityUtils.encrypt(response.token),
                role = response.user.role,
                loginTimestamp = System.currentTimeMillis()
            )
            userDao.clearUser()
            userDao.insertUser(userEntity)
            Result.success(userEntity.copy(token = response.token)) // Return decrypted token to caller memory
        } catch (e: HttpException) {
            val errorMsg = try {
                val errorBody = e.response()?.errorBody()?.string()
                val json = JSONObject(errorBody ?: "")
                json.optString("error", json.optString("message", "Invalid credentials. Please check email/mobile and password."))
            } catch (ex: Exception) {
                "Invalid credentials. Please check your details and try again."
            }
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Result.failure(Exception(maskErrorMessage(e, "Login failed. Please check your connection.")))
        }
    }

    override suspend fun signup(
        name: String,
        email: String,
        classSelection: String,
        mobile: String,
        password: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val cleanName = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(name, 100)
            val cleanEmail = com.sparkingpathshala.app.security.SecurityUtils.sanitizeEmail(email)
            val cleanClass = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(classSelection, 30)
            val cleanMobile = com.sparkingpathshala.app.security.SecurityUtils.sanitizeMobile(mobile)
            val cleanPassword = password.trim()

            val response = api.signup(SignupRequest(cleanName, cleanEmail, cleanClass, cleanMobile, cleanPassword))

            // Trigger admin notification for new signup
            val notif = NotificationEntity(
                id = "notif_reg_${System.currentTimeMillis()}",
                recipientRole = "admin",
                targetClass = "All",
                title = "New Student Registration",
                message = "$cleanName ($cleanClass) has registered and is pending approval.",
                category = "SIGNUP",
                timestamp = System.currentTimeMillis(),
                priority = "HIGH"
            )
            notificationDao.insertNotification(notif)

            Result.success(response.message)
        } catch (e: HttpException) {
            val errorMsg = try {
                val errorBody = e.response()?.errorBody()?.string()
                val json = JSONObject(errorBody ?: "")
                json.optString("error", json.optString("message", "Registration failed. Please check your inputs."))
            } catch (ex: Exception) {
                "Registration failed. Please check your details."
            }
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Result.failure(Exception(maskErrorMessage(e, "Registration failed. Please check your connection.")))
        }
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.clearUser()
        feeDao.clearFees()
        resultDao.clearResults()
        attendanceDao.clearAttendance()
        studyMaterialDao.clearNonDownloadedMaterials()
        quizDao.clearQuizzes()
        questionDao.clearQuestions()
        homeworkDao.clearNonDownloadedHomework()
        notificationDao.clearNotifications()
    }

    override suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.deleteAccount(token)
            logout()
            Result.success(Unit)
        } catch (e: HttpException) {
            val errorMsg = try {
                val errorBody = e.response()?.errorBody()?.string()
                val json = JSONObject(errorBody ?: "")
                json.optString("error", json.optString("message", "Account deletion failed"))
            } catch (ex: Exception) {
                "Account deletion failed: ${e.message()}"
            }
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(current: String, new: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.updatePassword(token, UpdatePasswordRequest(current, new))
            Result.success(Unit)
        } catch (e: HttpException) {
            val errorMsg = try {
                val errorBody = e.response()?.errorBody()?.string()
                val json = JSONObject(errorBody ?: "")
                json.optString("error", json.optString("message", "Password update failed"))
            } catch (ex: Exception) {
                "Password update failed: ${e.message()}"
            }
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun <T> retryWithExponentialBackoff(
        times: Int = 3,
        initialDelayMs: Long = 500,
        maxDelayMs: Long = 2000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMs
        repeat(times - 1) {
            try {
                return block()
            } catch (e: Exception) {
                if (e is HttpException && e.code() in 400..499) {
                    throw e
                }
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }
                kotlinx.coroutines.delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
            }
        }
        return block()
    }

    private suspend fun syncDashboard(): Result<Unit> = withContext(Dispatchers.IO) {
        dashboardSyncMutex.withLock {
            val now = System.currentTimeMillis()
            if (now - lastDashboardSyncTime < 3000L) {
                // Debounce rapid duplicate calls within 3 seconds
                return@withContext Result.success(Unit)
            }
            try {
                val token = getAuthToken()
                if (token.isEmpty()) return@withContext Result.success(Unit)

                val response = retryWithExponentialBackoff { api.getStudentDashboard(token) }

                // Sync Fees atomically
                val fees = response.fees.map {
                    FeeEntity(
                        id = it.id,
                        title = "${it.month} School Fee",
                        amount = it.totalDue,
                        dueDate = it.paymentDate,
                        isPaid = it.status.equals("Paid", ignoreCase = true),
                        paidDate = it.paymentDate,
                        receiptUrl = it.transactionRefNo ?: "",
                        status = it.status,
                        month = it.month,
                        amountPaid = it.amountPaid,
                        adjustedAmount = it.adjustedAmount,
                        remarks = it.remarks,
                        paymentMode = it.paymentMode
                    )
                }
                feeDao.replaceAllFees(fees)

                // Sync Results atomically
                val results = response.results.map {
                    ResultEntity(
                        id = it.id,
                        subject = it.subject,
                        marksObtained = it.marksObtained,
                        maxMarks = it.maxMarks,
                        examType = "Evaluation",
                        date = it.examDate
                    )
                }
                resultDao.replaceAllResults(results)

                // Sync Attendance atomically
                val attendance = response.attendance.map {
                    AttendanceEntity(
                        date = it.date,
                        isPresent = it.status.equals("Present", ignoreCase = true),
                        remarks = it.status
                    )
                }
                attendanceDao.replaceAllAttendance(attendance)

                lastDashboardSyncTime = System.currentTimeMillis()
                Result.success(Unit)
            } catch (e: Exception) {
                if (e is HttpException && e.code() == 401) {
                    try {
                        logout()
                    } catch (logoutEx: Exception) {
                        // Ignore
                    }
                    Result.failure(Exception("Session expired. Please log in again."))
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    override fun getFees(): Flow<List<FeeEntity>> = feeDao.getFeesFlow()

    override suspend fun syncFees(): Result<Unit> = syncDashboard()

    override fun getResults(): Flow<List<ResultEntity>> = resultDao.getResultsFlow()

    override suspend fun syncResults(): Result<Unit> = syncDashboard()

    override fun getAttendance(): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceFlow()

    override suspend fun syncAttendance(): Result<Unit> = syncDashboard()

    override fun getStudyMaterials(): Flow<List<StudyMaterialEntity>> = studyMaterialDao.getAllMaterialsFlow()

    override suspend fun syncStudyMaterials(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            val user = userDao.getUserDirect()
            val allResources = if (user.isAdmin() && token.isNotEmpty()) {
                try { retryWithExponentialBackoff { api.getAllLibraryResourcesAdmin(token) }.resources } catch (e: Exception) { emptyList() }
            } else {
                val guides = try { retryWithExponentialBackoff { api.getLibraryResources(token, "guides") }.resources } catch (e: Exception) { emptyList() }
                val qBanks = try { retryWithExponentialBackoff { api.getLibraryResources(token, "question-bank") }.resources } catch (e: Exception) { emptyList() }
                val testSeries = try { retryWithExponentialBackoff { api.getLibraryResources(token, "test-series") }.resources } catch (e: Exception) { emptyList() }
                val notes = try { retryWithExponentialBackoff { api.getLibraryResources(token, "notes") }.resources } catch (e: Exception) { emptyList() }
                val premium = if (token.isNotEmpty()) {
                    try { retryWithExponentialBackoff { api.getLibraryResources(token, "premium-material") }.resources } catch (e: Exception) { emptyList() }
                } else emptyList()
                guides + qBanks + testSeries + notes + premium
            }

            if (allResources.isNotEmpty()) {
                val downloadedMap = studyMaterialDao.getAllDownloadedMaterials().associateBy { it.id }
                val materials = allResources.map { res ->
                    val existing = downloadedMap[res.id]
                    val fileExists = existing != null && existing.localPath.isNotBlank() && java.io.File(existing.localPath).exists()
                    StudyMaterialEntity(
                        id = res.id,
                        title = res.title,
                        description = "${res.subject} Material - Category: ${res.category}",
                        subject = res.subject,
                        fileUrl = "api/library/resources/${res.id}/download",
                        localPath = if (fileExists) (existing?.localPath ?: "") else "",
                        isDownloaded = fileExists,
                        classLevel = res.classSelection,
                        category = res.category,
                        status = res.status ?: "active",
                        allowedEmails = res.allowedEmails
                    )
                }
                studyMaterialDao.replaceMaterials(materials)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadMaterial(context: Context, materialId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))

            val material = studyMaterialDao.getMaterialById(materialId)
                ?: return@withContext Result.failure(Exception("Material not found in database"))

            val response = api.downloadResource(token, materialId)
            val base64Data = response.fileData

            val cleanBase64 = if (base64Data.contains(",")) {
                base64Data.substring(base64Data.indexOf(",") + 1)
            } else {
                base64Data
            }

            val fileBytes = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT)

            val downloadsDir = File(context.filesDir, "downloads")
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val targetFile = File(downloadsDir, "material_$materialId.pdf")
            FileOutputStream(targetFile).use { output ->
                output.write(fileBytes)
            }

            val updated = material.copy(
                localPath = targetFile.absolutePath,
                isDownloaded = true
            )
            studyMaterialDao.updateMaterial(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDownloadedMaterial(materialId: String) = withContext(Dispatchers.IO) {
        val material = studyMaterialDao.getMaterialById(materialId)
        if (material != null && material.isDownloaded) {
            val file = File(material.localPath)
            if (file.exists()) {
                file.delete()
            }
            val updated = material.copy(
                localPath = "",
                isDownloaded = false
            )
            studyMaterialDao.updateMaterial(updated)
        }
    }

    override suspend fun deleteDownloadedHomework(homeworkId: String) = withContext(Dispatchers.IO) {
        val hw = homeworkDao.getHomeworkById(homeworkId)
        if (hw != null && hw.isDownloaded) {
            if (hw.localPath.isNotBlank()) {
                val file = File(hw.localPath)
                if (file.exists()) file.delete()
            }
            homeworkDao.updateHomework(hw.copy(localPath = "", isDownloaded = false))
        }
    }

    override suspend fun requestOnDemand(subject: String, description: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))

            val response = api.submitDoubt(
                token = token,
                request = DoubtRequest(
                    title = "On-Demand Study Material Request",
                    subject = subject,
                    message = description
                )
            )
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getQuizzes(): Flow<List<QuizEntity>> = quizDao.getQuizzesFlow()

    override fun getQuestions(quizId: String): Flow<List<QuestionEntity>> = questionDao.getQuestionsFlow(quizId)

    override suspend fun saveQuestions(questions: List<QuestionEntity>) {
        questionDao.insertQuestions(questions)
    }

    override suspend fun deleteQuestionsByQuizId(quizId: String) {
        questionDao.deleteQuestionsByQuizId(quizId)
    }

    override suspend fun syncQuizzes(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.success(Unit)

            try {
                val response = retryWithExponentialBackoff { api.getActiveQuiz(token) }

                val quiz = QuizEntity(
                    id = response.quiz.id,
                    title = "${response.quiz.chapter} Quiz",
                    description = "Active ${response.quiz.quizType} slot quiz for ${response.quiz.subject}. Scheduled start: ${response.quiz.scheduledStart}",
                    subject = response.quiz.subject,
                    numQuestions = response.questions.size,
                    classLevel = response.quiz.classSelection,
                    attempted = response.attempted ?: false,
                    score = response.score,
                    totalQuestions = response.totalQuestions
                )

                val questions = response.questions.map {
                    QuestionEntity(
                        id = it.id,
                        quizId = it.quizId,
                        questionText = it.questionText,
                        optionA = it.optionA,
                        optionB = it.optionB,
                        optionC = it.optionC,
                        optionD = it.optionD,
                        correctAnswer = when (it.correctOption?.uppercase()) {
                            "A" -> 0
                            "B" -> 1
                            "C" -> 2
                            "D" -> 3
                            else -> 0
                        },
                        explanation = it.explanation ?: ""
                    )
                }
                quizDao.replaceAllQuizzes(listOf(quiz))
                questionDao.replaceQuestions(response.quiz.id, questions)
                Result.success(Unit)
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 404 || e.code() == 401) {
                    // No active quiz found or unauthenticated session.
                    quizDao.clearQuizzes()
                    questionDao.clearQuestions()
                    Result.success(Unit)
                } else {
                    Result.failure(e)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStudentDoubts(): Result<List<DoubtResponse>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val response = api.getStudentDoubts(token)
            Result.success(response.doubts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAdminDoubts(): Result<List<DoubtResponse>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val response = api.getAdminDoubts(token)
            Result.success(response.doubts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDoubtReplies(doubtId: String): Result<List<DoubtReplyResponse>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val response = api.getDoubtReplies(token, doubtId)
            Result.success(response.replies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitDoubt(
        title: String,
        subject: String,
        message: String,
        fileName: String?,
        fileData: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val cleanTitle = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(title, 100)
            val cleanSubject = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(subject, 50)
            val cleanMessage = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(message, 1000)

            api.submitDoubt(
                token = token,
                request = DoubtRequest(cleanTitle, cleanSubject, cleanMessage, fileName, fileData)
            )

            // Trigger notification for Admin
            val user = userDao.getUserDirect()
            val studentName = user?.name ?: "A Student"
            val studentClass = user?.classLevel ?: "All"
            val notif = NotificationEntity(
                id = "notif_doubt_${System.currentTimeMillis()}",
                recipientRole = "admin",
                targetClass = "All",
                title = "New Doubt: $cleanTitle",
                message = "$studentName ($studentClass) asked: \"$cleanMessage\"",
                category = "DOUBT",
                timestamp = System.currentTimeMillis(),
                priority = "HIGH"
            )
            notificationDao.insertNotification(notif)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitDoubtReply(
        doubtId: String,
        message: String,
        fileName: String?,
        fileData: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val cleanMessage = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(message, 1000)

            api.submitDoubtReply(
                token = token,
                doubtId = doubtId,
                request = DoubtReplyRequest(cleanMessage, fileName, fileData)
            )

            val currentUser = userDao.getUserDirect()
            val isAdmin = currentUser.isAdmin()

            if (isAdmin) {
                // Admin replied -> Notify Student(s)
                val notif = NotificationEntity(
                    id = "notif_rep_${System.currentTimeMillis()}",
                    recipientRole = "student",
                    targetClass = "All",
                    title = "Faculty Replied to Doubt",
                    message = "Faculty Sajjan Mallik replied: \"$cleanMessage\"",
                    category = "DOUBT",
                    timestamp = System.currentTimeMillis(),
                    priority = "HIGH"
                )
                notificationDao.insertNotification(notif)
            } else {
                // Student replied -> Notify Admin
                val studentName = currentUser?.name ?: "Student"
                val studentClass = currentUser?.classLevel ?: "All"
                val notif = NotificationEntity(
                    id = "notif_rep_${System.currentTimeMillis()}",
                    recipientRole = "admin",
                    targetClass = "All",
                    title = "Student Follow-up on Doubt",
                    message = "$studentName ($studentClass) replied: \"$cleanMessage\"",
                    category = "DOUBT",
                    timestamp = System.currentTimeMillis(),
                    priority = "NORMAL"
                )
                notificationDao.insertNotification(notif)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDoubtStatus(doubtId: String, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.updateDoubtStatus(
                token = token,
                doubtId = doubtId,
                request = UpdateDoubtStatusRequest(status)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDoubt(doubtId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.deleteDoubt(token, doubtId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun gradeQuizLocally(quizId: String, answers: Map<String, String>): SubmitQuizResponse {
        val dbQuestionsByQuiz = questionDao.getQuestionsFlow(quizId).first()
        val dbQuestionsByIds = if (dbQuestionsByQuiz.size < answers.size) {
            questionDao.getQuestionsByIds(answers.keys.toList())
        } else {
            emptyList()
        }
        val dbEntities = (dbQuestionsByIds + dbQuestionsByQuiz).associateBy { it.id }.values.toList()

        val allEntities = QuestionBank.questions.values.flatten() +
                QuestionBank.primarySubjectQuestions.values.flatten() +
                QuestionBank.secondarySubjectQuestions.values.flatten() +
                QuestionBank.seniorSubjectQuestions.values.flatten()
        val oldPracticeEntities = listOf(
            QuestionEntity("p1", "practice_quiz", "What is the focal length of a flat plane mirror?", "0", "Infinity", "10 cm", "-10 cm", 1, "A plane mirror has an infinite focal length because it doesn't converge or diverge rays."),
            QuestionEntity("p2", "practice_quiz", "Which lens is used to correct myopia?", "Convex", "Concave", "Bifocal", "Cylindrical", 1, "Myopia (short-sightedness) requires a diverging concave lens to bring the image onto the retina."),
            QuestionEntity("p3", "practice_quiz", "The speed of light in vacuum is approximately:", "3 x 10^5 m/s", "3 x 10^8 m/s", "3 x 10^6 m/s", "1.5 x 10^8 m/s", 1, "Light travels at approximately 300,000,000 meters per second (3 x 10^8 m/s) in a vacuum."),
            QuestionEntity("p4", "practice_quiz", "Which color of light deviates the most when passing through a prism?", "Red", "Yellow", "Violet", "Green", 2, "Violet light has the shortest wavelength in the visible spectrum and deviates the most."),
            QuestionEntity("p5", "practice_quiz", "SI unit of power of a lens is:", "Meter", "Dioptre", "Watt", "Decibel", 1, "The power of a lens is measured in Dioptres (D), which is the reciprocal of the focal length in meters.")
        )
        val combined = (allEntities + oldPracticeEntities + dbEntities)
            .associateBy { it.id }.values
        val responses = combined.map { entity ->
            val correctOptStr = when (entity.correctAnswer) {
                0 -> "A"
                1 -> "B"
                2 -> "C"
                3 -> "D"
                else -> "A"
            }
            val correctText = when (entity.correctAnswer) {
                0 -> entity.optionA
                1 -> entity.optionB
                2 -> entity.optionC
                3 -> entity.optionD
                else -> entity.optionA
            }
            val cleanExplanation = if (entity.explanation.isBlank() ||
                entity.explanation.contains("Open Trivia", ignoreCase = true) ||
                entity.explanation.equals("N/A", ignoreCase = true) ||
                entity.explanation.contains("Category:", ignoreCase = true)) {
                "Option ($correctOptStr) \"$correctText\" is the verified correct answer."
            } else {
                entity.explanation
            }
            QuestionResponse(
                id = entity.id,
                quizId = entity.quizId,
                questionText = entity.questionText,
                optionA = entity.optionA,
                optionB = entity.optionB,
                optionC = entity.optionC,
                optionD = entity.optionD,
                correctOption = correctOptStr,
                explanation = cleanExplanation
            )
        }
        val questions = responses.filter { it.id in answers.keys }
        var score = 0
        for (q in questions) {
            val ans = answers[q.id]
            if (ans != null && ans.uppercase() == q.correctOption?.uppercase()) {
                score++
            }
        }
        return SubmitQuizResponse(
            message = "Quiz graded successfully!",
            score = score,
            totalQuestions = if (questions.isNotEmpty()) questions.size else answers.size,
            questions = questions
        )
    }

    override suspend fun submitQuiz(quizId: String, answers: Map<String, String>): Result<SubmitQuizResponse> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            val isGuest = token.isEmpty()

            val response = if (isGuest || quizId == "practice_quiz" || quizId.startsWith("practice_")) {
                gradeQuizLocally(quizId, answers)
            } else {
                try {
                    api.submitQuiz(
                        token = token,
                        request = SubmitQuizRequest(quizId, answers)
                    )
                } catch (e: Exception) {
                    gradeQuizLocally(quizId, answers)
                }
            }

            // Cache the graded questions in local DB so that review screen has them
            val entities = response.questions.map { res ->
                val correctIndex = when (res.correctOption?.uppercase()) {
                    "A" -> 0
                    "B" -> 1
                    "C" -> 2
                    "D" -> 3
                    else -> 0
                }
                val correctText = when (correctIndex) {
                    0 -> res.optionA
                    1 -> res.optionB
                    2 -> res.optionC
                    3 -> res.optionD
                    else -> res.optionA
                }
                val cleanExpl = if (res.explanation.isNullOrBlank() ||
                    res.explanation.contains("Open Trivia", ignoreCase = true) ||
                    res.explanation.equals("N/A", ignoreCase = true) ||
                    res.explanation.contains("Category:", ignoreCase = true)) {
                    "Option (${res.correctOption ?: "A"}) \"$correctText\" is the verified correct answer."
                } else {
                    res.explanation
                }
                QuestionEntity(
                    id = res.id,
                    quizId = res.quizId,
                    questionText = res.questionText,
                    optionA = res.optionA,
                    optionB = res.optionB,
                    optionC = res.optionC,
                    optionD = res.optionD,
                    correctAnswer = correctIndex,
                    explanation = cleanExpl
                )
            }
            if (entities.isNotEmpty()) {
                questionDao.insertQuestions(entities)
            }

            // Also mark as attempted locally to ensure instant vanishing from dashboard
            db.quizDao().updateQuizResult(quizId, true, response.score)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAdminStudents(): Result<List<AdminStudentInfo>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val response = api.getAdminStudents(token)
            Result.success(response.students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAdminRecords(type: String): Result<List<AdminRecord>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val response = api.getAdminRecords(token, type)
            Result.success(response.records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addAdminRecord(type: String, data: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.addAdminRecord(token, type, data)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAdminRecord(type: String, id: String, data: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.updateAdminRecord(token, type, id, data)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAdminRecord(type: String, id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.deleteAdminRecord(token, type, id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadStudyMaterial(
        title: String,
        category: String,
        classSelection: String,
        subject: String,
        fileName: String,
        fileSize: String,
        fileData: String,
        allowedEmails: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val payload = mutableMapOf<String, Any>(
                "title" to title,
                "category" to category,
                "classSelection" to classSelection,
                "subject" to subject,
                "fileName" to fileName,
                "fileSize" to fileSize,
                "fileData" to fileData
            )
            if (allowedEmails != null) {
                payload["allowedEmails"] = allowedEmails
            }
            api.uploadStudyMaterial(token, payload)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
 
    override suspend fun deleteResource(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.deleteResource(token, id)
            // Also delete from local db if it's cached
            db.studyMaterialDao().deleteMaterial(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
 
    override suspend fun updateLibraryResource(
        id: String,
        title: String,
        category: String,
        classSelection: String,
        subject: String,
        status: String,
        allowedEmails: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val payload = mutableMapOf<String, Any>(
                "title" to title,
                "category" to category,
                "classSelection" to classSelection,
                "subject" to subject,
                "status" to status
            )
            if (allowedEmails != null) {
                payload["allowedEmails"] = allowedEmails
            }
            api.updateLibraryResource(token, id, payload)
            val existing = db.studyMaterialDao().getMaterialById(id)
            if (existing != null) {
                db.studyMaterialDao().updateMaterial(
                    existing.copy(
                        title = title,
                        category = category,
                        classLevel = classSelection,
                        subject = subject,
                        status = status,
                        allowedEmails = allowedEmails
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAdminQuizzes(): Result<List<AdminQuizInfo>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isNotEmpty()) {
                try {
                    val response = api.getAdminQuizzes(token)
                    if (response.quizzes.isNotEmpty()) {
                        val activeQuizzes = response.quizzes.filterNot { isQuizExpired24Hours(it.scheduledStart, it.createdAt) }
                        response.quizzes.filter { isQuizExpired24Hours(it.scheduledStart, it.createdAt) }.forEach {
                            db.quizDao().deleteQuiz(it.id)
                        }
                        return@withContext Result.success(activeQuizzes)
                    }
                } catch (e: Exception) {
                    // Fall back to local database
                }
            }
            val localQuizzes = db.quizDao().getAllQuizzes()
            val nowTimeStr = SecurityUtils.formatIndianDate(System.currentTimeMillis())
            val adminQuizList = localQuizzes
                .filterNot { _ ->
                    isQuizExpired24Hours("2026-08-18 14:00", "2026-08-18")
                }
                .map { q ->
                    AdminQuizInfo(
                        id = q.id,
                        subject = q.subject,
                        classSelection = q.classLevel,
                        chapter = q.title.substringAfter(" - ", q.title),
                        quizType = "30 Min Quiz",
                        scheduledStart = nowTimeStr,
                        isManual = 1,
                        createdAt = "2026-08-18",
                        isLaunched = q.attempted
                    )
                }
            Result.success(adminQuizList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createManualQuiz(quiz: ManualQuizRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Save immediately in local database
            val quizId = "quiz_man_${System.currentTimeMillis()}"
            val questionEntities = quiz.questions.mapIndexed { idx, q ->
                QuestionEntity(
                    id = "q_man_${quizId}_$idx",
                    quizId = quizId,
                    questionText = q.questionText,
                    optionA = q.optionA,
                    optionB = q.optionB,
                    optionC = q.optionC,
                    optionD = q.optionD,
                    correctAnswer = when (q.correctOption.uppercase()) {
                        "A" -> 0
                        "B" -> 1
                        "C" -> 2
                        "D" -> 3
                        else -> 0
                    },
                    explanation = q.explanation ?: ""
                )
            }
            val quizEntity = QuizEntity(
                id = quizId,
                title = "${quiz.subject} - ${quiz.chapter}",
                description = "${quiz.quizType} for ${quiz.classSelection} (${quiz.subject})",
                classLevel = quiz.classSelection,
                subject = quiz.subject,
                numQuestions = questionEntities.size
            )
            db.quizDao().insertQuizzes(listOf(quizEntity))
            db.questionDao().insertQuestions(questionEntities)

            // Asynchronously attempt sync with remote backend
            val token = getAuthToken()
            if (token.isNotEmpty()) {
                try {
                    api.createManualQuiz(token, quiz)
                } catch (e: Exception) {
                    // Safe ignore if remote backend is unreachable
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateAiQuiz(quiz: AiQuizRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val quizId = "quiz_ai_${System.currentTimeMillis()}"
            val bankQuestions = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions(quiz.classSelection, quiz.subject, 10)
            val finalQuestions = if (bankQuestions.isNotEmpty()) {
                bankQuestions.mapIndexed { idx, q ->
                    q.copy(id = "q_ai_${quizId}_$idx", quizId = quizId)
                }
            } else {
                listOf(
                    QuestionEntity(
                        id = "q_ai_${quizId}_0",
                        quizId = quizId,
                        questionText = "Which planet is known as the Red Planet in our solar system?",
                        optionA = "Venus",
                        optionB = "Mars",
                        optionC = "Jupiter",
                        optionD = "Saturn",
                        correctAnswer = 1,
                        explanation = "Mars appears reddish due to iron oxide on its surface."
                    )
                )
            }
            val quizEntity = QuizEntity(
                id = quizId,
                title = "${quiz.subject} - ${quiz.chapter}",
                description = "${quiz.quizType} for ${quiz.classSelection} (${quiz.subject})",
                classLevel = quiz.classSelection,
                subject = quiz.subject,
                numQuestions = finalQuestions.size
            )
            db.quizDao().insertQuizzes(listOf(quizEntity))
            db.questionDao().insertQuestions(finalQuestions)

            // Attempt sync with remote backend with short timeout so local creation never hangs
            val token = getAuthToken()
            if (token.isNotEmpty()) {
                try {
                    kotlinx.coroutines.withTimeoutOrNull(4000L) {
                        api.generateAiQuiz(token, quiz)
                    }
                } catch (e: Exception) {
                    // Safe ignore if remote backend is unreachable or timed out
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteQuiz(quizId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.quizDao().deleteQuiz(quizId)
            db.questionDao().deleteQuestionsByQuizId(quizId)
            val token = getAuthToken()
            if (token.isNotEmpty()) {
                try {
                    api.deleteQuiz(token, quizId)
                } catch (e: Exception) {
                    // Safe ignore
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMultipleQuizzes(quizIds: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            quizIds.forEach { id ->
                db.quizDao().deleteQuiz(id)
                db.questionDao().deleteQuestionsByQuizId(id)
            }
            val token = getAuthToken()
            if (token.isNotEmpty()) {
                supervisorScope {
                    quizIds.map { id ->
                        async {
                            try {
                                api.deleteQuiz(token, id)
                            } catch (e: Exception) {
                                // Ignore
                            }
                        }
                    }.awaitAll()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun launchQuiz(quizId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.quizDao().updateQuizResult(quizId, true, 0)
            val token = getAuthToken()
            if (token.isNotEmpty()) {
                try {
                    api.launchQuiz(token, quizId)
                } catch (e: Exception) {
                    // Safe ignore
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun launchMultipleQuizzes(quizIds: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            quizIds.forEach { id ->
                db.quizDao().updateQuizResult(id, true, 0)
            }
            val token = getAuthToken()
            if (token.isNotEmpty()) {
                try {
                    api.launchMultipleQuizzes(token, LaunchMultipleQuizzesRequest(quizIds))
                } catch (e: Exception) {
                    // Safe ignore
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPendingStudents(): Result<List<PendingStudentInfo>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val response = api.getPendingStudents(token)
            Result.success(response.students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun approveStudent(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.approveStudent(token, id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectStudent(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.rejectStudent(token, id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteStudent(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.deleteStudent(token, id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStudentStatus(id: String, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.updateStudentStatus(token, id, mapOf("status" to status))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStudent(id: String, data: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.updateStudent(token, id, data)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initializePayment(month: String, amount: Double): Result<String> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val response = api.initializePayment(token, InitializePaymentRequest(month, amount))
            Result.success(response.txnRefId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyPayment(txnRefId: String, utrNumber: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.verifyPayment(token, VerifyPaymentRequest(txnRefId, utrNumber))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStudentTransactions(): Result<List<TransactionResponse>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val response = api.getStudentTransactions(token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAdminEnquiries(): Result<List<EnquiryInfo>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val responseBody = api.getEnquiries(token).string()
            val clean = responseBody.trim()
            val gson = com.google.gson.Gson()
            val list = if (clean.startsWith("[")) {
                val listType = object : com.google.gson.reflect.TypeToken<List<EnquiryInfo>>() {}.type
                gson.fromJson<List<EnquiryInfo>>(clean, listType)
            } else {
                val wrapper = gson.fromJson(clean, EnquiryResponse::class.java)
                wrapper.enquiries
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAdminEnquiry(id: String, data: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            val statusVal = (data["status"] as? String)?.lowercase() ?: "pending"
            val finalStatus = when (statusVal) {
                "pending" -> "pending"
                "contacted", "admitted" -> "contacted"
                "ignored", "rejected" -> "ignored"
                else -> "pending"
            }
            val payload = mutableMapOf<String, Any>()
            payload["status"] = finalStatus
            if (data.containsKey("remarks")) {
                payload["remarks"] = data["remarks"] ?: ""
            }
            api.updateEnquiry(token, id, payload)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAdminEnquiry(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.failure(Exception("Not authenticated"))
            api.deleteEnquiry(token, id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun listenToEnquiriesStream(onUpdate: () -> Unit): okhttp3.Call? {
        val sseClient = okHttpClient.newBuilder()
            .readTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build()

        var call: okhttp3.Call? = null
        val job = repositoryScope.launch {
            try {
                val token = getAuthToken()
                val requestBuilder = okhttp3.Request.Builder()
                    .url("https://api.sparkingpathshala.in/api/enquiries/stream")
                    .header("Accept", "text/event-stream")
                if (token.isNotBlank()) {
                    requestBuilder.header("Authorization", token)
                }
                val localCall = sseClient.newCall(requestBuilder.build())
                call = localCall
                localCall.execute().use { response ->
                    if (response.isSuccessful) {
                        val source = response.body?.source()
                        if (source != null) {
                            while (!source.exhausted()) {
                                val line = source.readUtf8Line()
                                if (line != null && line.trim() == "data: reload") {
                                    withContext(Dispatchers.Main) {
                                        onUpdate()
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (e !is java.io.InterruptedIOException && e !is java.net.SocketException) {
                    e.printStackTrace()
                }
            }
        }
        return object : okhttp3.Call {
            override fun cancel() {
                call?.cancel()
                job.cancel()
            }
            override fun clone(): okhttp3.Call = call?.clone() ?: okHttpClient.newCall(okhttp3.Request.Builder().url("https://api.sparkingpathshala.in/api/enquiries/stream").build())
            override fun enqueue(responseCallback: okhttp3.Callback) { call?.enqueue(responseCallback) }
            override fun execute(): okhttp3.Response = call?.execute() ?: throw java.io.IOException("Call not initialized")
            override fun isCanceled(): Boolean = call?.isCanceled() ?: job.isCancelled
            override fun isExecuted(): Boolean = call?.isExecuted() ?: false
            override fun request(): okhttp3.Request = call?.request() ?: okhttp3.Request.Builder().url("https://api.sparkingpathshala.in/api/enquiries/stream").build()
            override fun timeout(): okio.Timeout = call?.timeout() ?: okio.Timeout.NONE
        }
    }

    // --- Homework Management Implementation ---

    override fun getHomework(classLevel: String): Flow<List<HomeworkEntity>> {
        return homeworkDao.getHomeworkByClassFlow(classLevel)
    }

    override fun getAllHomework(): Flow<List<HomeworkEntity>> {
        return homeworkDao.getAllHomeworkFlow()
    }

    override suspend fun uploadHomework(
        title: String,
        description: String,
        subject: String,
        classLevel: String,
        topic: String?,
        batch: String?,
        fileName: String,
        fileData: String,
        fileType: String,
        dueDate: String,
        maxMarks: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val sanitizedTitle = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(title, 100)
            val sanitizedDesc = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(description, 500)
            val sanitizedSubject = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(subject, 50)
            val sanitizedClass = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(classLevel, 30)
            val sanitizedTopic = topic?.let { com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(it, 100) }
            val sanitizedBatch = batch?.let { com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(it, 50) }

            val homeworkId = "hw_${System.currentTimeMillis()}_${(1000..9999).random()}"
            val entity = HomeworkEntity(
                id = homeworkId,
                title = sanitizedTitle,
                description = sanitizedDesc,
                subject = sanitizedSubject,
                classLevel = sanitizedClass,
                topic = sanitizedTopic,
                batch = sanitizedBatch,
                fileUrl = if (fileData.isNotEmpty()) "data:${fileType.lowercase()};base64,$fileData" else "",
                fileType = fileType.uppercase(),
                createdAt = System.currentTimeMillis(),
                dueDate = dueDate,
                maxMarks = maxMarks
            )
            homeworkDao.insertSingleHomework(entity)

            val token = getAuthToken()
            if (token.isNotEmpty()) {
                runCatching {
                    api.createHomework(
                        token,
                        CreateHomeworkRequest(
                            classLevel = sanitizedClass,
                            subject = sanitizedSubject,
                            topic = sanitizedTopic,
                            questionText = sanitizedDesc,
                            imageName = fileName.ifBlank { null },
                            imageData = fileData.ifBlank { null },
                            assignedDate = dueDate
                        )
                    )
                }
            }

            // Trigger notification for this class
            val notif = NotificationEntity(
                id = "notif_hw_${System.currentTimeMillis()}",
                recipientRole = "student",
                targetClass = sanitizedClass,
                title = "New Homework: $sanitizedTitle",
                message = "New $sanitizedSubject homework has been assigned for $sanitizedClass.",
                category = "HOMEWORK",
                timestamp = System.currentTimeMillis(),
                priority = "HIGH"
            )
            notificationDao.insertNotification(notif)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(maskErrorMessage(e, "Failed to upload homework")))
        }
    }

    override suspend fun deleteHomework(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isNotEmpty()) {
                runCatching { api.deleteHomework(token, id) }
            }
            homeworkDao.deleteHomework(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(maskErrorMessage(e, "Failed to delete homework")))
        }
    }

    override suspend fun updateHomework(
        id: String,
        title: String,
        description: String,
        subject: String,
        classLevel: String,
        topic: String?,
        batch: String?,
        dueDate: String,
        maxMarks: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = homeworkDao.getHomeworkById(id)
            if (existing != null) {
                val updated = existing.copy(
                    title = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(title, 100),
                    description = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(description, 500),
                    subject = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(subject, 50),
                    classLevel = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(classLevel, 30),
                    topic = topic?.let { com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(it, 100) },
                    batch = batch?.let { com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(it, 50) },
                    dueDate = dueDate,
                    maxMarks = maxMarks
                )
                homeworkDao.updateHomework(updated)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(maskErrorMessage(e, "Failed to update homework")))
        }
    }

    override suspend fun downloadHomework(context: Context, homeworkId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val hw = homeworkDao.getHomeworkById(homeworkId) ?: return@withContext Result.failure(Exception("Homework not found"))
            val dir = File(context.filesDir, "homework")
            if (!dir.exists()) dir.mkdirs()
            val ext = when (hw.fileType.uppercase()) {
                "PDF" -> "pdf"
                "IMAGE", "JPG", "PNG", "JPEG" -> "jpg"
                "DOCX", "DOC" -> "docx"
                else -> "txt"
            }
            val targetFile = File(dir, "hw_${hw.id}.$ext")
            if (hw.fileUrl.startsWith("data:")) {
                val base64Data = hw.fileUrl.substringAfter("base64,")
                val decoded = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                FileOutputStream(targetFile).use { it.write(decoded) }
            } else if (hw.fileUrl.startsWith("http://") || hw.fileUrl.startsWith("https://")) {
                val req = okhttp3.Request.Builder().url(hw.fileUrl).build()
                okHttpClient.newCall(req).execute().use { res ->
                    if (res.isSuccessful) {
                        res.body?.byteStream()?.use { input ->
                            FileOutputStream(targetFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                    } else {
                        targetFile.writeText("Assignment: ${hw.title}\nSubject: ${hw.subject}\nClass: ${hw.classLevel}\nTopic: ${hw.topic ?: "N/A"}\n\nInstructions / Content:\n${hw.description}")
                    }
                }
            } else {
                val textContent = if (hw.fileUrl.isNotBlank()) hw.fileUrl else "Assignment: ${hw.title}\nSubject: ${hw.subject}\nClass: ${hw.classLevel}\nTopic: ${hw.topic ?: "N/A"}\n\nInstructions / Content:\n${hw.description}"
                targetFile.writeText(textContent)
            }
            homeworkDao.updateHomework(hw.copy(localPath = targetFile.absolutePath, isDownloaded = true))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(maskErrorMessage(e, "Failed to download homework")))
        }
    }

    override suspend fun purgeExpiredHomework(): Int = withContext(Dispatchers.IO) {
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        homeworkDao.purgeOldHomework(thirtyDaysAgo)
    }

    // --- Guest Telemetry Implementation ---

    override suspend fun recordGuestTelemetry(
        guestId: String,
        deviceName: String,
        deviceModel: String,
        location: String,
        ipAddress: String
    ) = withContext(Dispatchers.IO) {
        try {
            val existing = guestTelemetryDao.getGuestById(guestId)
            if (existing != null) {
                guestTelemetryDao.updateGuest(
                    existing.copy(
                        deviceName = deviceName,
                        deviceModel = deviceModel,
                        location = location,
                        ipAddress = ipAddress,
                        lastActiveTimestamp = System.currentTimeMillis(),
                        actionsCount = existing.actionsCount + 1
                    )
                )
            } else {
                val newGuest = GuestTelemetryEntity(
                    id = "guest_tel_${System.currentTimeMillis()}",
                    guestId = guestId,
                    deviceName = deviceName,
                    deviceModel = deviceModel,
                    location = location,
                    ipAddress = ipAddress,
                    firstSeenTimestamp = System.currentTimeMillis(),
                    lastActiveTimestamp = System.currentTimeMillis(),
                    actionsCount = 1
                )
                guestTelemetryDao.insertGuest(newGuest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getGuestTelemetryList(): Flow<List<GuestTelemetryEntity>> {
        return guestTelemetryDao.getAllGuestsFlow()
    }

    override suspend fun purgeExpiredGuests(): Int = withContext(Dispatchers.IO) {
        val sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000L)
        guestTelemetryDao.purgeOldGuests(sevenDaysAgo)
    }

    // --- Notification Engine Implementation ---

    override fun getStudentNotifications(role: String, classLevel: String): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsForStudentFlow(role, classLevel)
    }

    override fun getAdminNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsForAdminFlow()
    }

    override fun getStudentUnreadCount(role: String, classLevel: String): Flow<Int> {
        return notificationDao.getUnreadCountForStudentFlow(role, classLevel)
    }

    override fun getAdminUnreadCount(): Flow<Int> {
        return notificationDao.getUnreadCountForAdminFlow()
    }

    override suspend fun markNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    override suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    override suspend fun deleteNotification(id: String) = withContext(Dispatchers.IO) {
        notificationDao.deleteNotification(id)
    }

    override suspend fun clearAllNotifications() = withContext(Dispatchers.IO) {
        notificationDao.clearNotifications()
    }

    override suspend fun sendNotification(notification: NotificationEntity) = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(notification)
    }

    override suspend fun purgeExpiredNotifications(): Int = withContext(Dispatchers.IO) {
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        val purgedByAge = notificationDao.purgeOldNotifications(thirtyDaysAgo)
        val purgedByCap = notificationDao.trimNotificationHistory(100)
        purgedByAge + purgedByCap
    }

    override suspend fun loginAsGuest(
        guestId: String,
        deviceName: String,
        deviceModel: String,
        location: String,
        ipAddress: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val guestUser = UserEntity(
                id = guestId,
                name = "Guest Student",
                email = "$guestId@sparkingpathshala.in",
                rollNo = "SP-GUEST",
                classLevel = "Class 10",
                token = "guest_token_$guestId",
                role = "guest"
            )
            userDao.insertUser(guestUser)
            recordGuestTelemetry(guestId, deviceName, deviceModel, location, ipAddress)
            Result.success(guestUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncHomework(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            val user = userDao.getUserDirect() ?: return@withContext Result.failure(Exception("User not found"))
            val items = if (user.isAdmin()) {
                if (token.isEmpty()) emptyList() else api.getAdminHomework(token).homework
            } else {
                if (token.isEmpty()) emptyList() else api.getStudentHomework(token).homework
            }
            val entities = items.map { item ->
                val rawId = item.id?.toString() ?: ""
                val idStr = if (rawId.endsWith(".0")) rawId.removeSuffix(".0") else rawId.ifBlank { "hw_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(6)}" }
                val subjectStr = item.subject?.ifBlank { null } ?: "General"
                val classStr = item.classLevel?.ifBlank { null } ?: "All"
                val titleStr = item.topic?.ifBlank { null } ?: item.subject?.ifBlank { null }?.let { "$it Homework" } ?: "General Homework"
                HomeworkEntity(
                    id = idStr,
                    title = titleStr,
                    description = item.questionText ?: "",
                    subject = subjectStr,
                    classLevel = classStr,
                    topic = item.topic,
                    fileUrl = item.imageName ?: "",
                    fileType = if (item.imageName?.endsWith(".pdf", ignoreCase = true) == true) "PDF" else "IMAGE",
                    dueDate = item.assignedDate ?: "",
                    createdAt = System.currentTimeMillis()
                )
            }
            if (entities.isNotEmpty()) {
                homeworkDao.insertHomework(entities)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncNotifications(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.success(Unit)
            val res = api.getNotifications(token)
            val entities = res.notifications.map { item ->
                NotificationEntity(
                    id = item.id.toString(),
                    recipientRole = "student",
                    targetClass = "All",
                    title = item.title,
                    message = item.message,
                    category = item.type.uppercase(),
                    isRead = item.isRead == 1,
                    actionRoute = item.linkTab
                )
            }
            if (entities.isNotEmpty()) {
                notificationDao.insertNotifications(entities)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generatePracticeQuiz(subject: String, numQuestions: Int): Result<ActiveQuizResponse> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            val user = userDao.getUserDirect()
            val classLevel = user?.classLevel ?: "Class 10"
            val res = api.generatePracticeQuiz(
                token,
                PracticeQuizRequest(classLevel = classLevel, subject = subject, numQuestions = numQuestions)
            )
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuizHistory(): Result<List<QuizHistoryItem>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken()
            if (token.isEmpty()) return@withContext Result.success(emptyList())
            val res = api.getQuizHistory(token)
            Result.success(res.history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkAppVersion(): Result<com.sparkingpathshala.app.data.remote.AppVersionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAppVersion()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
