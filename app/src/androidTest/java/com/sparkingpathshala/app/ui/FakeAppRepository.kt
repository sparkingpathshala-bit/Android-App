package com.sparkingpathshala.app.ui

import android.content.Context
import com.sparkingpathshala.app.data.local.*
import com.sparkingpathshala.app.data.remote.*
import com.sparkingpathshala.app.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Open base fake repository providing default no-op implementations for instrumented UI tests.
 * Allows test classes to only override the specific methods needed for their assertions.
 */
open class FakeAppRepository : AppRepository {
    override fun getUser(): Flow<UserEntity?> = flowOf(null)
    override suspend fun login(email: String, password: String): Result<UserEntity> = Result.failure(Exception("Stub"))
    override suspend fun signup(name: String, email: String, classSelection: String, mobile: String, password: String): Result<String> = Result.success("Success")
    override suspend fun loginAsGuest(guestId: String, deviceName: String, deviceModel: String, location: String, ipAddress: String): Result<UserEntity> = Result.success(
        UserEntity(guestId, "Guest Student", "guest@sparkingpathshala.in", "SP-GUEST", "All", "mock_token", "guest")
    )
    override suspend fun logout() {}
    override suspend fun deleteAccount(): Result<Unit> = Result.success(Unit)
    override suspend fun updatePassword(current: String, new: String): Result<Unit> = Result.success(Unit)

    override fun getFees(): Flow<List<FeeEntity>> = flowOf(emptyList())
    override suspend fun syncFees(): Result<Unit> = Result.success(Unit)
    override suspend fun initializePayment(month: String, amount: Double): Result<String> = Result.success("mock_txn")
    override suspend fun verifyPayment(txnRefId: String, utrNumber: String): Result<Unit> = Result.success(Unit)
    override suspend fun getStudentTransactions(): Result<List<TransactionResponse>> = Result.success(emptyList())

    override fun getResults(): Flow<List<ResultEntity>> = flowOf(emptyList())
    override suspend fun syncResults(): Result<Unit> = Result.success(Unit)

    override fun getAttendance(): Flow<List<AttendanceEntity>> = flowOf(emptyList())
    override suspend fun syncAttendance(): Result<Unit> = Result.success(Unit)

    override fun getStudyMaterials(): Flow<List<StudyMaterialEntity>> = flowOf(emptyList())
    override suspend fun syncStudyMaterials(): Result<Unit> = Result.success(Unit)
    override suspend fun downloadMaterial(context: Context, materialId: String): Result<Unit> = Result.success(Unit)
    override suspend fun deleteDownloadedMaterial(materialId: String) {}
    override suspend fun deleteDownloadedHomework(homeworkId: String) {}

    override suspend fun requestOnDemand(subject: String, description: String): Result<String> = Result.success("Success")

    override fun getQuizzes(): Flow<List<QuizEntity>> = flowOf(emptyList())
    override fun getQuestions(quizId: String): Flow<List<QuestionEntity>> = flowOf(emptyList())
    override suspend fun saveQuestions(questions: List<QuestionEntity>) {}
    override suspend fun deleteQuestionsByQuizId(quizId: String) {}
    override suspend fun syncQuizzes(): Result<Unit> = Result.success(Unit)

    override suspend fun getStudentDoubts(): Result<List<DoubtResponse>> = Result.success(emptyList())
    override suspend fun getAdminDoubts(): Result<List<DoubtResponse>> = Result.success(emptyList())
    override suspend fun getDoubtReplies(doubtId: String): Result<List<DoubtReplyResponse>> = Result.success(emptyList())
    override suspend fun submitDoubt(title: String, subject: String, message: String, fileName: String?, fileData: String?): Result<Unit> = Result.success(Unit)
    override suspend fun submitDoubtReply(doubtId: String, message: String, fileName: String?, fileData: String?): Result<Unit> = Result.success(Unit)
    override suspend fun updateDoubtStatus(doubtId: String, status: String): Result<Unit> = Result.success(Unit)
    override suspend fun deleteDoubt(doubtId: String): Result<Unit> = Result.success(Unit)
    override suspend fun submitQuiz(quizId: String, answers: Map<String, String>): Result<SubmitQuizResponse> =
        Result.success(SubmitQuizResponse("Success", 0, 0, emptyList()))

