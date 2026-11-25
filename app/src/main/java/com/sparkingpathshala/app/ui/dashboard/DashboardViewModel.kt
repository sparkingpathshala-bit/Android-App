package com.sparkingpathshala.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sparkingpathshala.app.data.local.*
import com.sparkingpathshala.app.data.remote.*
import com.sparkingpathshala.app.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.core.content.edit

sealed interface SyncState {
    object Idle : SyncState
    object Syncing : SyncState
    object Success : SyncState
    data class Error(val message: String) : SyncState
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DashboardViewModel(private val repository: AppRepository) : ViewModel() {

    val user: StateFlow<UserEntity?> = repository.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Expose cached data lists for simple overview on dashboard
    val fees: StateFlow<List<FeeEntity>> = repository.getFees()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val results: StateFlow<List<ResultEntity>> = repository.getResults()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendance: StateFlow<List<AttendanceEntity>> = repository.getAttendance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyMaterials: StateFlow<List<StudyMaterialEntity>> = repository.getStudyMaterials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedCount: StateFlow<Int> = kotlinx.coroutines.flow.combine(repository.getStudyMaterials(), user) { list, currentUser ->
        if (currentUser.isGuest()) {
            0
        } else {
            list.count { it.isDownloaded }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val quizzes: StateFlow<List<QuizEntity>> = repository.getQuizzes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Sync everything when user is authenticated and available
        viewModelScope.launch {
            user.filterNotNull()
                .distinctUntilChangedBy { it.id }
                .collect { currentUser ->
                    if (!currentUser.isGuest()) {
                        syncAll()
                    }
                }
        }

        // Real-time notification polling: checks every 12 seconds so notifications arrive immediately with zero delay
        viewModelScope.launch {
            while (true) {
                if (user.value != null && !user.value.isGuest()) {
                    runCatching { repository.syncNotifications() }
                }
                kotlinx.coroutines.delay(12_000L)
            }
        }
    }

    fun syncAll() {
        val currentUser = user.value
        if (currentUser == null || currentUser.isGuest()) {
            _syncState.value = SyncState.Success
            return
        }
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            _isRefreshing.value = true
            var hasError = false
            kotlinx.coroutines.coroutineScope {
                val jobs = listOf(
                    launch { runCatching { repository.syncFees() }.onFailure { hasError = true } },
                    launch { runCatching { repository.syncStudyMaterials() }.onFailure { hasError = true } },
                    launch { runCatching { repository.syncQuizzes() }.onFailure { hasError = true } },
                    launch { runCatching { repository.syncHomework() }.onFailure { hasError = true } },
                    launch { runCatching { repository.syncNotifications() }.onFailure { hasError = true } },
                    launch { runCatching { repository.syncAttendance() }.onFailure { hasError = true } },
                    launch { runCatching { repository.syncResults() }.onFailure { hasError = true } }
                )
                jobs.forEach { it.join() }
            }
            _isRefreshing.value = false
            _syncState.value = if (hasError) SyncState.Error("Some items could not sync") else SyncState.Success
        }
    }

    fun getQuestionsForQuiz(context: android.content.Context, quizId: String): Flow<List<QuestionEntity>> {
        val appContext = context.applicationContext ?: context
        if (quizId.startsWith("practice_")) {
            val parts = quizId.substringAfter("practice_").split("_")
            if (parts.size >= 2) {
                val className = parts[0]
                val subject = parts[1]
                val count = if (parts.size >= 3) parts[2].toIntOrNull() ?: 5 else 5
                return flow {
                    val userId = user.value?.id ?: "guest"
                    val prefs = appContext.getSharedPreferences("sparking_quiz_prefs", android.content.Context.MODE_PRIVATE)
                    val seenKey = "seen_questions_${userId}_${quizId}"
                    val seenSet = prefs.getStringSet(seenKey, emptySet()) ?: emptySet()
                    
                    val allSaved = repository.getQuestions(quizId).first()
                    val seenTexts = allSaved.filter { it.id in seenSet }.map { it.questionText.trim() }.distinct().takeLast(20)

                    repository.deleteQuestionsByQuizId(quizId)
                    
                    // Fetch practice questions via secure backend API (with 3s timeout for instant smooth loading)
                    val backendQuestions = kotlinx.coroutines.withTimeoutOrNull(3000L) {
                        try {
                            val res = repository.generatePracticeQuiz(subject, count)
                            res.getOrNull()?.questions?.map { q ->
                                QuestionEntity(
                                    id = q.id,
                                    quizId = quizId,
                                    questionText = q.questionText,
                                    optionA = q.optionA,
                                    optionB = q.optionB,
                                    optionC = q.optionC,
                                    optionD = q.optionD,
                                    correctAnswer = when (q.correctOption?.uppercase()) {
                                        "A" -> 0
                                        "B" -> 1
                                        "C" -> 2
                                        "D" -> 3
                                        else -> 0
                                    },
                                    explanation = q.explanation ?: "Aligned with CBSE curriculum."
                                )
                            }
                        } catch (_: Exception) {
                            null
                        }
                    }

                    if (backendQuestions != null && backendQuestions.isNotEmpty()) {
                        val uniqueBackend = backendQuestions.distinctBy { it.questionText.trim().lowercase() }
                        var filtered = uniqueBackend.filter { it.id !in seenSet }
                        
                        if (filtered.size < count) {
                            val filler = QuestionBank.getQuestions(className, subject, count, seenSet)
                                .filter { it.id !in filtered.map { q -> q.id } }
                            filtered = (filtered + filler).take(count)
                        }
                        
                        val updatedQuestions = filtered.map { it.copy(quizId = quizId).shuffledOptions() }
                        repository.saveQuestions(updatedQuestions)
                        emit(updatedQuestions)
                    } else {
                        // Fallback 1: Check local cached database offline bank (Room DB)
                        val cachedBank = repository.getQuestions("offline_bank_${className}_${subject}").first()
                        val uniqueCached = cachedBank.distinctBy { it.questionText.trim().lowercase() }
                        val unseen = uniqueCached.filter { it.id !in seenSet }
                        val finalSelection = if (unseen.size >= count) {
                            unseen.shuffled().take(count)
                        } else {
                            val seen = uniqueCached.filter { it.id in seenSet }.shuffled()
                            (unseen.shuffled() + seen).take(count)
                        }
                        
                        if (finalSelection.isNotEmpty() && finalSelection.size >= count) {
                            val preparedBank = finalSelection.map { it.copy(quizId = quizId) }
                            repository.saveQuestions(preparedBank)
                            emit(preparedBank)
                        } else {
                            // Fallback 2: Verified CBSE QuestionBank (pure NCERT syllabus aligned)
                            val localList = QuestionBank.getQuestions(className, subject, count, seenSet)
                            val finalSelectionList = if (localList.isNotEmpty()) {
                                localList.take(count)
                            } else {
                                QuestionBank.getQuestions(className, subject, count, emptySet()).take(count)
                            }
                            val finalLocal = finalSelectionList.map { it.copy(quizId = quizId) }
                            repository.saveQuestions(finalLocal)
                            emit(finalLocal)
                        }
                    }
                }.flowOn(Dispatchers.IO)
            }
        }
        if (quizId == "practice_quiz") {
            val list = listOf(
                QuestionEntity("p1", "practice_quiz", "What is the focal length of a flat plane mirror?", "0", "Infinity", "10 cm", "-10 cm", 1, "A plane mirror has an infinite focal length because it doesn't converge or diverge rays."),
                QuestionEntity("p2", "practice_quiz", "Which lens is used to correct myopia?", "Convex", "Concave", "Bifocal", "Cylindrical", 1, "Myopia (short-sightedness) requires a diverging concave lens to bring the image onto the retina."),
                QuestionEntity("p3", "practice_quiz", "The speed of light in vacuum is approximately:", "3 x 10^5 m/s", "3 x 10^8 m/s", "3 x 10^6 m/s", "1.5 x 10^8 m/s", 1, "Light travels at approximately 300,000,000 meters per second (3 x 10^8 m/s) in a vacuum."),
                QuestionEntity("p4", "practice_quiz", "Which color of light deviates the most when passing through a prism?", "Red", "Yellow", "Violet", "Green", 2, "Violet light has the shortest wavelength in the visible spectrum and deviates the most."),
                QuestionEntity("p5", "practice_quiz", "SI unit of power of a lens is:", "Meter", "Dioptre", "Watt", "Decibel", 1, "The power of a lens is measured in Dioptres (D), which is the reciprocal of the focal length in meters.")
            ).map { it.shuffledOptions() }
            return flow {
                repository.deleteQuestionsByQuizId(quizId)
                repository.saveQuestions(list)
                emit(list)
            }.flowOn(Dispatchers.IO)
        }
        return repository.getQuestions(quizId)
    }

    fun syncStudyMaterials() {
        viewModelScope.launch {
            runCatching { repository.syncStudyMaterials() }
        }
    }

    fun syncAttendance() {
        viewModelScope.launch {
            runCatching { repository.syncAttendance() }
        }
    }

    fun syncResults() {
        viewModelScope.launch {
            runCatching { repository.syncResults() }
        }
    }

    fun syncFees() {
        viewModelScope.launch {
            runCatching { repository.syncFees() }
        }
    }

    fun syncQuizzes() {
        viewModelScope.launch {
            runCatching { repository.syncQuizzes() }
        }
    }

    private val _isHomeworkSyncing = MutableStateFlow(false)
    val isHomeworkSyncing: StateFlow<Boolean> = _isHomeworkSyncing.asStateFlow()

    fun syncHomework(onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _isHomeworkSyncing.value = true
            val res = runCatching { repository.syncHomework() }
            _isHomeworkSyncing.value = false
            onResult?.invoke(res.isSuccess)
        }
    }

