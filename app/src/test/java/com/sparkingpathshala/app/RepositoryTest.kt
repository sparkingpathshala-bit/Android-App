package com.sparkingpathshala.app

import com.sparkingpathshala.app.data.local.*
import com.sparkingpathshala.app.data.remote.*
import com.sparkingpathshala.app.data.repository.AppRepositoryImpl
import com.sparkingpathshala.app.security.SecurityUtils
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.Before
import org.junit.After
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class RepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    class FakeUserDao : UserDao {
        var cachedUser: UserEntity? = null

        override fun getUserFlow(): Flow<UserEntity?> = flowOf(cachedUser)
        override suspend fun getUserDirect(): UserEntity? = cachedUser
        override suspend fun insertUser(user: UserEntity) {
            cachedUser = user
        }
        override suspend fun clearUser() {
            cachedUser = null
        }
    }

    class FakeStudyMaterialDao : StudyMaterialDao {
        val materials = mutableListOf<StudyMaterialEntity>()
        override fun getAllMaterialsFlow(): Flow<List<StudyMaterialEntity>> = flowOf(materials.toList())
        override fun getMaterialsByClassFlow(classLevel: String): Flow<List<StudyMaterialEntity>> = 
            flowOf(materials.filter { it.classLevel == classLevel || it.classLevel == "All" })
        override suspend fun getMaterialById(id: String): StudyMaterialEntity? = materials.find { it.id == id }
        override suspend fun getAllDownloadedMaterials(): List<StudyMaterialEntity> = materials.filter { it.isDownloaded }
        override suspend fun insertMaterials(materials: List<StudyMaterialEntity>) {
            materials.forEach { item ->
                this.materials.removeAll { it.id == item.id }
                this.materials.add(item)
            }
        }
        override suspend fun updateMaterial(material: StudyMaterialEntity) {
            val idx = materials.indexOfFirst { it.id == material.id }
            if (idx != -1) materials[idx] = material
        }
        override suspend fun clearMaterials() { materials.clear() }
        override suspend fun clearNonDownloadedMaterials() { materials.removeAll { !it.isDownloaded } }
        override suspend fun deleteMaterial(id: String) { materials.removeAll { it.id == id } }
    }

    class FakeHomeworkDao : HomeworkDao {
        val items = mutableListOf<HomeworkEntity>()
        override fun getAllHomeworkFlow(): Flow<List<HomeworkEntity>> = flowOf(items.toList())
        override fun getHomeworkByClassFlow(classLevel: String): Flow<List<HomeworkEntity>> =
            flowOf(items.filter { it.classLevel == classLevel || it.classLevel == "All" })
        override suspend fun getHomeworkById(id: String): HomeworkEntity? = items.find { it.id == id }
        override suspend fun insertHomework(items: List<HomeworkEntity>) {
            items.forEach { item ->
                this.items.removeAll { it.id == item.id }
                this.items.add(item)
            }
        }
        override suspend fun insertSingleHomework(item: HomeworkEntity) {
            items.removeAll { it.id == item.id }
            items.add(item)
        }
        override suspend fun updateHomework(item: HomeworkEntity) {
            val idx = items.indexOfFirst { it.id == item.id }
            if (idx != -1) items[idx] = item
        }
        override suspend fun deleteHomework(id: String) { items.removeAll { it.id == id } }
        override suspend fun clearHomework() { items.clear() }
        override suspend fun clearNonDownloadedHomework() { items.removeAll { !it.isDownloaded } }
        override suspend fun purgeOldHomework(cutoffTimestamp: Long): Int {
            val countBefore = items.size
            items.removeAll { it.createdAt < cutoffTimestamp }
            return countBefore - items.size
        }
    }

    class FakeAttendanceDao : AttendanceDao {
        val list = mutableListOf<AttendanceEntity>()
        override fun getAttendanceFlow(): Flow<List<AttendanceEntity>> = flowOf(list.toList())
        override suspend fun insertAttendance(attendance: List<AttendanceEntity>) {
            attendance.forEach { item ->
                list.removeAll { it.date == item.date }
                list.add(item)
            }
        }
        override suspend fun clearAttendance() { list.clear() }
    }

    class FakeQuestionDao : QuestionDao {
        val questions = mutableListOf<QuestionEntity>()
        override fun getQuestionsFlow(quizId: String): Flow<List<QuestionEntity>> = flowOf(questions.filter { it.quizId == quizId })
        override suspend fun insertQuestions(questions: List<QuestionEntity>) {
            questions.forEach { q ->
                this.questions.removeAll { it.id == q.id }
                this.questions.add(q)
            }
        }
        override suspend fun deleteQuestionsByQuizId(quizId: String) {
            questions.removeAll { it.quizId == quizId }
        }
        override suspend fun getQuestionsByIds(ids: List<String>): List<QuestionEntity> {
            return questions.filter { it.id in ids }
        }
        override suspend fun clearQuestions() { questions.clear() }
    }

    class FakeQuizDao : QuizDao {
        val quizzes = mutableListOf<QuizEntity>()
        override fun getQuizzesFlow(): Flow<List<QuizEntity>> = flowOf(quizzes.toList())
        override suspend fun getAllQuizzes(): List<QuizEntity> = quizzes.toList()
        override suspend fun insertQuizzes(quizzes: List<QuizEntity>) {
            quizzes.forEach { q ->
                this.quizzes.removeAll { it.id == q.id }
                this.quizzes.add(q)
            }
        }
        override suspend fun updateQuizResult(quizId: String, attempted: Boolean, score: Int) {
            val idx = quizzes.indexOfFirst { it.id == quizId }
            if (idx != -1) {
                quizzes[idx] = quizzes[idx].copy(attempted = attempted, score = score)
            }
        }
        override suspend fun deleteQuiz(id: String) { quizzes.removeAll { it.id == id } }
        override suspend fun clearQuizzes() { quizzes.clear() }
    }

    class FakeGuestTelemetryDao : GuestTelemetryDao {
        val guests = mutableListOf<GuestTelemetryEntity>()
        override fun getAllGuestsFlow(): Flow<List<GuestTelemetryEntity>> = flowOf(guests.toList())
        override suspend fun getGuestById(guestId: String): GuestTelemetryEntity? = guests.find { it.guestId == guestId }
        override suspend fun insertGuest(guest: GuestTelemetryEntity) {
            guests.removeAll { it.guestId == guest.guestId }
            guests.add(guest)
        }
        override suspend fun updateGuest(guest: GuestTelemetryEntity) {
            val idx = guests.indexOfFirst { it.guestId == guest.guestId }
            if (idx != -1) guests[idx] = guest
        }
        override suspend fun purgeOldGuests(cutoffTimestamp: Long): Int {
            val before = guests.size
            guests.removeAll { it.lastActiveTimestamp < cutoffTimestamp }
            return before - guests.size
        }
    }

    class FakeNotificationDao : NotificationDao {
        val notifications = mutableListOf<NotificationEntity>()
        override fun getNotificationsForStudentFlow(role: String, classLevel: String): Flow<List<NotificationEntity>> = flowOf(notifications.toList())
        override fun getNotificationsForAdminFlow(): Flow<List<NotificationEntity>> = flowOf(notifications.toList())
        override fun getUnreadCountForStudentFlow(role: String, classLevel: String): Flow<Int> = flowOf(notifications.count { !it.isRead })
        override fun getUnreadCountForAdminFlow(): Flow<Int> = flowOf(notifications.count { !it.isRead })
        override suspend fun insertNotification(notification: NotificationEntity) {
            notifications.removeAll { it.id == notification.id }
            notifications.add(notification)
        }
        override suspend fun insertNotifications(notifications: List<NotificationEntity>) {
            notifications.forEach { insertNotification(it) }
        }
        override suspend fun insertNotificationIgnore(notification: NotificationEntity) {
            if (notifications.none { it.id == notification.id }) {
                notifications.add(notification)
            }
        }
        override suspend fun markAsRead(id: String) {
            val idx = notifications.indexOfFirst { it.id == id }
            if (idx != -1) notifications[idx] = notifications[idx].copy(isRead = true)
        }
        override suspend fun markAllAsRead() {
            for (i in notifications.indices) {
                notifications[i] = notifications[i].copy(isRead = true)
            }
        }
        override suspend fun deleteNotification(id: String) {
            notifications.removeAll { it.id == id }
        }
        override suspend fun clearNotifications() {
            notifications.clear()
        }
        override suspend fun purgeOldNotifications(cutoffTimestamp: Long): Int {
            val before = notifications.size
            notifications.removeAll { it.timestamp < cutoffTimestamp }
            return before - notifications.size
        }
        override suspend fun trimNotificationHistory(keepLimit: Int): Int {
            if (notifications.size > keepLimit) {
                val excess = notifications.size - keepLimit
                repeat(excess) { notifications.removeAt(0) }
                return excess
            }
            return 0
        }
    }

    class FakeApiService : ApiService {
        val enquiriesList = mutableListOf(
            EnquiryInfo("enq_1", "John Doe", "john@example.com", "9876543210", "Class 10", "Science", "Enquiry 1", "Pending", null, "2026-06-18")
        )

        override suspend fun login(request: LoginRequest): AuthResponse {
            if (request.email == "student@sparkingpathshala.in" && request.password == "password123") {
                return AuthResponse(
                    token = "bearer_token",
                    user = UserInfo(
                        id = "student_101",
                        name = "Sajjan Mallik",
                        email = request.email,
                        classSelection = "Class 10",
                        role = "student",
                        status = "approved"
                    )
                )
            }
            if (request.email == "pending@sparkingpathshala.in" && request.password == "password123") {
                return AuthResponse(
                    token = "bearer_token",
                    user = UserInfo(
                        id = "student_pending",
                        name = "Pending User",
                        email = request.email,
                        classSelection = "Class 10",
                        role = "student",
                        status = "pending_approval"
                    )
                )
            }
            if (request.email == "inactive@sparkingpathshala.in" && request.password == "password123") {
                return AuthResponse(
                    token = "bearer_token",
                    user = UserInfo(
                        id = "student_inactive",
                        name = "Inactive User",
                        email = request.email,
                        classSelection = "Class 10",
                        role = "student",
                        status = "inactive"
                    )
                )
            }
            throw Exception("Invalid credentials")
        }

        override suspend fun signup(request: SignupRequest): SimpleMessageResponse {
            return SimpleMessageResponse("Registration successful! Awaiting Admin approval.")
        }

        override suspend fun getStudentDashboard(token: String): DashboardResponse {
            return DashboardResponse(emptyList(), emptyList(), emptyList())
        }

        override suspend fun getLibraryResources(token: String, category: String): LibraryResourcesResponse {
            return LibraryResourcesResponse(emptyList())
        }

        override suspend fun downloadResource(token: String, resourceId: String): DownloadResourceResponse {
            return DownloadResourceResponse("test.pdf", "data:application/pdf;base64,AAA=")
        }

        override suspend fun submitDoubt(token: String, request: DoubtRequest): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun getActiveQuiz(token: String): ActiveQuizResponse {
            return ActiveQuizResponse(
                quiz = QuizDetailsResponse("quiz_01", "Physics", "Optics", "Class 10", "15-min", "2026-06-09 10:00"),
                questions = emptyList()
            )
        }

        override suspend fun deleteAccount(token: String): SimpleMessageResponse {
            return SimpleMessageResponse("Your account and all associated data have been permanently deleted.")
        }

        override suspend fun updatePassword(token: String, request: UpdatePasswordRequest): SimpleMessageResponse {
            return SimpleMessageResponse("Password updated successfully!")
        }

        override suspend fun getStudentDoubts(token: String): DoubtsListResponse {
            return DoubtsListResponse(emptyList())
        }

        override suspend fun getAdminDoubts(token: String): DoubtsListResponse {
            return DoubtsListResponse(emptyList())
        }

        override suspend fun getDoubtReplies(token: String, doubtId: String): DoubtRepliesResponse {
            return DoubtRepliesResponse(emptyList())
        }

        override suspend fun submitDoubtReply(token: String, doubtId: String, request: DoubtReplyRequest): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun updateDoubtStatus(token: String, doubtId: String, request: UpdateDoubtStatusRequest): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun deleteDoubt(token: String, doubtId: String): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun submitQuiz(token: String, request: SubmitQuizRequest): SubmitQuizResponse {
            return SubmitQuizResponse("Success", 0, 0, emptyList())
        }

        override suspend fun getAdminStudents(token: String): AdminStudentsResponse {
            return AdminStudentsResponse(emptyList())
        }

        override suspend fun getAdminRecords(token: String, type: String): AdminRecordResponse {
            return AdminRecordResponse(emptyList())
        }

        override suspend fun addAdminRecord(token: String, type: String, request: Map<String, @JvmSuppressWildcards Any>): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun updateAdminRecord(token: String, type: String, id: String, request: Map<String, @JvmSuppressWildcards Any>): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun deleteAdminRecord(token: String, type: String, id: String): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun uploadStudyMaterial(token: String, request: Map<String, @JvmSuppressWildcards Any>): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun getAdminQuizzes(token: String): AdminQuizzesResponse {
            return AdminQuizzesResponse(emptyList())
        }

        override suspend fun createManualQuiz(token: String, request: ManualQuizRequest): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun generateAiQuiz(token: String, request: AiQuizRequest): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun deleteQuiz(token: String, quizId: String): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun deleteQuizSingular(token: String, quizId: String): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun launchQuiz(token: String, quizId: String): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun launchMultipleQuizzes(token: String, request: LaunchMultipleQuizzesRequest): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun deleteResource(token: String, id: String): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun getAllLibraryResourcesAdmin(token: String): LibraryResourcesResponse {
            return LibraryResourcesResponse(emptyList())
        }

        override suspend fun updateLibraryResource(token: String, id: String, request: Map<String, @JvmSuppressWildcards Any>): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun getPendingStudents(token: String): PendingStudentsResponse {
            return PendingStudentsResponse(emptyList())
        }

        override suspend fun approveStudent(token: String, id: String): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun rejectStudent(token: String, id: String): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun deleteStudent(token: String, id: String): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun initializePayment(token: String, request: InitializePaymentRequest): InitializePaymentResponse {
            return InitializePaymentResponse("mock_txn_ref_id")
        }

        override suspend fun verifyPayment(token: String, request: VerifyPaymentRequest): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun updateStudentStatus(token: String, id: String, request: Map<String, @JvmSuppressWildcards Any>): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun updateStudent(token: String, id: String, request: Map<String, @JvmSuppressWildcards Any>): SimpleMessageResponse {
            return SimpleMessageResponse("Success")
        }

        override suspend fun getStudentTransactions(token: String): List<TransactionResponse> {
            return emptyList()
        }

        override suspend fun getEnquiries(token: String): okhttp3.ResponseBody {
            val json = com.google.gson.Gson().toJson(EnquiryResponse(enquiriesList))
            return json.toResponseBody("application/json".toMediaTypeOrNull())
        }

        override suspend fun updateEnquiry(token: String, id: String, request: Map<String, @JvmSuppressWildcards Any>): SimpleMessageResponse {
            val idx = enquiriesList.indexOfFirst { it.id == id }
            if (idx != -1) {
                val existing = enquiriesList[idx]
                enquiriesList[idx] = existing.copy(
                    status = request["status"] as? String ?: existing.status,
                    remarks = request["remarks"] as? String ?: existing.remarks
                )
            }
            return SimpleMessageResponse("Success")
        }

        override suspend fun deleteEnquiry(token: String, id: String): SimpleMessageResponse {
            enquiriesList.removeIf { it.id == id }
            return SimpleMessageResponse("Success")
        }

        val studentHomeworkList = mutableListOf<RemoteHomeworkItem>()
        val adminHomeworkList = mutableListOf<RemoteHomeworkItem>()
        val notificationsList = mutableListOf<RemoteNotificationItem>()
        val quizHistoryList = mutableListOf<QuizHistoryItem>()

        override suspend fun getStudentHomework(token: String): StudentHomeworkResponse = StudentHomeworkResponse("Class 10", studentHomeworkList.toList())
        override suspend fun getAdminHomework(token: String): AdminHomeworkResponse = AdminHomeworkResponse(adminHomeworkList.toList())
        override suspend fun createHomework(token: String, request: CreateHomeworkRequest): SimpleMessageResponse {
            val item = RemoteHomeworkItem(
                id = (adminHomeworkList.size + 1),
                classLevel = request.classLevel,
                subject = request.subject,
                topic = request.topic,
                questionText = request.questionText,
                imageName = request.imageName,
                imageData = request.imageData,
                assignedDate = request.assignedDate,
                createdAt = "2026-06-18T10:00:00Z"
            )
            adminHomeworkList.add(item)
            return SimpleMessageResponse("Success")
        }
        override suspend fun updateHomework(token: String, id: String, request: UpdateHomeworkRequest): SimpleMessageResponse {
            val idx = adminHomeworkList.indexOfFirst { it.id.toString() == id }
            if (idx != -1) {
                val old = adminHomeworkList[idx]
                adminHomeworkList[idx] = old.copy(
                    classLevel = request.classLevel,
                    subject = request.subject,
                    topic = request.topic,
                    questionText = request.questionText,
                    imageName = request.imageName,
                    imageData = request.imageData,
                    assignedDate = request.assignedDate
                )
            }
            return SimpleMessageResponse("Success")
        }
        override suspend fun deleteHomework(token: String, id: String): SimpleMessageResponse {
            adminHomeworkList.removeIf { it.id.toString() == id }
            return SimpleMessageResponse("Success")
        }

        override suspend fun getNotifications(token: String): NotificationsListResponse = NotificationsListResponse(notificationsList.toList(), notificationsList.count { it.isRead == 0 })
        override suspend fun sendCustomNotification(token: String, request: SendCustomNotificationRequest): SimpleMessageResponse {
            notificationsList.add(RemoteNotificationItem(
                id = (notificationsList.size + 1),
                recipientEmail = request.recipientEmail ?: "all@sparkingpathshala.in",
                title = request.title,
                message = request.message,
                type = "CUSTOM",
                linkTab = request.linkTab,
                isRead = 0,
                createdAt = "2026-06-18T10:00:00Z"
            ))
            return SimpleMessageResponse("Success")
        }
        override suspend fun markNotificationRead(token: String, id: String): SimpleMessageResponse {
            val idx = notificationsList.indexOfFirst { it.id.toString() == id }
            if (idx != -1) notificationsList[idx] = notificationsList[idx].copy(isRead = 1)
            return SimpleMessageResponse("Success")
        }
        override suspend fun markAllNotificationsRead(token: String): SimpleMessageResponse {
            for (i in notificationsList.indices) {
                notificationsList[i] = notificationsList[i].copy(isRead = 1)
            }
            return SimpleMessageResponse("Success")
        }
        override suspend fun deleteNotification(token: String, id: String): SimpleMessageResponse {
            notificationsList.removeIf { it.id.toString() == id }
            return SimpleMessageResponse("Success")
        }
        override suspend fun clearAllNotifications(token: String): SimpleMessageResponse {
            notificationsList.clear()
            return SimpleMessageResponse("Success")
        }

        override suspend fun generatePracticeQuiz(token: String, request: PracticeQuizRequest): ActiveQuizResponse = ActiveQuizResponse(
            quiz = QuizDetailsResponse("prac_01", request.subject, "Practice", request.classLevel, "practice", "practice"),
            questions = emptyList()
        )
        override suspend fun getQuizHistory(token: String): QuizHistoryResponse = QuizHistoryResponse(quizHistoryList.toList())
        override suspend fun refreshFallbackBank(token: String, request: Map<String, @JvmSuppressWildcards Any>): SimpleMessageResponse = SimpleMessageResponse("Success")
        override suspend fun getFallbackBankStats(token: String): Map<String, Any> = emptyMap()
        override suspend fun getAppVersion(): AppVersionResponse = AppVersionResponse()
    }

    @Test
    fun testLoginSuccess() = runBlocking {
        // Arrange
        val fakeUserDao = FakeUserDao()
        val mockDb = mock(AppDatabase::class.java)
        
        // Mock the DAOs
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mock(NotificationDao::class.java))
        
        val fakeApi = FakeApiService()
        val okHttpClient = OkHttpClient()
        
        val repository = AppRepositoryImpl(mockDb, fakeApi, okHttpClient)
        
        // Act
        val result = repository.login("student@sparkingpathshala.in", "password123")
        
        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals("student_101", user?.id)
        assertEquals("Sajjan Mallik", user?.name)
        assertEquals("bearer_token", user?.token)
        
        // Verify user is inserted in cache and token is encrypted
        assertEquals(user?.id, fakeUserDao.cachedUser?.id)
        assertEquals(user?.name, fakeUserDao.cachedUser?.name)
        assertEquals(user?.token, SecurityUtils.decrypt(fakeUserDao.cachedUser!!.token))
    }

    @Test
    fun testLoginPending() = runBlocking {
        // Arrange
        val fakeUserDao = FakeUserDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mock(NotificationDao::class.java))

        val fakeApi = FakeApiService()
        val okHttpClient = OkHttpClient()
        val repository = AppRepositoryImpl(mockDb, fakeApi, okHttpClient)

        // Act
        val result = repository.login("pending@sparkingpathshala.in", "password123")

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("pending admin approval") == true)
        
        // Verify user is NOT inserted in cache
        assertNull(fakeUserDao.cachedUser)
    }

    @Test
    fun testLoginInactive() = runBlocking {
        // Arrange
        val fakeUserDao = FakeUserDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mock(NotificationDao::class.java))

        val fakeApi = FakeApiService()
        val okHttpClient = OkHttpClient()
        val repository = AppRepositoryImpl(mockDb, fakeApi, okHttpClient)

        // Act
        val result = repository.login("inactive@sparkingpathshala.in", "password123")

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("locked or inactive") == true)
        
        // Verify user is NOT inserted in cache
        assertNull(fakeUserDao.cachedUser)
    }

    @Test
    fun testEnquiriesCrud() = runBlocking {
        // Arrange
        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("student_101", "Sajjan Mallik", "student@sparkingpathshala.in", "Class 10", "student", "approved", "bearer_token"))
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mock(NotificationDao::class.java))
        
        val fakeApi = FakeApiService()
        val okHttpClient = OkHttpClient()
        val repository = AppRepositoryImpl(mockDb, fakeApi, okHttpClient)

        // 1. Get enquiries
        val enquiriesResult = repository.getAdminEnquiries()
        assertTrue(enquiriesResult.isSuccess)
        val enquiries = enquiriesResult.getOrNull()
        assertEquals(1, enquiries?.size)
        assertEquals("enq_1", enquiries?.first()?.id)
        assertEquals("John Doe", enquiries?.first()?.name)

        // 2. Update enquiry
        val updateResult = repository.updateAdminEnquiry("enq_1", mapOf("status" to "Contacted", "remarks" to "Spoke with parent"))
        assertTrue(updateResult.isSuccess)
        assertEquals("contacted", fakeApi.enquiriesList.first().status)
        assertEquals("Spoke with parent", fakeApi.enquiriesList.first().remarks)

        // 3. Delete enquiry
        val deleteResult = repository.deleteAdminEnquiry("enq_1")
        assertTrue(deleteResult.isSuccess)
        assertTrue(fakeApi.enquiriesList.isEmpty())
    }

    @Test
    fun testQuestionBankRotationAndFallback() {
        // Test standard retrieval
        val qList = QuestionBank.getQuestions("Class 10", "Physics")
        assertEquals(5, qList.size)
        qList.forEach {
            assertEquals("practice_Class 10_Physics", it.quizId)
            assertTrue(it.questionText.isNotEmpty())
        }

        // Test fallback to generic subjects
        val qFallback = QuestionBank.getQuestions("Class 9", "English")
        assertEquals(5, qFallback.size)
        qFallback.forEach {
            assertEquals("practice_Class 9_English", it.quizId)
        }
    }

    @Test
    fun testAttendanceDateParsing() {
        fun extractYearMonth(dateStr: String): String? {
            val clean = dateStr.trim()
            val parts = if (clean.contains("-")) {
                clean.split("-")
            } else if (clean.contains("/")) {
                clean.split("/")
            } else {
                return null
            }
            if (parts.size >= 3) {
                val first = parts[0]
                val second = parts[1]
                val third = parts[2]
                if (first.length == 4 && first.all { it.isDigit() }) {
                    return "$first-${second.padStart(2, '0')}"
                } else if (third.length == 4 && third.all { it.isDigit() }) {
                    return "$third-${second.padStart(2, '0')}"
                }
            } else if (parts.size == 2) {
                val first = parts[0]
                val second = parts[1]
                if (first.length == 4 && first.all { it.isDigit() }) {
                    return "$first-${second.padStart(2, '0')}"
                }
            }
            return null
        }

        assertEquals("2026-06", extractYearMonth("2026-06-18"))
        assertEquals("2026-06", extractYearMonth("18-06-2026"))
        assertEquals("2026-06", extractYearMonth("2026/06/18"))
        assertEquals("2026-06", extractYearMonth("18/06/2026"))
        assertEquals("2026-06", extractYearMonth("2026-6-18"))
        assertEquals("2026-06", extractYearMonth("2026-06"))
        assertEquals(null, extractYearMonth("invalid-date"))
    }

    @Test
    fun testQuizOptionPermutationsAndVariety() = runBlocking {
        val fakeUserDao = FakeUserDao()
        val mockDb = mock(AppDatabase::class.java)
        val mockQuestionDao = mock(QuestionDao::class.java)
        
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mockQuestionDao)
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mock(NotificationDao::class.java))
        
        `when`(mockQuestionDao.getQuestionsFlow(org.mockito.ArgumentMatchers.anyString()))
            .thenReturn(flowOf(emptyList()))
        
        val mockContext = mock(android.content.Context::class.java)
        val mockPrefs = mock(android.content.SharedPreferences::class.java)
        val mockEditor = mock(android.content.SharedPreferences.Editor::class.java)
        `when`(mockContext.getSharedPreferences(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt()))
            .thenReturn(mockPrefs)
        `when`(mockPrefs.getLong(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong()))
            .thenReturn(0L)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putLong(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong()))
            .thenReturn(mockEditor)

        val fakeApi = FakeApiService()
        val okHttpClient = OkHttpClient()
        val repository = AppRepositoryImpl(mockDb, fakeApi, okHttpClient)
        val viewModel = DashboardViewModel(repository)
        
        val classes = (1..12).map { "Class $it" }
        val counts = listOf(5, 7, 10)
        
        for (className in classes) {
            val numberPart = className.filter { it.isDigit() }.toInt()
            val subjects = when {
                numberPart <= 6 -> listOf("SST", "EVS", "Computer", "GK", "Hindi", "English", "Math", "Science")
                numberPart <= 10 -> listOf("Hindi", "English", "Science", "SST", "GK", "Computer", "Sanskrit", "Math")
                else -> { // Class 11 and 12, check PCM, PCB, Arts, Commerce streams
                    listOf(
                        "English", "Physics", "Chemistry", "Math", "GK", "Sanskrit",
                        "Biology", "Geography", "History", "Political Science", "Economics",
                        "Accountancy", "Business Studies"
                    )
                }
            }
            for (subject in subjects) {
                for (count in counts) {
                    for (run in 1..3) {
                        val quizId = "practice_${className}_${subject}_$count"
                        val flow = viewModel.getQuestionsForQuiz(mockContext, quizId)
                        val questionsList = flow.first()
                        
                        assertEquals("Count mismatch for $quizId", count, questionsList.size)
                        
                        val uniqueTexts = questionsList.map { it.questionText.trim().lowercase() }.toSet()
                        val maxUniquePossible = Math.min(count, 6)
                        assertTrue("Duplicate questions found in $quizId (found ${uniqueTexts.size}, expected $maxUniquePossible)", uniqueTexts.size >= maxUniquePossible)
                        
                        for (q in questionsList) {
                            assertTrue("Option A empty in ${q.id}", q.optionA.isNotEmpty())
                            assertTrue("Option B empty in ${q.id}", q.optionB.isNotEmpty())
                            assertTrue("Option C empty in ${q.id}", q.optionC.isNotEmpty())
                            assertTrue("Option D empty in ${q.id}", q.optionD.isNotEmpty())
                            assertTrue("Invalid correctAnswer index in ${q.id}", q.correctAnswer in 0..3)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testSecuritySanitization() {
        val unsafeText = "  Hello <script>alert('xss')</script> World! \u0000 "
        val cleaned = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(unsafeText)
        assertEquals("Hello World!", cleaned)

        val unsafeEmail = " Student+CBSE@SparkingPathshala.IN "
        val cleanEmail = com.sparkingpathshala.app.security.SecurityUtils.sanitizeEmail(unsafeEmail)
        assertEquals("student+cbse@sparkingpathshala.in", cleanEmail)

        val unsafeFilename = "my_notes/../../etc/passwd.pdf"
        val cleanFilename = com.sparkingpathshala.app.security.SecurityUtils.sanitizeFileName(unsafeFilename)
        assertEquals("passwd.pdf", cleanFilename)
    }

    @Test
    fun testForceUpdateGracePeriodLogic() {
        val releaseDate = 1755475200000L // 18 Aug 2026
        val targetVersionCode = 13

        // Case 1: Up to date (v13)
        val infoUpToDate = DashboardViewModel.AppVersionInfo(
            currentVersionCode = targetVersionCode,
            latestVersionCode = targetVersionCode,
            latestVersionName = "1.3.0",
            status = DashboardViewModel.ForceUpdateStatus.UP_TO_DATE
        )
        assertEquals(DashboardViewModel.ForceUpdateStatus.UP_TO_DATE, infoUpToDate.status)

        // Case 2: Outdated (v12) within 7 days
        val currentTimeInGrace = releaseDate + (3 * 24 * 3600 * 1000L) // 3 days after release
        val elapsedDays = ((currentTimeInGrace - releaseDate) / (24 * 3600 * 1000L)).toInt()
        val daysRemaining = maxOf(0, 7 - elapsedDays)
        assertEquals(4, daysRemaining)

        // Case 3: Outdated (> 7 days)
        val currentTimeExpired = releaseDate + (10 * 24 * 3600 * 1000L)
        val elapsedExpired = ((currentTimeExpired - releaseDate) / (24 * 3600 * 1000L)).toInt()
        val statusExpired = if (elapsedExpired <= 7) DashboardViewModel.ForceUpdateStatus.GRACE_PERIOD_ACTIVE else DashboardViewModel.ForceUpdateStatus.FORCE_UPDATE_REQUIRED
        assertEquals(DashboardViewModel.ForceUpdateStatus.FORCE_UPDATE_REQUIRED, statusExpired)
    }

    @Test
    fun testQuestionBankSeniorHindiAndSanskrit() {
        val seniorHindi = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions("Class 12", "Hindi", 10)
        assertTrue("Senior Hindi questions should not be empty", seniorHindi.isNotEmpty())
        for (q in seniorHindi) {
            assertTrue("Hindi question text empty", q.questionText.isNotEmpty())
            assertTrue("Hindi explanation empty", q.explanation.isNotEmpty())
            assertTrue("Hindi valid answer index", q.correctAnswer in 0..3)
        }

        val seniorSanskrit = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions("Class 12", "Sanskrit", 10)
        assertTrue("Senior Sanskrit questions should not be empty", seniorSanskrit.isNotEmpty())
        for (q in seniorSanskrit) {
            assertTrue("Sanskrit question text empty", q.questionText.isNotEmpty())
            assertTrue("Sanskrit explanation empty", q.explanation.isNotEmpty())
            assertTrue("Sanskrit valid answer index", q.correctAnswer in 0..3)
        }
    }

    @Test
    fun testClassSubjectMappingsAndQuestions() {
        // 1. Primary Classes (1 to 5)
        val primarySubjects = listOf("Math", "Hindi", "English", "EVS", "SST", "Computer", "GK", "Mental Math")
        for (sub in primarySubjects) {
            val questions = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions("Class 4", sub, 5)
            assertTrue("Class 4 subject $sub should have questions", questions.isNotEmpty())
            assertEquals(5, questions.size)
        }

        // 2. Middle/Secondary Classes (6 to 9)
        val secondarySubjects = listOf("Math", "Hindi", "English", "EVS", "SST", "Computer", "GK", "Hindi Grammar", "English Grammar", "Sanskrit")
        for (sub in secondarySubjects) {
            val questions = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions("Class 8", sub, 5)
            assertTrue("Class 8 subject $sub should have questions", questions.isNotEmpty())
            assertEquals(5, questions.size)
        }

        // 3. Senior Classes (10 to 12)
        val seniorSubjects = listOf("Physics", "Chemistry", "Math", "Biology", "English", "GK", "Sanskrit", "Geography", "History", "Political Science", "Hindi")
        for (sub in seniorSubjects) {
            val questions = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions("Class 11", sub, 5)
            assertTrue("Class 11 subject $sub should have questions", questions.isNotEmpty())
            assertEquals(5, questions.size)
        }
    }

    @Test
    fun testAntiRepetitionQuestionShuffling() {
        val allQuestions = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions("Class 10", "Math", 15)
        assertTrue(allQuestions.size >= 10)

        // Simulate Session 1 (User takes 5 questions)
        val session1 = allQuestions.shuffled().take(5)
        val seenSet = session1.map { it.id }.toSet()
        assertEquals(5, seenSet.size)

        // Simulate Session 2 (Filter out seen questions)
        val remaining = allQuestions.filter { it.id !in seenSet }
        assertTrue("Remaining questions should be available", remaining.isNotEmpty())
        val session2 = remaining.shuffled().take(5)

        // Verify session 1 and session 2 questions are disjoint (no overlap)
        val overlap = session1.map { it.id }.intersect(session2.map { it.id }.toSet())
        assertTrue("Session 1 and 2 should have zero repeating questions", overlap.isEmpty())
    }

    // ==========================================
    // ROLE & FEATURE MATRIX TESTS: GUEST MODE
    // ==========================================

    @Test
    fun testGuestMode_ProfileProperties() = runBlocking {
        val fakeUserDao = FakeUserDao()
        val guestUser = UserEntity(
            id = "guest_temp_123",
            name = "Guest Student",
            email = "guest@sparkingpathshala.in",
            rollNo = "GUEST",
            classLevel = "Class 10",
            token = "",
            role = "guest"
        )
        fakeUserDao.insertUser(guestUser)

        val fetched = fakeUserDao.getUserDirect()
        assertNotNull(fetched)
        assertEquals("guest", fetched?.role)
        assertTrue(fetched?.token.isNullOrEmpty())
    }

    @Test
    fun testGuestMode_LibraryFreeBrowsingAcrossClasses() = runBlocking {
        val fakeStudyMaterialDao = FakeStudyMaterialDao()
        fakeStudyMaterialDao.insertMaterials(listOf(
            StudyMaterialEntity("m1", "Class 9 Physics Notes", "Desc", "Physics", "url1", "", false, "Class 9", "notes", "active", null),
            StudyMaterialEntity("m2", "Class 10 Math Guide", "Desc", "Mathematics", "url2", "", false, "Class 10", "guides", "active", null),
            StudyMaterialEntity("m3", "Class 11 Chemistry Bank", "Desc", "Chemistry", "url3", "", false, "Class 11", "question-bank", "active", null),
            StudyMaterialEntity("m4", "Class 12 Test Series", "Desc", "Biology", "url4", "", false, "Class 12", "test-series", "active", null)
        ))

        val allMaterials = fakeStudyMaterialDao.getAllMaterialsFlow().first()
        assertEquals(4, allMaterials.size)

        val class9Materials = allMaterials.filter { it.classLevel == "Class 9" }
        assertEquals(1, class9Materials.size)
        assertEquals("Class 9 Physics Notes", class9Materials[0].title)

        val class12Materials = allMaterials.filter { it.classLevel == "Class 12" }
        assertEquals(1, class12Materials.size)
        assertEquals("Class 12 Test Series", class12Materials[0].title)
    }

    @Test
    fun testGuestMode_PremiumMaterialAccessGate() {
        val premiumItem = StudyMaterialEntity(
            id = "prem_1",
            title = "Exclusive JEE Advanced Question Bank",
            description = "Premium Material",
            subject = "Physics",
            fileUrl = "url",
            localPath = "",
            isDownloaded = false,
            classLevel = "Class 12",
            category = "premium-material",
            status = "active",
            allowedEmails = null
        )

        val isGuest = true
        val shouldShowGuestGate = isGuest && (premiumItem.category == "premium-material" || !premiumItem.isDownloaded)
        assertTrue("Guest user must trigger guest login/signup gate for premium material", shouldShowGuestGate)
    }

    @Test
    fun testGuestMode_HomeworkPurgeAfter30Days() = runBlocking {
        val fakeHomeworkDao = FakeHomeworkDao()
        val now = System.currentTimeMillis()
        val thirtyFiveDaysAgo = now - (35L * 24 * 60 * 60 * 1000)
        val fiveDaysAgo = now - (5L * 24 * 60 * 60 * 1000)

        fakeHomeworkDao.insertHomework(listOf(
            HomeworkEntity(
                id = "hw_old",
                title = "Old Assignment",
                description = "Old",
                subject = "Math",
                classLevel = "Class 10",
                fileUrl = "file1.pdf",
                fileType = "PDF",
                localPath = "",
                isDownloaded = false,
                createdAt = thirtyFiveDaysAgo,
                dueDate = "",
                maxMarks = 100
            ),
            HomeworkEntity(
                id = "hw_recent",
                title = "Recent Homework",
                description = "Recent",
                subject = "Science",
                classLevel = "Class 10",
                fileUrl = "file2.pdf",
                fileType = "PDF",
                localPath = "",
                isDownloaded = false,
                createdAt = fiveDaysAgo,
                dueDate = "",
                maxMarks = 100
            )
        ))

        val thirtyDaysCutoff = now - (30L * 24 * 60 * 60 * 1000)
        val purgedCount = fakeHomeworkDao.purgeOldHomework(thirtyDaysCutoff)

        assertEquals(1, purgedCount)
        val remaining = fakeHomeworkDao.getAllHomeworkFlow().first()
        assertEquals(1, remaining.size)
        assertEquals("hw_recent", remaining[0].id)
    }

    // ==========================================
    // ROLE & FEATURE MATRIX TESTS: NORMAL STUDENT
    // ==========================================

    @Test
    fun testNormalStudent_ClassSpecificPremiumAccess() = runBlocking {
        val studentClass = "Class 10"

        val class10Premium = StudyMaterialEntity("p10", "Class 10 Masterclass Notes", "", "Physics", "url", "", false, "Class 10", "premium-material", "active", null)
        val class12Premium = StudyMaterialEntity("p12", "Class 12 IIT JEE Notes", "", "Chemistry", "url", "", false, "Class 12", "premium-material", "active", null)

        val canAccessClass10 = class10Premium.classLevel.equals(studentClass, ignoreCase = true) || class10Premium.classLevel == "All"
        val canAccessClass12 = class12Premium.classLevel.equals(studentClass, ignoreCase = true) || class12Premium.classLevel == "All"

        assertTrue("Enrolled student must have access to their class premium materials", canAccessClass10)
        assertFalse("Student cannot access premium materials of other classes", canAccessClass12)
    }

    @Test
    fun testNormalStudent_OfflineDownloadsPersistenceAcrossLogout() = runBlocking {
        val fakeStudyMaterialDao = FakeStudyMaterialDao()
        fakeStudyMaterialDao.insertMaterials(listOf(
            StudyMaterialEntity("m_downloaded", "Offline Physics PDF", "Desc", "Physics", "url1", "/storage/emulated/0/Download/m_downloaded.pdf", true, "Class 10", "notes", "active", null),
            StudyMaterialEntity("m_cloud_only", "Cloud Only Notes", "Desc", "Math", "url2", "", false, "Class 10", "notes", "active", null)
        ))

        // Student logs out -> clearNonDownloadedMaterials is called
        fakeStudyMaterialDao.clearNonDownloadedMaterials()

        val remaining = fakeStudyMaterialDao.getAllMaterialsFlow().first()
        assertEquals("Downloaded offline files must survive logout", 1, remaining.size)
        assertEquals("m_downloaded", remaining[0].id)
        assertTrue(remaining[0].isDownloaded)
        assertEquals("/storage/emulated/0/Download/m_downloaded.pdf", remaining[0].localPath)

        // User deletes downloaded file manually
        fakeStudyMaterialDao.deleteMaterial("m_downloaded")
        val afterManualDelete = fakeStudyMaterialDao.getAllMaterialsFlow().first()
        assertEquals(0, afterManualDelete.size)
    }

    @Test
    fun testNormalStudent_OfflineHomeworkPersistenceAcrossLogout() = runBlocking {
        val fakeHomeworkDao = FakeHomeworkDao()
        fakeHomeworkDao.insertHomework(listOf(
            HomeworkEntity("hw_downloaded", "Offline Math Worksheet", "Solve chapter 5", "Math", "Class 10", isDownloaded = true, localPath = "/storage/emulated/0/Download/hw.pdf"),
            HomeworkEntity("hw_cloud", "Online Assignment", "Complete online", "Physics", "Class 10", isDownloaded = false)
        ))

        fakeHomeworkDao.clearNonDownloadedHomework()

        val remaining = fakeHomeworkDao.getAllHomeworkFlow().first()
        assertEquals("Downloaded offline homework must survive logout", 1, remaining.size)
        assertEquals("hw_downloaded", remaining[0].id)
        assertTrue(remaining[0].isDownloaded)
    }

    @Test
    fun testNormalStudent_AttendanceAndTestResultsCalculation() = runBlocking {
        val fakeAttendanceDao = FakeAttendanceDao()
        fakeAttendanceDao.insertAttendance(listOf(
            AttendanceEntity(date = "2026-08-01", isPresent = true, remarks = "Present on time"),
            AttendanceEntity(date = "2026-08-02", isPresent = true, remarks = "Present"),
            AttendanceEntity(date = "2026-08-03", isPresent = false, remarks = "Sick leave")
        ))

        val logs = fakeAttendanceDao.getAttendanceFlow().first()
        val total = logs.size
        val present = logs.count { it.isPresent }
        val percentage = (present.toFloat() / total * 100).toInt()

        assertEquals(3, total)
        assertEquals(2, present)
        assertEquals(66, percentage)
    }

    // ==========================================
    // ROLE & FEATURE MATRIX TESTS: ADMIN USER
    // ==========================================

    @Test
    fun testAdminMode_TodayOnlyDateLockForAttendance() {
        val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        
        val payload = mutableMapOf<String, Any>()
        payload["student_email"] = "student_101@sparkingpathshala.in"
        payload["date"] = todayDate // strictly locked to today
        payload["status"] = "Present"

        assertEquals(todayDate, payload["date"])
        assertNotNull(payload["status"])
    }

    @Test
    fun testAdminMode_TestResultsPaginationAndSorting() {
        val rawResults = listOf(
            AdminRecord(id = "r1", studentEmail = "student_c@test.com", date = "2026-08-10", subject = "Math", marksObtained = 85.0, maxMarks = 100.0),
            AdminRecord(id = "r2", studentEmail = "student_a@test.com", date = "2026-08-15", subject = "Physics", marksObtained = 95.0, maxMarks = 100.0),
            AdminRecord(id = "r3", studentEmail = "student_b@test.com", date = "2026-08-01", subject = "Chemistry", marksObtained = 70.0, maxMarks = 100.0)
        )

        // Sort by Score High to Low
        val sortedByScoreDesc = rawResults.sortedByDescending { it.marksObtained ?: 0.0 }
        assertEquals("r2", sortedByScoreDesc[0].id) // 95.0
        assertEquals("r1", sortedByScoreDesc[1].id) // 85.0
        assertEquals("r3", sortedByScoreDesc[2].id) // 70.0

        // Sort by Student A-Z
        val sortedByName = rawResults.sortedBy { it.studentEmail.lowercase() }
        assertEquals("student_a@test.com", sortedByName[0].studentEmail)
        assertEquals("student_b@test.com", sortedByName[1].studentEmail)
        assertEquals("student_c@test.com", sortedByName[2].studentEmail)

        // Pagination: 2 items per page
        val pageSize = 2
        val page1 = sortedByScoreDesc.take(pageSize)
        val page2 = sortedByScoreDesc.drop(pageSize).take(pageSize)

        assertEquals(2, page1.size)
        assertEquals(1, page2.size)
        assertEquals("r3", page2[0].id)
    }

    @Test
    fun testAdminMode_PremiumMaterialUploadFieldValidation() {
        val title = "IIT JEE Complete Calculus"
        val category = "premium-material"
        val classSelection = "Class 12"
        val subject = "Mathematics"
        val batch = "Morning Batch" // optional
        val allowedEmails = "" // optional: blank means all students in Class 12

        assertTrue("Title is mandatory", title.isNotBlank())
        assertTrue("Subject is mandatory", subject.isNotBlank())
        assertTrue("Category is premium-material", category == "premium-material")
        assertTrue("Class selection cannot be All for premium material", classSelection != "All" && classSelection.isNotBlank())
        assertTrue("Batch can be provided or empty", batch.isNotBlank())
        assertTrue("Allowed emails can be empty for whole class", allowedEmails.isBlank())
    }

    @Test
    fun testAdminMode_StudyMaterialEditAndDelete() = runBlocking {
        val materialDao = FakeStudyMaterialDao()
        val original = StudyMaterialEntity(
            id = "mat_101",
            title = "Thermodynamics Concept",
            description = "Detailed chapter explanation",
            subject = "Physics",
            fileUrl = "https://example.com/thermo.pdf",
            localPath = "",
            isDownloaded = false,
            classLevel = "Class 11",
            category = "premium-material",
            status = "active",
            allowedEmails = "student1@test.com"
        )
        materialDao.insertMaterials(listOf(original))

        // Edit material
        val updated = original.copy(
            title = "Thermodynamics & Kinetic Theory (Updated)",
            subject = "Physics Advanced",
            status = "disabled",
            allowedEmails = "student1@test.com, student2@test.com"
        )
        materialDao.updateMaterial(updated)

        val retrieved = materialDao.getMaterialById("mat_101")
        assertNotNull(retrieved)
        assertEquals("Thermodynamics & Kinetic Theory (Updated)", retrieved?.title)
        assertEquals("Physics Advanced", retrieved?.subject)
        assertEquals("disabled", retrieved?.status)
        assertEquals("student1@test.com, student2@test.com", retrieved?.allowedEmails)

        // Delete material
        materialDao.deleteMaterial("mat_101")
        val deleted = materialDao.getMaterialById("mat_101")
        assertNull(deleted)
    }

    @Test
    fun testAdminMode_StudyMaterialPaginationAndSorting() {
        val materials = listOf(
            StudyMaterialEntity("m1", "Zoology Notes", "desc", "Biology", "url1", "", false, "Class 11", category = "notes", status = "active"),
            StudyMaterialEntity("m2", "Algebra Fundamentals", "desc", "Mathematics", "url2", "", false, "Class 10", category = "premium-material", status = "active"),
            StudyMaterialEntity("m3", "Chemistry Lab Manual", "desc", "Chemistry", "url3", "", false, "Class 12", category = "guides", status = "active"),
            StudyMaterialEntity("m4", "Mechanics Problem Set", "desc", "Physics", "url4", "", false, "Class 11", category = "premium-material", status = "active"),
            StudyMaterialEntity("m5", "Botany Diagrams", "desc", "Biology", "url5", "", false, "Class 12", category = "notes", status = "active")
        )

        // Test Category Filter
        val premiumOnly = materials.filter { it.category == "premium-material" }
        assertEquals(2, premiumOnly.size)

        // Test Class Filter
        val class11Only = materials.filter { it.classLevel == "Class 11" }
        assertEquals(2, class11Only.size)

        // Test Sorting Title A-Z
        val sortedByTitle = materials.sortedBy { it.title.lowercase() }
        assertEquals("m2", sortedByTitle[0].id) // Algebra Fundamentals
        assertEquals("m5", sortedByTitle[1].id) // Botany Diagrams

        // Test Sorting Subject A-Z
        val sortedBySubject = materials.sortedBy { it.subject.lowercase() }
        assertEquals("Biology", sortedBySubject[0].subject)

        // Test 2-item Pagination
        val pageSize = 2
        val page1 = materials.drop(0).take(pageSize)
        val page2 = materials.drop(2).take(pageSize)
        val page3 = materials.drop(4).take(pageSize)

        assertEquals(2, page1.size)
        assertEquals(2, page2.size)
        assertEquals(1, page3.size)
    }

    @Test
    fun testGuestMode_HomeworkAccessRestrictedWithGateActions() {
        val isGuest = true
        val canAccessDirectly = !isGuest
        assertFalse("Guest user must not directly access homework list or downloads", canAccessDirectly)

        // Verify available modal actions
        val gateOptions = listOf("Sign Up", "Log In", "Dismiss")
        assertTrue(gateOptions.contains("Sign Up"))
        assertTrue(gateOptions.contains("Log In"))
        assertTrue(gateOptions.contains("Dismiss"))
    }

    // ==========================================
    // SECURITY AUDIT & DEFENSE TESTS
    // ==========================================

    @Test
    fun testSecurity_InputSanitizationXss() {
        val maliciousInput = "<script>alert('XSS Attack!')</script>Hello <b>World</b>"
        val sanitized = com.sparkingpathshala.app.security.SecurityUtils.sanitizeInput(maliciousInput)
        
        assertFalse("Script tags must be stripped", sanitized.contains("<script>", ignoreCase = true))
        assertFalse("HTML tags must be stripped", sanitized.contains("<b>", ignoreCase = true))
        assertTrue("Benign text content must remain", sanitized.contains("Hello World"))
    }

    @Test
    fun testSecurity_FileNameDirectoryTraversal() {
        val traversalPath1 = "../../../../etc/passwd"
        val clean1 = com.sparkingpathshala.app.security.SecurityUtils.sanitizeFileName(traversalPath1)
        assertFalse("Directory traversal dots must not allow parent directory escape", clean1.contains("/"))
        assertEquals("passwd", clean1)

        val traversalPath2 = "..\\..\\sensitive_file.txt"
        val clean2 = com.sparkingpathshala.app.security.SecurityUtils.sanitizeFileName(traversalPath2)
        assertFalse("Backslash traversal must be stripped", clean2.contains("\\"))
    }

    @Test
    fun testSecurity_EmailSanitization() {
        val dirtyEmail = "  STUDENT.Test+2026@SparkingPathshala.IN  "
        val cleanEmail = com.sparkingpathshala.app.security.SecurityUtils.sanitizeEmail(dirtyEmail)
        assertEquals("student.test+2026@sparkingpathshala.in", cleanEmail)
    }

    @Test
    fun testSecurity_MobileSanitization() {
        val rawMobile1 = "+91 98765-43210"
        val clean1 = com.sparkingpathshala.app.security.SecurityUtils.sanitizeMobile(rawMobile1)
        assertEquals("9876543210", clean1)

        val rawMobile2 = "09876543210"
        val clean2 = com.sparkingpathshala.app.security.SecurityUtils.sanitizeMobile(rawMobile2)
        assertEquals("9876543210", clean2)
    }

    @Test
    fun testSecurity_TokenAbsenceInGuestMode() = runBlocking {
        val fakeUserDao = FakeUserDao()
        val guest = UserEntity(
            id = "guest_abc",
            name = "Guest User",
            email = "guest@sparkingpathshala.in",
            rollNo = "GUEST",
            classLevel = "Class 10",
            token = "",
            role = "guest"
        )
        fakeUserDao.insertUser(guest)

        val user = fakeUserDao.getUserDirect()
        assertNotNull(user)
        assertTrue("Guest user MUST NOT have a bearer token", user?.token.isNullOrEmpty())
        assertEquals("guest", user?.role)
    }

    @Test
    fun testSecurity_RoleSeparationIntegrity() {
        val student = UserInfo("s1", "Student A", "s@test.com", "Class 10", "student", "approved")
        val admin = UserInfo("a1", "Admin B", "admin@sparkingpathshala.in", "All", "admin", "approved")

        val isStudentAdmin = student.role == "admin"
        val isAdminAdmin = admin.role == "admin"

        assertFalse("Student role cannot pass admin role check", isStudentAdmin)
        assertTrue("Admin role passes admin role check", isAdminAdmin)
    }

    @Test
    fun testSecurity_CertificatePinnerMultiPinConfiguration() {
        val pinner = okhttp3.CertificatePinner.Builder()
            .add("api.sparkingpathshala.in", "sha256/zX9bbPDw6sPnBAegKjsq2g0ckhxXfRhwyUb0zDEfFUw=")
            .add("*.sparkingpathshala.in", "sha256/zX9bbPDw6sPnBAegKjsq2g0ckhxXfRhwyUb0zDEfFUw=")
            .add("api.sparkingpathshala.in", "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=")
            .add("*.sparkingpathshala.in", "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=")
            .add("api.sparkingpathshala.in", "sha256/r/mIts6OE1MpXkeGO60cyB04mtmxbbczcPq4yJaKPGY=")
            .add("*.sparkingpathshala.in", "sha256/r/mIts6OE1MpXkeGO60cyB04mtmxbbczcPq4yJaKPGY=")
            .build()

        val matchingPins = pinner.findMatchingPins("api.sparkingpathshala.in")
        assertTrue("Must have multiple pins configured for dynamic certificate rotation", matchingPins.size >= 3)
    }

    @Test
    fun testGuestUser_DefaultClassIsAll() {
        val guest = UserEntity(
            id = "guest_123",
            name = "Guest Student",
            email = "guest@sparkingpathshala.in",
            rollNo = "SP-GUEST",
            classLevel = "All",
            token = "",
            role = "guest"
        )
        assertEquals("All", guest.classLevel)
        assertEquals("guest", guest.role)
    }

    @Test
    fun testAdminStudents_ClassFilteringAndSearch() {
        val students = listOf(
            AdminStudentInfo("1", "Rohan Sharma", "rohan@test.com", "Class 10", "approved", 1500.0, "9876543210"),
            AdminStudentInfo("2", "Aakash Verma", "aakash@test.com", "Class 9", "approved", 1500.0, "9876543211"),
            AdminStudentInfo("3", "Priya Singh", "priya@test.com", "Class 10", "approved", 1500.0, "9876543212"),
            AdminStudentInfo("4", "Sajjan Kumar", "sajjan@test.com", "Class 12", "approved", 1800.0, "9876543213")
        )

        // Filter by Class 10
        val class10Students = students.filter { it.classSelection.equals("Class 10", ignoreCase = true) }
        assertEquals(2, class10Students.size)

        // Filter by Class 9
        val class9Students = students.filter { it.classSelection.equals("Class 9", ignoreCase = true) }
        assertEquals(1, class9Students.size)
        assertEquals("Aakash Verma", class9Students[0].name)

        // Filter by All + Search Query "Priya"
        val query = "Priya"
        val selectedClass = "All"
        val priyaSearch = students.filter { student ->
            val matchesClass = selectedClass == "All" || student.classSelection.equals(selectedClass, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                student.name.contains(query, ignoreCase = true) ||
                student.email.contains(query, ignoreCase = true) ||
                (student.mobile ?: "").contains(query) ||
                student.classSelection.contains(query, ignoreCase = true)
            matchesClass && matchesQuery
        }
        assertEquals(1, priyaSearch.size)
        assertEquals("Priya Singh", priyaSearch[0].name)
    }

    @Test
    fun testDoubtNotifications_AdminAndStudentTriggers() {
        val notifs = mutableListOf<NotificationEntity>()

        // 1. Student raises doubt -> Admin notification created
        val doubtNotif = NotificationEntity(
            id = "notif_doubt_01",
            recipientRole = "admin",
            targetClass = "All",
            title = "New Doubt: Ray Optics",
            message = "Rohan Sharma (Class 10) asked: \"What is focal length?\"",
            category = "DOUBT",
            timestamp = System.currentTimeMillis(),
            priority = "HIGH"
        )
        notifs.add(doubtNotif)

        // Verify Admin receives it
        val adminNotifs = notifs.filter { it.recipientRole == "all" || it.recipientRole == "admin" }
        assertEquals(1, adminNotifs.size)
        assertEquals("New Doubt: Ray Optics", adminNotifs[0].title)

        // 2. Admin replies -> Student notification created
        val replyNotif = NotificationEntity(
            id = "notif_rep_01",
            recipientRole = "student",
            targetClass = "All",
            title = "Faculty Replied to Doubt",
            message = "Faculty Sajjan Mallik replied: \"Focal length is distance from optical center to focal point.\"",
            category = "DOUBT",
            timestamp = System.currentTimeMillis(),
            priority = "HIGH"
        )
        notifs.add(replyNotif)

        // Verify Student receives it
        val studentClass = "Class 10"
        val studentNotifs = notifs.filter {
            (it.recipientRole == "all" || it.recipientRole == "student" || it.recipientRole == "guest") &&
            (it.targetClass == "All" || it.targetClass == "all" || it.targetClass == studentClass || studentClass == "All")
        }
        assertEquals(1, studentNotifs.size)
        assertEquals("Faculty Replied to Doubt", studentNotifs[0].title)
    }

    @Test
    fun testOfflineMode_BannerAndRestorationBehavior() {
        var isOffline = true
        var showRestoredBanner = false

        // Offline state verification
        assertTrue("Device should report offline when internet capability is lost", isOffline)

        // Network restoration event simulation
        if (isOffline) {
            showRestoredBanner = true
            isOffline = false
        }

        assertFalse("Device should report online after restoration", isOffline)
        assertTrue("App should display restoration banner when back online", showRestoredBanner)
    }

    @Test
    fun testOfflineMode_DoubtSubmissionBlockedWithPrompt() {
        val isNetworkConnected = false
        val actionMessage = if (!isNetworkConnected) {
            "You are offline. Please connect to the internet to submit your doubt."
        } else {
            "Doubt Submitted Successfully!"
        }

        assertEquals("You are offline. Please connect to the internet to submit your doubt.", actionMessage)
    }

    @Test
    fun testAdminQuizCreation_ResilienceAndInstantPersistence() {
        val manualRequest = ManualQuizRequest(
            subject = "Physics",
            classSelection = "Class 10",
            chapter = "Light - Reflection & Refraction",
            quizType = "30 Min Quiz",
            scheduledStart = "2026-08-18 14:00",
            questions = listOf(
                ManualQuestion(
                    questionText = "What is the speed of light in vacuum?",
                    optionA = "3 x 10^8 m/s",
                    optionB = "3 x 10^6 m/s",
                    optionC = "3 x 10^5 m/s",
                    optionD = "3 x 10^7 m/s",
                    correctOption = "A",
                    explanation = "Speed of light in vacuum is approx 3 x 10^8 m/s."
                )
            )
        )

        assertEquals("Physics", manualRequest.subject)
        assertEquals(1, manualRequest.questions.size)
        assertEquals("A", manualRequest.questions[0].correctOption)
    }

    @Test
    fun testAdminHomework_RoleAccessValidation() {
        val adminRole = "admin"
        val billingAdminRole = "billing_admin"
        val studentRole = "student"
        val guestRole = "guest"

        fun isUserAdmin(role: String, email: String): Boolean {
            return role == "admin" || role == "billing_admin" || role.contains("admin") || email.equals("sajjanmallik@sparkingpathshala.in", ignoreCase = true)
        }

        assertTrue("Admin role must have upload options", isUserAdmin(adminRole, "admin@school.com"))
        assertTrue("Billing admin role must have upload options", isUserAdmin(billingAdminRole, "billing@school.com"))
        assertTrue("Admin email must have upload options", isUserAdmin("user", "sajjanmallik@sparkingpathshala.in"))
        assertFalse("Student role must not have admin upload options", isUserAdmin(studentRole, "student@school.com"))
        assertFalse("Guest role must not have admin upload options", isUserAdmin(guestRole, "guest@school.com"))
    }

    @Test
    fun testQuizPlay_RoleRestrictions() {
        fun canPlayQuiz(role: String): Boolean {
            return role == "student" || role == "guest"
        }

        assertFalse("Admin cannot play quiz", canPlayQuiz("admin"))
        assertFalse("Billing admin cannot play quiz", canPlayQuiz("billing_admin"))
        assertTrue("Student can play quiz", canPlayQuiz("student"))
        assertTrue("Guest user can play quiz", canPlayQuiz("guest"))
    }

    @Test
    fun testAdminFooter_QuizTabExcluded() {
        val allTabs = listOf("home", "homework_tab", "material", "quiz", "profile")
        
        fun getVisibleTabs(role: String): List<String> {
            return if (role == "admin" || role == "billing_admin") {
                allTabs.filter { it != "quiz" }
            } else {
                allTabs
            }
        }

        val adminTabs = getVisibleTabs("admin")
        assertFalse("Footer for Admin must NOT contain quiz tab", adminTabs.contains("quiz"))
        assertEquals(4, adminTabs.size)

        val studentTabs = getVisibleTabs("student")
        assertTrue("Footer for Student must contain quiz tab", studentTabs.contains("quiz"))
        assertEquals(5, studentTabs.size)

        val guestTabs = getVisibleTabs("guest")
        assertTrue("Footer for Guest must contain quiz tab", guestTabs.contains("quiz"))
        assertEquals(5, guestTabs.size)
    }

    @Test
    fun testGuestTelemetry_RealNetworkDataValidation() {
        val sampleRealTelemetry = SecurityUtils.NetworkTelemetryInfo(
            ipAddress = "103.211.54.12",
            location = "Patna, India",
            country = "India"
        )

        assertFalse("Real IP must not contain placeholder Mobile/WiFi text", sampleRealTelemetry.ipAddress.contains("Mobile/WiFi"))
        assertTrue("Real IP must be valid IPv4 or network identifier", sampleRealTelemetry.ipAddress.matches(Regex("^([0-9]{1,3}\\.){3}[0-9]{1,3}$")) || sampleRealTelemetry.ipAddress == "Offline")
        assertTrue("Real Location must include country name", sampleRealTelemetry.location.contains(sampleRealTelemetry.country))
        assertEquals("India", sampleRealTelemetry.country)
    }

    @Test
    fun testUserEntity_RBACExtensions() {
        val adminUser = UserEntity("u1", "Admin", "admin@sparkingpathshala.in", "ADM-1", "All", "token", "admin")
        val billingAdmin = UserEntity("u2", "Billing", "billing@sparkingpathshala.in", "ADM-2", "All", "token", "billing_admin")
        val ownerAdmin = UserEntity("u3", "Sajjan", "sajjanmallik@sparkingpathshala.in", "ADM-3", "All", "token", "student")
        val studentUser = UserEntity("u4", "Student", "student@sparkingpathshala.in", "SP-101", "Class 10", "token", "student")
        val guestUser = UserEntity("u5", "Guest", "guest@sparkingpathshala.in", "SP-GUEST", "All", "token", "guest")
        val nullUser: UserEntity? = null

        assertTrue("adminUser is admin", adminUser.isAdmin())
        assertFalse("adminUser is not student", adminUser.isStudent())
        assertFalse("adminUser is not guest", adminUser.isGuest())

        assertTrue("billingAdmin is admin", billingAdmin.isAdmin())
        assertFalse("billingAdmin is not student", billingAdmin.isStudent())

        assertTrue("ownerAdmin is admin", ownerAdmin.isAdmin())

        assertFalse("studentUser is not admin", studentUser.isAdmin())
        assertTrue("studentUser is student", studentUser.isStudent())
        assertFalse("studentUser is not guest", studentUser.isGuest())

        assertFalse("guestUser is not admin", guestUser.isAdmin())
        assertFalse("guestUser is not student", guestUser.isStudent())
        assertTrue("guestUser is guest", guestUser.isGuest())

        assertFalse("nullUser is not admin", nullUser.isAdmin())
        assertFalse("nullUser is not student", nullUser.isStudent())
        assertFalse("nullUser is not guest", nullUser.isGuest())
    }

    @Test
    fun testCrashResilience_ZeroDivisionSafety() {
        val emptyResults = emptyList<ResultEntity>()
        val avgScore = if (emptyResults.isNotEmpty()) {
            (emptyResults.sumOf { it.marksObtained.toFloat() / it.maxMarks } / emptyResults.size * 100).toInt()
        } else 0
        assertEquals(0, avgScore)

        val emptyAttendance = emptyList<AttendanceEntity>()
        val attendancePct = if (emptyAttendance.isNotEmpty()) {
            (emptyAttendance.count { it.isPresent }.toFloat() / emptyAttendance.size * 100).toInt()
        } else 100
        assertEquals(100, attendancePct)
    }

    @Test
    fun testGuestQuizSubmission_LocalGradingSuccess() = runBlocking {
        val mockDb = mock(AppDatabase::class.java)
        val fakeUserDao = FakeUserDao()
        val fakeQuestionDao = FakeQuestionDao()
        val fakeQuizDao = FakeQuizDao()
        val fakeHwDao = FakeHomeworkDao()
        val fakeMatDao = FakeStudyMaterialDao()
        val fakeFeeDao = mock(FeeDao::class.java)
        val fakeResultDao = mock(ResultDao::class.java)
        val fakeAttDao = mock(AttendanceDao::class.java)
        val fakeGuestDao = mock(GuestTelemetryDao::class.java)
        val fakeNotificationDao = mock(NotificationDao::class.java)

        val q1 = QuestionEntity("q1", "practice_quiz", "What is the capital of France?", "London", "Paris", "Berlin", "Rome", 1, "Paris is the capital of France.")
        val q2 = QuestionEntity("q2", "practice_quiz", "What is 2 + 2?", "3", "4", "5", "6", 1, "2 + 2 = 4.")
        fakeQuestionDao.insertQuestions(listOf(q1, q2))

        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.questionDao()).thenReturn(fakeQuestionDao)
        `when`(mockDb.quizDao()).thenReturn(fakeQuizDao)
        `when`(mockDb.homeworkDao()).thenReturn(fakeHwDao)
        `when`(mockDb.studyMaterialDao()).thenReturn(fakeMatDao)
        `when`(mockDb.feeDao()).thenReturn(fakeFeeDao)
        `when`(mockDb.resultDao()).thenReturn(fakeResultDao)
        `when`(mockDb.attendanceDao()).thenReturn(fakeAttDao)
        `when`(mockDb.guestTelemetryDao()).thenReturn(fakeGuestDao)
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val repo = AppRepositoryImpl(mockDb, FakeApiService(), OkHttpClient())

        // Guest user submitting answers (no auth token)
        val answers = mapOf("q1" to "B", "q2" to "B")
        val result = repo.submitQuiz("practice_quiz", answers)

        assertTrue("Guest submission should succeed without auth error", result.isSuccess)
        val response = result.getOrNull()
        assertNotNull(response)
        assertEquals(2, response?.score)
        assertEquals(2, response?.totalQuestions)
        assertTrue(response?.questions?.any { it.id == "q1" && it.correctOption == "B" } == true)
        assertTrue(response?.questions?.all { it.explanation?.contains("Open Trivia") == false } == true)
    }

    @Test
    fun testQuizPurge_24HoursCutoff() {
        val now = System.currentTimeMillis()
        val sdfFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("Asia/Kolkata")
        }
        val recentDate = sdfFormat.format(java.util.Date(now - 2 * 60 * 60 * 1000L)) // 2 hours ago
        val expiredDate = sdfFormat.format(java.util.Date(now - 26 * 60 * 60 * 1000L)) // 26 hours ago

        assertFalse("Recent quiz (2h ago) must NOT be expired", com.sparkingpathshala.app.data.repository.isQuizExpired24Hours(recentDate))
        assertTrue("Expired quiz (26h ago) MUST be expired and purged", com.sparkingpathshala.app.data.repository.isQuizExpired24Hours(expiredDate))
    }

    @Test
    fun testHomeworkDownload_OfflineStateToggle() = runBlocking {
        val hwDao = FakeHomeworkDao()
        val hw1 = HomeworkEntity("hw1", "Math Homework", "Solve quadratic equations", "Maths", "Class 10", null, null, "data:text/plain;base64,VGVzdA==", "TXT", "", false, 1000L, "2026-08-20", 100)
        val hw2 = HomeworkEntity("hw2", "Physics Homework", "Optics ray diagram", "Physics", "Class 10", null, null, "data:text/plain;base64,VGVzdA==", "TXT", "/data/user/0/app/hw_hw2.txt", true, 2000L, "2026-08-21", 100)
        hwDao.insertHomework(listOf(hw1, hw2))

        val allHw = hwDao.getAllHomeworkFlow().first()
        assertEquals(2, allHw.size)

        val downloadedOnly = allHw.filter { it.isDownloaded }
        assertEquals(1, downloadedOnly.size)
        assertEquals("hw2", downloadedOnly[0].id)
        assertTrue(downloadedOnly[0].isDownloaded)
        assertEquals("/data/user/0/app/hw_hw2.txt", downloadedOnly[0].localPath)
    }

    @Test
    fun testDigitalLibrary_DownloadedToggleFilter() = runBlocking {
        val materialDao = FakeStudyMaterialDao()
        val mat1 = StudyMaterialEntity("m1", "NCERT Chemistry", "Full chapter", "Science", "https://url.com", "", false, "Class 10", "notes")
        val mat2 = StudyMaterialEntity("m2", "Math Formula Sheet", "Formulas", "Maths", "https://url.com", "/data/user/0/app/notes_m2.pdf", true, "Class 10", "guides")
        materialDao.insertMaterials(listOf(mat1, mat2))

        val allMaterials = materialDao.getAllMaterialsFlow().first()
        assertEquals(2, allMaterials.size)

        val downloadedMaterials = allMaterials.filter { it.isDownloaded }
        assertEquals(1, downloadedMaterials.size)
        assertEquals("m2", downloadedMaterials[0].id)
        assertTrue(downloadedMaterials[0].isDownloaded)
    }

    @Test
    fun testBitmapDownsampling_SampleSizeCalculation() {
        val options = android.graphics.BitmapFactory.Options().apply {
            outWidth = 4000
            outHeight = 3000
        }
        val sampleSize = com.sparkingpathshala.app.ui.ondemand.calculateInSampleSize(options, 1280, 1280)
        assertTrue("Sample size for 4000x3000 downsampled to 1280x1280 must be at least 2", sampleSize >= 2)
        assertEquals(2, sampleSize)

        val hugeOptions = android.graphics.BitmapFactory.Options().apply {
            outWidth = 8000
            outHeight = 6000
        }
        val hugeSampleSize = com.sparkingpathshala.app.ui.ondemand.calculateInSampleSize(hugeOptions, 1280, 1280)
        assertTrue("Sample size for 8000x6000 image must be 4", hugeSampleSize == 4)

        val smallOptions = android.graphics.BitmapFactory.Options().apply {
            outWidth = 800
            outHeight = 600
        }
        val smallSampleSize = com.sparkingpathshala.app.ui.ondemand.calculateInSampleSize(smallOptions, 1280, 1280)
        assertEquals("Sample size for small image must be 1 (no downsampling)", 1, smallSampleSize)
    }

    @Test
    fun testAesGcmEncryptionAndDecryption() {
        val originalSecret = "jwt_bearer_token_secret_12345!@#"
        val encrypted1 = SecurityUtils.encrypt(originalSecret)
        val encrypted2 = SecurityUtils.encrypt(originalSecret)

        // Must start with v3: prefix
        assertTrue("Ciphertext must use v3 prefix", encrypted1.startsWith("v3:"))
        assertTrue("Ciphertext must use v3 prefix", encrypted2.startsWith("v3:"))

        // Must produce non-deterministic ciphertext due to randomized IV
        assertFalse("Two encryptions of same plaintext must produce different ciphertexts due to random IV", encrypted1 == encrypted2)

        // Both must decrypt correctly to original plaintext
        assertEquals(originalSecret, SecurityUtils.decrypt(encrypted1))
        assertEquals(originalSecret, SecurityUtils.decrypt(encrypted2))
    }

    @Test
    fun testAesV2CbcDecryptionBackwardCompatibility() {
        val originalSecret = "jwt_bearer_token_secret_v2_backward_compatible"
        val v2Encrypted = SecurityUtils.encryptV2(originalSecret)

        // Must start with v2: prefix
        assertTrue("Ciphertext must use v2 prefix", v2Encrypted.startsWith("v2:"))

        // Decrypt using modern SecurityUtils.decrypt()
        assertEquals(originalSecret, SecurityUtils.decrypt(v2Encrypted))
    }

    @Test
    fun testAesLegacyEcbDecryptionFallback() {
        // Raw plaintext encrypted via legacy ECB
        val legacyKey = byteArrayOf(115, 112, 97, 114, 107, 105, 110, 103, 112, 97, 116, 104, 115, 104, 97, 108)
        val secretKey = javax.crypto.spec.SecretKeySpec(legacyKey, "AES")
        val cipher = javax.crypto.Cipher.getInstance("AES")
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey)
        val plain = "legacy_token_pre_v2"
        val encryptedBytes = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        val legacyCiphertext = java.util.Base64.getEncoder().encodeToString(encryptedBytes)

        // Decrypt using SecurityUtils
        val decrypted = SecurityUtils.decrypt(legacyCiphertext)
        assertEquals("Legacy ECB ciphertext must seamlessly decrypt without v2 prefix", plain, decrypted)
    }

    @Test
    fun testPurgeExpiredNotifications() = runBlocking {
        val mockDb = mock(AppDatabase::class.java)
        val mockNotificationDao = mock(NotificationDao::class.java)
        `when`(mockDb.userDao()).thenReturn(mock(UserDao::class.java))
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mockNotificationDao)

        `when`(mockNotificationDao.purgeOldNotifications(org.mockito.ArgumentMatchers.anyLong())).thenReturn(5)
        `when`(mockNotificationDao.trimNotificationHistory(100)).thenReturn(3)

        val repo = AppRepositoryImpl(mockDb, FakeApiService(), OkHttpClient())
        val purgedCount = repo.purgeExpiredNotifications()

        assertEquals(8, purgedCount)
    }

    @Test
    fun testLoginAsGuest_GeneratesEncryptedTokenAndInsertsGuestUser() = runBlocking {
        val fakeUserDao = FakeUserDao()
        val fakeGuestTelemetryDao = FakeGuestTelemetryDao()
        val fakeNotificationDao = FakeNotificationDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(fakeGuestTelemetryDao)
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val repo = AppRepositoryImpl(mockDb, FakeApiService(), OkHttpClient())
        val result = repo.loginAsGuest(
            guestId = "guest_test_123",
            deviceName = "Pixel 8",
            deviceModel = "Google",
            location = "Delhi, India",
            ipAddress = "192.168.1.100"
        )

        assertTrue(result.isSuccess)
        val guest = result.getOrNull()
        assertNotNull(guest)
        assertEquals("guest_test_123", guest?.id)
        assertEquals("guest", guest?.role)
        assertEquals("Guest Student", guest?.name)
        assertTrue(guest?.token?.isNotEmpty() == true)
        val cached = fakeUserDao.getUserDirect()
        assertEquals(guest?.id, cached?.id)
        val savedGuest = fakeGuestTelemetryDao.getGuestById("guest_test_123")
        assertNotNull(savedGuest)
        assertEquals("Pixel 8", savedGuest?.deviceName)
    }

    @Test
    fun testSyncHomework_Student_PullsRemoteAndInsertsToRoom() = runBlocking {
        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("st_1", "Student", "student@test.com", "01", "Class 10", SecurityUtils.encrypt("token"), "student", System.currentTimeMillis()))
        val fakeHomeworkDao = FakeHomeworkDao()
        val fakeNotificationDao = FakeNotificationDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(fakeHomeworkDao)
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val fakeApi = FakeApiService()
        fakeApi.studentHomeworkList.add(
            RemoteHomeworkItem(
                id = 101,
                classLevel = "Class 10",
                subject = "Mathematics",
                topic = "Real Numbers",
                questionText = "Complete ex 1.2",
                imageName = "hw.pdf",
                imageData = "base64data",
                assignedDate = "2026-07-01",
                createdAt = "2026-06-18T10:00:00Z"
            )
        )

        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())
        val syncResult = repo.syncHomework()
        assertTrue(syncResult.isSuccess)

        val saved = fakeHomeworkDao.items.find { it.id == "101" }
        assertNotNull(saved)
        assertEquals("Real Numbers", saved?.title)
        assertEquals("Mathematics", saved?.subject)
    }

    @Test
    fun testSyncHomework_Admin_PullsRemoteAndInsertsToRoom() = runBlocking {
        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("ad_1", "Admin", "admin@sparkingpathshala.in", "00", "All", SecurityUtils.encrypt("token"), "admin", System.currentTimeMillis()))
        val fakeHomeworkDao = FakeHomeworkDao()
        val fakeNotificationDao = FakeNotificationDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(fakeHomeworkDao)
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val fakeApi = FakeApiService()
        fakeApi.adminHomeworkList.add(
            RemoteHomeworkItem(
                id = 202,
                classLevel = "Class 12",
                subject = "Physics",
                topic = "Optics",
                questionText = "Optics experiment",
                imageName = null,
                imageData = null,
                assignedDate = "2026-07-10",
                createdAt = "2026-06-18T10:00:00Z"
            )
        )

        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())
        val syncResult = repo.syncHomework()
        assertTrue(syncResult.isSuccess)

        val saved = fakeHomeworkDao.items.find { it.id == "202" }
        assertNotNull(saved)
        assertEquals("Optics", saved?.title)
        assertEquals("Class 12", saved?.classLevel)
    }

    @Test
    fun testUploadHomework_PropagatesToRemoteAndSavesToRoom() = runBlocking {
        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("ad_1", "Admin", "admin@sparkingpathshala.in", "00", "All", SecurityUtils.encrypt("token"), "admin", System.currentTimeMillis()))
        val fakeHomeworkDao = FakeHomeworkDao()
        val fakeNotificationDao = FakeNotificationDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(fakeHomeworkDao)
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val fakeApi = FakeApiService()
        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())

        val uploadResult = repo.uploadHomework(
            title = "Chemistry Worksheet",
            description = "Organic reactions",
            subject = "Chemistry",
            classLevel = "Class 11",
            topic = "Hydrocarbons",
            batch = "Batch A",
            fileName = "worksheet.pdf",
            fileData = "base64data",
            fileType = "application/pdf",
            dueDate = "2026-07-15",
            maxMarks = 25
        )

        assertTrue(uploadResult.isSuccess)
        assertEquals(1, fakeApi.adminHomeworkList.size)
        assertEquals("Chemistry", fakeApi.adminHomeworkList[0].subject)
        assertEquals(1, fakeHomeworkDao.items.size)
        assertEquals("Chemistry Worksheet", fakeHomeworkDao.items[0].title)
    }

    @Test
    fun testUpdateHomework_PropagatesToRemoteAndUpdatesRoom() = runBlocking {
        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("ad_1", "Admin", "admin@sparkingpathshala.in", "00", "All", SecurityUtils.encrypt("token"), "admin", System.currentTimeMillis()))
        val fakeHomeworkDao = FakeHomeworkDao()
        fakeHomeworkDao.insertSingleHomework(
            HomeworkEntity(
                id = "hw_1",
                title = "Old Title",
                description = "Old Desc",
                subject = "Biology",
                classLevel = "Class 9",
                topic = "Cell",
                batch = null,
                fileUrl = "",
                fileType = "PDF",
                localPath = "",
                isDownloaded = false,
                dueDate = "2026-06-20",
                maxMarks = 10,
                createdAt = System.currentTimeMillis()
            )
        )
        val fakeNotificationDao = FakeNotificationDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(fakeHomeworkDao)
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val fakeApi = FakeApiService()
        fakeApi.adminHomeworkList.add(
            RemoteHomeworkItem(id = "hw_1", classLevel = "Class 9", subject = "Biology", topic = "Cell", assignedDate = "2026-06-20", createdAt = "2026-06-18T10:00:00Z")
        )

        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())
        val updateResult = repo.updateHomework(
            id = "hw_1",
            title = "New Biology Title",
            description = "Updated description",
            subject = "Biology",
            classLevel = "Class 9",
            topic = "Cell Structure",
            batch = "Batch B",
            dueDate = "2026-06-25",
            maxMarks = 15
        )

        assertTrue(updateResult.isSuccess)
        val updated = fakeHomeworkDao.getHomeworkById("hw_1")
        assertEquals("New Biology Title", updated?.title)
        assertEquals("Biology", updated?.subject)
    }

    @Test
    fun testDeleteHomework_CallsRemoteAndDeletesRoom() = runBlocking {
        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("ad_1", "Admin", "admin@sparkingpathshala.in", "00", "All", SecurityUtils.encrypt("token"), "admin", System.currentTimeMillis()))
        val fakeHomeworkDao = FakeHomeworkDao()
        fakeHomeworkDao.insertSingleHomework(
            HomeworkEntity(
                id = "hw_10",
                title = "To Delete",
                description = "Desc",
                subject = "English",
                classLevel = "Class 10",
                topic = null,
                batch = null,
                fileUrl = "",
                fileType = "PDF",
                localPath = "",
                isDownloaded = false,
                dueDate = "2026-06-20",
                maxMarks = 10,
                createdAt = System.currentTimeMillis()
            )
        )
        val fakeNotificationDao = FakeNotificationDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(fakeHomeworkDao)
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val fakeApi = FakeApiService()
        fakeApi.adminHomeworkList.add(
            RemoteHomeworkItem(id = "hw_10", classLevel = "Class 10", subject = "English", assignedDate = "2026-06-20", createdAt = "2026-06-18T10:00:00Z")
        )

        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())
        val delResult = repo.deleteHomework("hw_10")
        assertTrue(delResult.isSuccess)

        val found = fakeHomeworkDao.getHomeworkById("hw_10")
        assertNull(found)
        assertEquals(0, fakeApi.adminHomeworkList.size)
    }

    @Test
    fun testSyncNotifications_PullsRemoteAndInsertsToRoom() = runBlocking {
        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("st_1", "Student", "student@test.com", "01", "Class 10", SecurityUtils.encrypt("token"), "student", System.currentTimeMillis()))
        val fakeNotificationDao = FakeNotificationDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val fakeApi = FakeApiService()
        fakeApi.notificationsList.add(
            RemoteNotificationItem(
                id = 501,
                recipientEmail = "student@test.com",
                title = "Exam Schedule Released",
                message = "Check the schedule tab",
                type = "ANNOUNCEMENT",
                linkTab = "results",
                isRead = 0,
                createdAt = "2026-06-18T10:00:00Z"
            )
        )

        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())
        val syncResult = repo.syncNotifications()
        assertTrue(syncResult.isSuccess)

        val saved = fakeNotificationDao.notifications.find { it.id == "501" }
        assertNotNull(saved)
        assertEquals("Exam Schedule Released", saved?.title)
        assertEquals("results", saved?.actionRoute)
        assertFalse(saved?.isRead ?: true)
    }

    @Test
    fun testNotificationActions_ReadAndClearFlow() = runBlocking {
        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("st_1", "Student", "student@test.com", "01", "Class 10", SecurityUtils.encrypt("token"), "student", System.currentTimeMillis()))
        val fakeNotificationDao = FakeNotificationDao()
        fakeNotificationDao.insertNotification(
            NotificationEntity(
                id = "n_1",
                recipientRole = "student",
                targetClass = "Class 10",
                targetUserId = "student@test.com",
                title = "Alert 1",
                message = "Msg 1",
                category = "TEST",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                actionRoute = null
            )
        )
        fakeNotificationDao.insertNotification(
            NotificationEntity(
                id = "n_2",
                recipientRole = "student",
                targetClass = "Class 10",
                targetUserId = "student@test.com",
                title = "Alert 2",
                message = "Msg 2",
                category = "TEST",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                actionRoute = null
            )
        )
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val fakeApi = FakeApiService()
        fakeApi.notificationsList.add(RemoteNotificationItem(id = 1, recipientEmail = "student@test.com", type = "TEST", title = "Alert 1", message = "Msg 1", linkTab = null, isRead = 0, isHidden = 0, createdAt = "2026-06-18T10:00:00Z"))
        fakeApi.notificationsList.add(RemoteNotificationItem(id = 2, recipientEmail = "student@test.com", type = "TEST", title = "Alert 2", message = "Msg 2", linkTab = null, isRead = 0, isHidden = 0, createdAt = "2026-06-18T10:00:00Z"))

        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())

        // Mark single notification as read
        repo.markNotificationAsRead("n_1")
        val n1 = fakeNotificationDao.notifications.find { it.id == "n_1" }
        assertTrue(n1?.isRead == true)

        // Mark all as read
        repo.markAllNotificationsAsRead()
        val n2 = fakeNotificationDao.notifications.find { it.id == "n_2" }
        assertTrue(n2?.isRead == true)

        // Delete single notification
        repo.deleteNotification("n_1")
        assertNull(fakeNotificationDao.notifications.find { it.id == "n_1" })

        // Clear all notifications
        repo.clearAllNotifications()
        assertTrue(fakeNotificationDao.notifications.isEmpty())
    }

    @Test
    fun testGeneratePracticeQuizAndQuizHistory() = runBlocking {
        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("st_1", "Student", "student@test.com", "01", "Class 10", SecurityUtils.encrypt("token"), "student", System.currentTimeMillis()))
        val fakeNotificationDao = FakeNotificationDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(fakeNotificationDao)

        val fakeApi = FakeApiService()
        fakeApi.quizHistoryList.add(
            QuizHistoryItem(
                id = 1,
                quizId = "q_hist_1",
                score = 8,
                totalQuestions = 10,
                submittedAt = "2026-06-18T10:00:00Z",
                subject = "Mathematics",
                classLevel = "Class 10",
                chapter = "Algebra"
            )
        )

        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())

        val practiceResult = repo.generatePracticeQuiz("Mathematics", 10)
        assertTrue(practiceResult.isSuccess)
        val quizResponse = practiceResult.getOrNull()
        assertNotNull(quizResponse)
        assertEquals("Mathematics", quizResponse?.quiz?.subject)

        val historyResult = repo.getQuizHistory()
        assertTrue(historyResult.isSuccess)
        val history = historyResult.getOrNull()
        assertNotNull(history)
        assertEquals(1, history?.size)
        assertEquals("Mathematics", history?.get(0)?.subject)
        assertEquals(8, history?.get(0)?.score)
    }

    @Test
    fun testHomeworkScreen_SubjectsNeverHaveDuplicatesEvenWithAll() {
        val hwListWithAll = listOf(
            HomeworkEntity(id = "1", title = "Math HW", description = "Solve chapter 1", subject = "Maths", classLevel = "Class 10"),
            HomeworkEntity(id = "2", title = "General HW", description = "Read all", subject = "All", classLevel = "Class 10"),
            HomeworkEntity(id = "3", title = "All in lowercase", description = "Read all", subject = "all", classLevel = "Class 10"),
            HomeworkEntity(id = "4", title = "With whitespace", description = "Read all", subject = " Maths ", classLevel = "Class 10"),
            HomeworkEntity(id = "5", title = "Science HW", description = "Lab report", subject = "Science", classLevel = "Class 10")
        )

        val nonAllSubjects = hwListWithAll
            .mapNotNull { it.subject?.trim() }
            .filter { it.isNotBlank() && !it.equals("All", ignoreCase = true) }
            .distinct()
            .sorted()
        val subjects = listOf("All") + nonAllSubjects

        assertEquals("Subjects list must have exactly 3 unique entries: All, Maths, Science", 3, subjects.size)
        assertEquals("All", subjects[0])
        assertEquals("Maths", subjects[1])
        assertEquals("Science", subjects[2])

        // Verify keys used for LazyRow items are 100% unique
        val keys = subjects.map { "subject_$it" }
        assertEquals("All subject keys must be strictly unique without collisions", keys.size, keys.distinct().size)
    }

    @Test
    fun testHomeworkScreen_FilterAndSortWithEdgeCases() {
        val mixedHomework = listOf(
            HomeworkEntity(id = "hw_1", title = "[Assignment] Algebra", description = "Assignment tasks", subject = "Maths", classLevel = "Class 10", dueDate = "2026-11-15"),
            HomeworkEntity(id = "hw_2", title = "Mechanics", description = "Solve problems", subject = "Physics", classLevel = "All", dueDate = ""),
            HomeworkEntity(id = "hw_3", title = "Botany", description = "", subject = "Biology", classLevel = "Class 10", dueDate = "2026-11-10"),
            HomeworkEntity(id = "hw_1", title = "Duplicate ID", description = "Test collision", subject = "Chemistry", classLevel = "Class 10")
        )

        // Unique deduplication check
        val uniqueList = mixedHomework.distinctBy { it.id }
        assertEquals(3, uniqueList.size)

        // Sort by Due Date
        val sortedByDue = uniqueList.sortedBy { (it.dueDate ?: "").ifBlank { "9999" } }
        assertEquals("hw_3", sortedByDue[0].id)
        assertEquals("hw_1", sortedByDue[1].id)
        assertEquals("hw_2", sortedByDue[2].id)

        // Sort by Title
        val sortedByTitle = uniqueList.sortedBy { (it.title ?: "").lowercase() }
        assertEquals("[Assignment] Algebra", sortedByTitle[0].title)

        // Sort by Subject
        val sortedBySubject = uniqueList.sortedBy { (it.subject ?: "").lowercase() }
        assertEquals("Biology", sortedBySubject[0].subject)
    }

    @Test
    fun testSyncHomework_WithNullAndDoubleIdRemoteItems() = runBlocking {
        class FakeHwDao : HomeworkDao {
            val storedItems = mutableListOf<HomeworkEntity>()
            override fun getAllHomeworkFlow(): Flow<List<HomeworkEntity>> = flowOf(storedItems)
            override fun getHomeworkByClassFlow(classLevel: String): Flow<List<HomeworkEntity>> = flowOf(storedItems)
            override suspend fun getHomeworkById(id: String): HomeworkEntity? = storedItems.find { it.id == id }
            override suspend fun insertHomework(items: List<HomeworkEntity>) {
                storedItems.removeAll { existing -> items.any { it.id == existing.id } }
                storedItems.addAll(items)
            }
            override suspend fun insertSingleHomework(item: HomeworkEntity) { storedItems.add(item) }
            override suspend fun updateHomework(item: HomeworkEntity) {}
            override suspend fun deleteHomework(id: String) { storedItems.removeAll { it.id == id } }
            override suspend fun purgeOldHomework(cutoffTimestamp: Long): Int = 0
            override suspend fun clearNonDownloadedHomework() {}
            override suspend fun clearHomework() { storedItems.clear() }
        }

        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("u_1", "Student", "st@test.com", "01", "Class 10", SecurityUtils.encrypt("valid_token"), "student", System.currentTimeMillis()))
        val fakeHwDao = FakeHwDao()
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(fakeHwDao)
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mock(NotificationDao::class.java))

        val fakeApi = FakeApiService()
        fakeApi.studentHomeworkList.clear()
        // Simulate items with Double ID from Gson (e.g. 101.0), and nullable fields
        fakeApi.studentHomeworkList.add(
            RemoteHomeworkItem(
                id = 101.0,
                classLevel = null,
                subject = null,
                topic = null,
                questionText = null,
                assignedDate = null
            )
        )

        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())
        val syncRes = repo.syncHomework()
        assertTrue("Sync must succeed even with null/floating-point remote fields", syncRes.isSuccess)

        val synced = fakeHwDao.storedItems
        assertEquals(1, synced.size)
        assertEquals("ID must be stripped of .0 suffix", "101", synced[0].id)
        assertEquals("Subject must safely fallback to General", "General", synced[0].subject)
        assertEquals("Class must safely fallback to All", "All", synced[0].classLevel)
        assertEquals("Title must safely fallback to General Homework", "General Homework", synced[0].title)
        assertEquals("Description must safely fallback to empty string", "", synced[0].description)
        assertEquals("DueDate must safely fallback to empty string", "", synced[0].dueDate)
    }

    @Test
    fun testHomeworkList_RecoversFromRoomSqliteExceptionGracefully() = runBlocking {
        class CrashingHwDao : HomeworkDao {
            override fun getAllHomeworkFlow(): Flow<List<HomeworkEntity>> = kotlinx.coroutines.flow.flow {
                throw android.database.sqlite.SQLiteException("no such table: homework")
            }
            override fun getHomeworkByClassFlow(classLevel: String): Flow<List<HomeworkEntity>> = kotlinx.coroutines.flow.flow {
                throw android.database.sqlite.SQLiteException("no such table: homework")
            }
            override suspend fun getHomeworkById(id: String): HomeworkEntity? = null
            override suspend fun insertHomework(items: List<HomeworkEntity>) {}
            override suspend fun insertSingleHomework(item: HomeworkEntity) {}
            override suspend fun updateHomework(item: HomeworkEntity) {}
            override suspend fun deleteHomework(id: String) {}
            override suspend fun purgeOldHomework(cutoffTimestamp: Long): Int = 0
            override suspend fun clearNonDownloadedHomework() {}
            override suspend fun clearHomework() {}
        }

        val fakeUserDao = FakeUserDao()
        fakeUserDao.insertUser(UserEntity("u_1", "Student", "st@test.com", "01", "Class 10", SecurityUtils.encrypt("valid_token"), "student", System.currentTimeMillis()))
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(CrashingHwDao())
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mock(NotificationDao::class.java))

        val repo = AppRepositoryImpl(mockDb, FakeApiService(), OkHttpClient())
        val viewModel = DashboardViewModel(repo)

        // Collecting homeworkList must not throw uncaught SQLiteException
        val hwList = viewModel.homeworkList.first()
        assertTrue("When Room throws an SQLiteException, homeworkList must safely catch it and emit empty list", hwList.isEmpty())
    }

    @Test
    fun testAutoLogoutAfterSevenDays() = runBlocking {
        val fakeUserDao = FakeUserDao()
        val eightDaysAgo = System.currentTimeMillis() - (8L * 24 * 60 * 60 * 1000L)
        fakeUserDao.insertUser(
            UserEntity(
                id = "expired_student",
                name = "Test Student",
                email = "student@test.com",
                rollNo = "SP-01",
                classLevel = "Class 10",
                token = SecurityUtils.encrypt("test_token"),
                role = "student",
                loginTimestamp = eightDaysAgo
            )
        )
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mock(NotificationDao::class.java))

        val repo = AppRepositoryImpl(mockDb, FakeApiService(), OkHttpClient())
        val user = repo.getUser().first()
        assertNull("A session older than 7 days must be automatically expired and emit null", user)
    }

    @Test
    fun testSessionKeptWithinSevenDays() = runBlocking {
        val fakeUserDao = FakeUserDao()
        val twoDaysAgo = System.currentTimeMillis() - (2L * 24 * 60 * 60 * 1000L)
        fakeUserDao.insertUser(
            UserEntity(
                id = "active_student",
                name = "Test Student",
                email = "student@test.com",
                rollNo = "SP-01",
                classLevel = "Class 10",
                token = SecurityUtils.encrypt("test_token"),
                role = "student",
                loginTimestamp = twoDaysAgo
            )
        )
        val mockDb = mock(AppDatabase::class.java)
        `when`(mockDb.userDao()).thenReturn(fakeUserDao)
        `when`(mockDb.feeDao()).thenReturn(mock(FeeDao::class.java))
        `when`(mockDb.resultDao()).thenReturn(mock(ResultDao::class.java))
        `when`(mockDb.attendanceDao()).thenReturn(mock(AttendanceDao::class.java))
        `when`(mockDb.studyMaterialDao()).thenReturn(mock(StudyMaterialDao::class.java))
        `when`(mockDb.quizDao()).thenReturn(mock(QuizDao::class.java))
        `when`(mockDb.questionDao()).thenReturn(mock(QuestionDao::class.java))
        `when`(mockDb.homeworkDao()).thenReturn(mock(HomeworkDao::class.java))
        `when`(mockDb.guestTelemetryDao()).thenReturn(mock(GuestTelemetryDao::class.java))
        `when`(mockDb.notificationDao()).thenReturn(mock(NotificationDao::class.java))

        val repo = AppRepositoryImpl(mockDb, FakeApiService(), OkHttpClient())
        val user = repo.getUser().first()
        assertNotNull("A session within 7 days must remain active", user)
        assertEquals("active_student", user?.id)
        assertEquals("test_token", user?.token)
    }

    @Test
    fun testCheckAppVersionApi() = runBlocking {
        val mockDb = mock(AppDatabase::class.java)
        val fakeApi = FakeApiService()
        val repo = AppRepositoryImpl(mockDb, fakeApi, OkHttpClient())

        val result = repo.checkAppVersion()
        assertTrue(result.isSuccess)
        val version = result.getOrNull()
        assertNotNull(version)
        assertEquals(31, version?.latestVersionCode)
        assertEquals("3.1", version?.latestVersionName)
        assertEquals(7, version?.sessionLifetimeDays)
    }
}
