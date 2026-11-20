package com.sparkingpathshala.app.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/signup")
    suspend fun signup(@Body request: SignupRequest): SimpleMessageResponse

    @GET("api/student/dashboard")
    suspend fun getStudentDashboard(@Header("Authorization") token: String): DashboardResponse

    @GET("api/library/resources/{category}")
    suspend fun getLibraryResources(
        @Header("Authorization") token: String,
        @Path("category") category: String
    ): LibraryResourcesResponse

    @GET("api/library/resources/{id}/download")
    suspend fun downloadResource(
        @Header("Authorization") token: String,
        @Path("id") resourceId: String
    ): DownloadResourceResponse

    @POST("api/doubts")
    suspend fun submitDoubt(
        @Header("Authorization") token: String,
        @Body request: DoubtRequest
    ): SimpleMessageResponse

    @GET("api/student/quiz/active")
    suspend fun getActiveQuiz(
        @Header("Authorization") token: String
    ): ActiveQuizResponse

    @DELETE("api/auth/delete-account")
    suspend fun deleteAccount(
        @Header("Authorization") token: String
    ): SimpleMessageResponse

    @PUT("api/auth/update-password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): SimpleMessageResponse

    @GET("api/doubts/student")
    suspend fun getStudentDoubts(
        @Header("Authorization") token: String
    ): DoubtsListResponse

    @GET("api/admin/doubts")
    suspend fun getAdminDoubts(
        @Header("Authorization") token: String
    ): DoubtsListResponse

    @GET("api/doubts/{id}/replies")
    suspend fun getDoubtReplies(
        @Header("Authorization") token: String,
        @Path("id") doubtId: String
    ): DoubtRepliesResponse

    @POST("api/doubts/{id}/replies")
    suspend fun submitDoubtReply(
        @Header("Authorization") token: String,
        @Path("id") doubtId: String,
        @Body request: DoubtReplyRequest
    ): SimpleMessageResponse

    @PUT("api/admin/doubts/{id}/status")
    suspend fun updateDoubtStatus(
        @Header("Authorization") token: String,
        @Path("id") doubtId: String,
        @Body request: UpdateDoubtStatusRequest
    ): SimpleMessageResponse

    @DELETE("api/admin/doubts/{id}")
    suspend fun deleteDoubt(
        @Header("Authorization") token: String,
        @Path("id") doubtId: String
    ): SimpleMessageResponse

    @POST("api/student/quiz/submit")
    suspend fun submitQuiz(
        @Header("Authorization") token: String,
        @Body request: SubmitQuizRequest
    ): SubmitQuizResponse

    @GET("api/admin/students")
    suspend fun getAdminStudents(
        @Header("Authorization") token: String
    ): AdminStudentsResponse

    @GET("api/admin/records/{type}")
    suspend fun getAdminRecords(
        @Header("Authorization") token: String,
        @Path("type") type: String
    ): AdminRecordResponse

    @POST("api/admin/records/{type}")
    suspend fun addAdminRecord(
        @Header("Authorization") token: String,
        @Path("type") type: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): SimpleMessageResponse

    @PUT("api/admin/records/{type}/{id}")
    suspend fun updateAdminRecord(
        @Header("Authorization") token: String,
        @Path("type") type: String,
        @Path("id") id: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): SimpleMessageResponse

    @DELETE("api/admin/records/{type}/{id}")
    suspend fun deleteAdminRecord(
        @Header("Authorization") token: String,
        @Path("type") type: String,
        @Path("id") id: String
    ): SimpleMessageResponse

    @POST("api/library/resources")
    suspend fun uploadStudyMaterial(
        @Header("Authorization") token: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): SimpleMessageResponse

    @DELETE("api/admin/library/resources/{id}")
    suspend fun deleteResource(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): SimpleMessageResponse

    @GET("api/admin/library/resources")
    suspend fun getAllLibraryResourcesAdmin(
        @Header("Authorization") token: String
    ): LibraryResourcesResponse

    @PUT("api/admin/library/resources/{id}")
    suspend fun updateLibraryResource(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): SimpleMessageResponse

    @GET("api/admin/quizzes")
    suspend fun getAdminQuizzes(
        @Header("Authorization") token: String
    ): AdminQuizzesResponse

    @POST("api/admin/quiz/manual")
    suspend fun createManualQuiz(
        @Header("Authorization") token: String,
        @Body request: ManualQuizRequest
    ): SimpleMessageResponse

    @POST("api/admin/quiz/generate-ai")
    suspend fun generateAiQuiz(
        @Header("Authorization") token: String,
        @Body request: AiQuizRequest
    ): SimpleMessageResponse

    @DELETE("api/admin/quizzes/{id}")
    suspend fun deleteQuiz(
        @Header("Authorization") token: String,
        @Path("id") quizId: String
    ): SimpleMessageResponse

    @DELETE("api/admin/quiz/{id}")
    suspend fun deleteQuizSingular(
        @Header("Authorization") token: String,
        @Path("id") quizId: String
    ): SimpleMessageResponse

    @POST("api/admin/quizzes/{id}/launch")
    suspend fun launchQuiz(
        @Header("Authorization") token: String,
        @Path("id") quizId: String
    ): SimpleMessageResponse

    @POST("api/admin/quizzes/launch-multiple")
    suspend fun launchMultipleQuizzes(
        @Header("Authorization") token: String,
        @Body request: LaunchMultipleQuizzesRequest
    ): SimpleMessageResponse

    @GET("api/admin/pending-students")
    suspend fun getPendingStudents(
        @Header("Authorization") token: String
    ): PendingStudentsResponse

    @POST("api/admin/approve-student/{id}")
    suspend fun approveStudent(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): SimpleMessageResponse

    @POST("api/admin/reject-student/{id}")
    suspend fun rejectStudent(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): SimpleMessageResponse

    @DELETE("api/admin/students/{id}")
    suspend fun deleteStudent(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): SimpleMessageResponse

    @PUT("api/admin/students/{id}/status")
    suspend fun updateStudentStatus(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): SimpleMessageResponse

    @PUT("api/admin/students/{id}")
    suspend fun updateStudent(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): SimpleMessageResponse

    @POST("api/payments/initialize")
    suspend fun initializePayment(
        @Header("Authorization") token: String,
        @Body request: InitializePaymentRequest
    ): InitializePaymentResponse

    @POST("api/payments/verify")
    suspend fun verifyPayment(
        @Header("Authorization") token: String,
        @Body request: VerifyPaymentRequest
    ): SimpleMessageResponse

    @GET("api/payments/transactions")
    suspend fun getStudentTransactions(
        @Header("Authorization") token: String
    ): List<TransactionResponse>

    @GET("api/enquiries")
    suspend fun getEnquiries(
        @Header("Authorization") token: String
    ): okhttp3.ResponseBody

    @PUT("api/enquiries/{id}/status")
    suspend fun updateEnquiry(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): SimpleMessageResponse

    @DELETE("api/enquiries/{id}")
    suspend fun deleteEnquiry(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): SimpleMessageResponse

    // Homework Management Endpoints
    @GET("api/student/homework")
    suspend fun getStudentHomework(
        @Header("Authorization") token: String
    ): StudentHomeworkResponse

    @GET("api/admin/homework")
    suspend fun getAdminHomework(
        @Header("Authorization") token: String
    ): AdminHomeworkResponse

    @POST("api/admin/homework")
    suspend fun createHomework(
        @Header("Authorization") token: String,
        @Body request: CreateHomeworkRequest
    ): SimpleMessageResponse

    @PUT("api/admin/homework/{id}")
    suspend fun updateHomework(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: UpdateHomeworkRequest
    ): SimpleMessageResponse

    @DELETE("api/admin/homework/{id}")
    suspend fun deleteHomework(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): SimpleMessageResponse

    // Notification Engine Endpoints
    @GET("api/notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): NotificationsListResponse

    @POST("api/admin/notifications/send")
    suspend fun sendCustomNotification(
        @Header("Authorization") token: String,
        @Body request: SendCustomNotificationRequest
    ): SimpleMessageResponse

    @PUT("api/notifications/{id}/read")
    suspend fun markNotificationRead(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): SimpleMessageResponse

    @PUT("api/notifications/read-all")
    suspend fun markAllNotificationsRead(
        @Header("Authorization") token: String
    ): SimpleMessageResponse

    @DELETE("api/notifications/{id}")
    suspend fun deleteNotification(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): SimpleMessageResponse

    @DELETE("api/notifications/clear-all")
    suspend fun clearAllNotifications(
        @Header("Authorization") token: String
    ): SimpleMessageResponse

    // Practice Quiz & History Endpoints
    @POST("api/student/quiz/practice")
    suspend fun generatePracticeQuiz(
        @Header("Authorization") token: String,
        @Body request: PracticeQuizRequest
    ): ActiveQuizResponse

    @GET("api/student/quiz/history")
    suspend fun getQuizHistory(
        @Header("Authorization") token: String
    ): QuizHistoryResponse

    @POST("api/admin/quiz/refresh-fallback-bank")
    suspend fun refreshFallbackBank(
        @Header("Authorization") token: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): SimpleMessageResponse

    @GET("api/admin/quiz/fallback-bank-stats")
    suspend fun getFallbackBankStats(
        @Header("Authorization") token: String
    ): Map<String, Any>
}


data class InitializePaymentRequest(
    @SerializedName("month") val month: String,
    @SerializedName("amount") val amount: Double
)

data class InitializePaymentResponse(
    @SerializedName("txnRefId") val txnRefId: String
)

data class VerifyPaymentRequest(
    @SerializedName("txnRefId") val txnRefId: String,
    @SerializedName("utrNumber") val utrNumber: String,
    @SerializedName("screenshotData") val screenshotData: String? = null
)

data class UpdatePasswordRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)

data class DoubtRequest(
    @SerializedName("title") val title: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("message") val message: String,
    @SerializedName("fileName") val fileName: String? = null,
    @SerializedName("fileData") val fileData: String? = null
)

data class ActiveQuizResponse(
    @SerializedName("quiz") val quiz: QuizDetailsResponse,
    @SerializedName("questions") val questions: List<QuestionResponse>,
    @SerializedName("attempted") val attempted: Boolean? = false,
    @SerializedName("score") val score: Int? = null,
    @SerializedName("total_questions") val totalQuestions: Int? = null
)

data class QuizDetailsResponse(
    @SerializedName("id") val id: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("chapter") val chapter: String,
    @SerializedName("class") val classSelection: String,
    @SerializedName("quiz_type") val quizType: String,
    @SerializedName("scheduled_start") val scheduledStart: String
)