    fun syncNotifications() {
        viewModelScope.launch {
            runCatching { repository.syncNotifications() }
        }
    }

    fun downloadMaterial(context: android.content.Context, materialId: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.downloadMaterial(context, materialId)
            onResult(result.isSuccess)
        }
    }

    fun deleteDownloadedMaterial(materialId: String) {
        viewModelScope.launch {
            repository.deleteDownloadedMaterial(materialId)
        }
    }

    fun deleteDownloadedHomework(homeworkId: String) {
        viewModelScope.launch {
            repository.deleteDownloadedHomework(homeworkId)
        }
    }

    fun requestOnDemand(subject: String, description: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = repository.requestOnDemand(subject, description)
            onResult(result)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun updatePassword(current: String, new: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.updatePassword(current, new)
            onResult(result)
        }
    }

    fun submitDoubt(title: String, subject: String, message: String, fileName: String?, fileData: String?, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.submitDoubt(title, subject, message, fileName, fileData))
        }
    }

    fun getStudentDoubts(onResult: (Result<List<DoubtResponse>>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getStudentDoubts())
        }
    }

    fun getAdminDoubts(onResult: (Result<List<DoubtResponse>>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getAdminDoubts())
        }
    }

    fun getDoubtReplies(doubtId: String, onResult: (Result<List<DoubtReplyResponse>>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getDoubtReplies(doubtId))
        }
    }

    fun submitDoubtReply(doubtId: String, message: String, fileName: String?, fileData: String?, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.submitDoubtReply(doubtId, message, fileName, fileData))
        }
    }

    fun updateDoubtStatus(doubtId: String, status: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.updateDoubtStatus(doubtId, status))
        }
    }

    fun deleteDoubt(doubtId: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.deleteDoubt(doubtId))
        }
    }

    fun submitQuiz(quizId: String, answers: Map<String, String>, onResult: (Result<SubmitQuizResponse>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.submitQuiz(quizId, answers))
        }
    }

    fun getAdminStudents(onResult: (Result<List<AdminStudentInfo>>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getAdminStudents())
        }
    }

    fun updateStudentStatus(id: String, status: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.updateStudentStatus(id, status))
        }
    }

    fun updateStudent(id: String, data: Map<String, Any>, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.updateStudent(id, data))
        }
    }

    fun getAdminRecords(type: String, onResult: (Result<List<AdminRecord>>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getAdminRecords(type))
        }
    }

    fun addAdminRecord(type: String, data: Map<String, Any>, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.addAdminRecord(type, data))
        }
    }

    fun updateAdminRecord(type: String, id: String, data: Map<String, Any>, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.updateAdminRecord(type, id, data))
        }
    }

    fun deleteAdminRecord(type: String, id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.deleteAdminRecord(type, id))
        }
    }

    fun uploadStudyMaterial(title: String, category: String, classSelection: String, subject: String, fileName: String, fileSize: String, fileData: String, allowedEmails: String? = null, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.uploadStudyMaterial(title, category, classSelection, subject, fileName, fileSize, fileData, allowedEmails))
        }
    }
 
    fun deleteResource(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.deleteResource(id))
        }
    }
 
    fun updateLibraryResource(
        id: String,
        title: String,
        category: String,
        classSelection: String,
        subject: String,
        status: String,
        allowedEmails: String? = null,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            onResult(repository.updateLibraryResource(id, title, category, classSelection, subject, status, allowedEmails))
        }
    }

    fun getAdminQuizzes(onResult: (Result<List<AdminQuizInfo>>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getAdminQuizzes())
        }
    }

    fun createManualQuiz(quiz: ManualQuizRequest, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.createManualQuiz(quiz))
        }
    }

    fun generateAiQuiz(quiz: AiQuizRequest, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.generateAiQuiz(quiz))
        }
    }

    fun deleteQuiz(quizId: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.deleteQuiz(quizId))
        }
    }

    fun deleteMultipleQuizzes(quizIds: List<String>, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.deleteMultipleQuizzes(quizIds))
        }
    }

    fun launchQuiz(quizId: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.launchQuiz(quizId))
        }
    }

    fun launchMultipleQuizzes(quizIds: List<String>, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.launchMultipleQuizzes(quizIds))
        }
    }

    fun syncQuizzes(onResult: ((Result<Unit>) -> Unit)? = null) {
        viewModelScope.launch {
            val res = repository.syncQuizzes()
            onResult?.invoke(res)
        }
    }

    fun getPendingStudents(onResult: (Result<List<PendingStudentInfo>>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getPendingStudents())
        }
    }

    fun approveStudent(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.approveStudent(id))
        }
    }

    fun rejectStudent(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.rejectStudent(id))
        }
    }

    fun deleteStudent(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.deleteStudent(id))
        }
    }

    fun initializePayment(month: String, amount: Double, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.initializePayment(month, amount))
        }
    }

    fun verifyPayment(txnRefId: String, utrNumber: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.verifyPayment(txnRefId, utrNumber))
        }
    }

    fun getStudentTransactions(onResult: (Result<List<TransactionResponse>>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getStudentTransactions())
        }
    }

    fun getAdminEnquiries(onResult: (Result<List<EnquiryInfo>>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getAdminEnquiries())
        }
    }

    fun updateAdminEnquiry(id: String, data: Map<String, Any>, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.updateAdminEnquiry(id, data))
        }
    }

    fun deleteAdminEnquiry(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.deleteAdminEnquiry(id))
        }
    }

    fun listenToEnquiriesStream(onUpdate: () -> Unit): okhttp3.Call? {
        return repository.listenToEnquiriesStream(onUpdate)
    }

    // --- Homework Management State & Actions ---

    val homeworkList: StateFlow<List<HomeworkEntity>> = user
        .flatMapLatest { currentUser ->
            // Retrieve all local cached homework and allow UI-level resilient isClassMatch filtering
            repository.getAllHomework()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun uploadHomework(
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
        maxMarks: Int,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.uploadHomework(title, description, subject, classLevel, topic, batch, fileName, fileData, fileType, dueDate, maxMarks)
            onResult(res)
        }
    }

    fun deleteHomework(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = repository.deleteHomework(id)
            onResult(res)
        }
    }

    fun updateHomework(
        id: String,
        title: String,
        description: String,
        subject: String,
        classLevel: String,
        topic: String?,
        batch: String?,
        dueDate: String,
        maxMarks: Int,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.updateHomework(id, title, description, subject, classLevel, topic, batch, dueDate, maxMarks)
            onResult(res)
        }
    }

    fun downloadHomework(context: android.content.Context, homeworkId: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = repository.downloadHomework(context, homeworkId)
            onResult(res)
        }
    }

    // --- Guest Telemetry State & Actions ---

    val guestList: StateFlow<List<GuestTelemetryEntity>> = repository.getGuestTelemetryList()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Notification Engine State & Actions ---

    val notifications: StateFlow<List<NotificationEntity>> = user
        .flatMapLatest { currentUser ->
            val role = currentUser?.role ?: "student"
            val classLevel = if (currentUser.isGuest()) "All" else (currentUser?.classLevel ?: "All")
            if (currentUser.isAdmin()) {
                repository.getAdminNotifications()
            } else {
                repository.getStudentNotifications(role, classLevel)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = user
        .flatMapLatest { currentUser ->
            val role = currentUser?.role ?: "student"
            val classLevel = if (currentUser.isGuest()) "All" else (currentUser?.classLevel ?: "All")
            if (currentUser.isAdmin()) {
                repository.getAdminUnreadCount()
            } else {
                repository.getStudentUnreadCount(role, classLevel)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    fun triggerNotification(
        title: String,
        message: String,
        category: String,
        targetClass: String = "All",
        recipientRole: String = "student",
        priority: String = "NORMAL"
    ) {
        viewModelScope.launch {
            val notif = NotificationEntity(
                id = "notif_custom_${System.currentTimeMillis()}",
                recipientRole = recipientRole,
                targetClass = targetClass,
                title = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(title, 100),
                message = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(message, 300),
                category = category,
                timestamp = System.currentTimeMillis(),
                priority = priority
            )
            repository.sendNotification(notif)
        }
    }

    // --- Version Control & Force-Update Checking ---

    enum class ForceUpdateStatus {
        UP_TO_DATE,
        GRACE_PERIOD_ACTIVE, // Shows non-blocking banner reminding user to update
        FORCE_UPDATE_REQUIRED // Blocks app usage after 7 days
    }

    data class AppVersionInfo(
        val currentVersionCode: Int = com.sparkingpathshala.app.BuildConfig.VERSION_CODE,
        val latestVersionCode: Int = com.sparkingpathshala.app.BuildConfig.VERSION_CODE,
        val latestVersionName: String = com.sparkingpathshala.app.BuildConfig.VERSION_NAME,
        val releaseDateMillis: Long = 1795824000000L, // 2026/2027 standard
        val status: ForceUpdateStatus = ForceUpdateStatus.UP_TO_DATE,
        val daysRemainingInGrace: Int = 7
    )

    private val _versionInfo = MutableStateFlow(AppVersionInfo())
    val versionInfo: StateFlow<AppVersionInfo> = _versionInfo.asStateFlow()

    fun checkAppVersion(currentCode: Int) {
        val latestCode = com.sparkingpathshala.app.BuildConfig.VERSION_CODE
        val latestName = com.sparkingpathshala.app.BuildConfig.VERSION_NAME
        val releaseDate = 1795824000000L
        val sevenDaysMs = 7L * 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()
        val elapsed = (now - releaseDate).coerceAtLeast(0L)
        val daysRemaining = ((sevenDaysMs - elapsed) / (24 * 60 * 60 * 1000L)).coerceAtLeast(0).toInt()

        val status = when {
            currentCode >= latestCode -> ForceUpdateStatus.UP_TO_DATE
            elapsed <= sevenDaysMs -> ForceUpdateStatus.GRACE_PERIOD_ACTIVE
            else -> ForceUpdateStatus.FORCE_UPDATE_REQUIRED
        }

        _versionInfo.value = AppVersionInfo(
            currentVersionCode = currentCode,
            latestVersionCode = latestCode,
            latestVersionName = latestName,
            releaseDateMillis = releaseDate,
            status = status,
            daysRemainingInGrace = daysRemaining
        )

        // Remote sync against backend version policy
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.checkAppVersion()
            res.onSuccess { remote ->
                val remoteCode = remote.latestVersionCode
                val remoteName = remote.latestVersionName
                val remoteStatus = when {
                    currentCode >= remoteCode -> ForceUpdateStatus.UP_TO_DATE
                    remote.forceUpdate -> ForceUpdateStatus.FORCE_UPDATE_REQUIRED
                    else -> ForceUpdateStatus.GRACE_PERIOD_ACTIVE
                }
                _versionInfo.value = AppVersionInfo(
                    currentVersionCode = currentCode,
                    latestVersionCode = remoteCode,
                    latestVersionName = remoteName,
                    releaseDateMillis = releaseDate,
                    status = remoteStatus,
                    daysRemainingInGrace = daysRemaining
                )
            }
        }
    }

    init {
        // Auto-purge old homework (> 30 days), old guest telemetry (> 14 days), and stale notifications (> 30 days / max 100 items)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.purgeExpiredHomework()
                repository.purgeExpiredGuests()
                repository.purgeExpiredNotifications()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Publishes a Faculty Notice announcement to the backend notification engine
     * and broadcasts it to all student devices.
     */
    fun sendNoticeAnnouncement(title: String, message: String, note: String?) {
        viewModelScope.launch {
            val fullMsg = if (!note.isNullOrBlank()) "$message\n\n📌 $note" else message
            val notification = NotificationEntity(
                id = "notice_${System.currentTimeMillis()}",
                recipientRole = "student",
                targetClass = "All",
                title = title,
                message = fullMsg,
                category = "GENERAL",
                actionRoute = "dashboard"
            )
            repository.sendNotification(notification)
        }
    }
}