    override suspend fun getAdminStudents(): Result<List<AdminStudentInfo>> = Result.success(emptyList())
    override suspend fun getAdminRecords(type: String): Result<List<AdminRecord>> = Result.success(emptyList())
    override suspend fun addAdminRecord(type: String, data: Map<String, Any>): Result<Unit> = Result.success(Unit)
    override suspend fun updateAdminRecord(type: String, id: String, data: Map<String, Any>): Result<Unit> = Result.success(Unit)
    override suspend fun deleteAdminRecord(type: String, id: String): Result<Unit> = Result.success(Unit)
    override suspend fun uploadStudyMaterial(title: String, category: String, classSelection: String, subject: String, fileName: String, fileSize: String, fileData: String, allowedEmails: String?): Result<Unit> = Result.success(Unit)
    override suspend fun deleteResource(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateLibraryResource(id: String, title: String, category: String, classSelection: String, subject: String, status: String, allowedEmails: String?): Result<Unit> = Result.success(Unit)
    override suspend fun getAdminQuizzes(): Result<List<AdminQuizInfo>> = Result.success(emptyList())
    override suspend fun createManualQuiz(quiz: ManualQuizRequest): Result<Unit> = Result.success(Unit)
    override suspend fun generateAiQuiz(quiz: AiQuizRequest): Result<Unit> = Result.success(Unit)
    override suspend fun deleteQuiz(quizId: String): Result<Unit> = Result.success(Unit)
    override suspend fun launchQuiz(quizId: String): Result<Unit> = Result.success(Unit)
    override suspend fun launchMultipleQuizzes(quizIds: List<String>): Result<Unit> = Result.success(Unit)
    override suspend fun deleteMultipleQuizzes(quizIds: List<String>): Result<Unit> = Result.success(Unit)

    override suspend fun getPendingStudents(): Result<List<PendingStudentInfo>> = Result.success(emptyList())
    override suspend fun approveStudent(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun rejectStudent(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun deleteStudent(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateStudentStatus(id: String, status: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateStudent(id: String, data: Map<String, Any>): Result<Unit> = Result.success(Unit)

    override suspend fun getAdminEnquiries(): Result<List<EnquiryInfo>> = Result.success(emptyList())
    override fun listenToEnquiriesStream(onUpdate: () -> Unit): okhttp3.Call? = null
    override suspend fun updateAdminEnquiry(id: String, data: Map<String, Any>): Result<Unit> = Result.success(Unit)
    override suspend fun deleteAdminEnquiry(id: String): Result<Unit> = Result.success(Unit)

    override fun getHomework(classLevel: String): Flow<List<HomeworkEntity>> = flowOf(emptyList())
    override fun getAllHomework(): Flow<List<HomeworkEntity>> = flowOf(emptyList())
    override suspend fun uploadHomework(title: String, description: String, subject: String, classLevel: String, topic: String?, batch: String?, fileName: String, fileData: String, fileType: String, dueDate: String, maxMarks: Int): Result<Unit> = Result.success(Unit)
    override suspend fun deleteHomework(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateHomework(id: String, title: String, description: String, subject: String, classLevel: String, topic: String?, batch: String?, dueDate: String, maxMarks: Int): Result<Unit> = Result.success(Unit)
    override suspend fun downloadHomework(context: Context, homeworkId: String): Result<Unit> = Result.success(Unit)
    override suspend fun purgeExpiredHomework(): Int = 0

    override suspend fun recordGuestTelemetry(guestId: String, deviceName: String, deviceModel: String, location: String, ipAddress: String) {}
    override fun getGuestTelemetryList(): Flow<List<GuestTelemetryEntity>> = flowOf(emptyList())
    override suspend fun purgeExpiredGuests(): Int = 0

    override fun getStudentNotifications(classLevel: String): Flow<List<NotificationEntity>> = flowOf(emptyList())
    override fun getAdminNotifications(): Flow<List<NotificationEntity>> = flowOf(emptyList())
    override fun getStudentUnreadCount(classLevel: String): Flow<Int> = flowOf(0)
    override fun getAdminUnreadCount(): Flow<Int> = flowOf(0)
    override suspend fun markNotificationAsRead(id: String) {}
    override suspend fun markAllNotificationsAsRead() {}
    override suspend fun deleteNotification(id: String) {}
    override suspend fun clearAllNotifications() {}
    override suspend fun sendNotification(notification: NotificationEntity) {}
    override suspend fun syncHomework(): Result<Unit> = Result.success(Unit)
    override suspend fun syncNotifications(): Result<Unit> = Result.success(Unit)
    override suspend fun generatePracticeQuiz(subject: String, numQuestions: Int): Result<ActiveQuizResponse> = Result.failure(Exception("Not implemented"))
    override suspend fun getQuizHistory(): Result<List<QuizHistoryItem>> = Result.success(emptyList())
}
