package com.sparkingpathshala.app.data.remote

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SignupRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("class") val classSelection: String,
    @SerializedName("classSelection") val classSelectionAlias: String = classSelection,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("password") val password: String,
    @SerializedName("parentsMobile") val parentsMobile: String? = null
) {
    constructor(name: String, email: String, classSelection: String, mobile: String, password: String) :
        this(name, email, classSelection, classSelection, mobile, password, null)
}

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserInfo
)

data class UserInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("class") val classSelection: String,
    @SerializedName("role") val role: String,
    @SerializedName("status") val status: String
)

data class FeeResponse(
    @SerializedName("id") val id: String,
    @SerializedName("amount_paid") val amountPaid: Double,
    @SerializedName("total_due") val totalDue: Double,
    @SerializedName("payment_date") val paymentDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("month") val month: String,
    @SerializedName("adjusted_amount") val adjustedAmount: Double,
    @SerializedName("remarks") val remarks: String,
    @SerializedName("payment_mode") val paymentMode: String,
    @SerializedName("transaction_reference_number") val transactionRefNo: String?
)

data class ResultResponse(
    @SerializedName("id") val id: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("marks_obtained") val marksObtained: Double,
    @SerializedName("max_marks") val maxMarks: Double,
    @SerializedName("exam_date") val examDate: String
)

data class AttendanceResponse(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("status") val status: String
)

data class DashboardResponse(
    @SerializedName("attendance") val attendance: List<AttendanceResponse>,
    @SerializedName("results") val results: List<ResultResponse>,
    @SerializedName("fees") val fees: List<FeeResponse>
)

data class LibraryResourcesResponse(
    @SerializedName("resources") val resources: List<LibraryResourceResponse>
)

data class LibraryResourceResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("class") val classSelection: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_size") val fileSize: String,
    @SerializedName("file_data") val fileData: String?,
    @SerializedName("status") val status: String? = "active",
    @SerializedName("allowed_emails") val allowedEmails: String? = null
)

data class DownloadResourceResponse(
    @SerializedName("fileName") val fileName: String,
    @SerializedName("fileData") val fileData: String
)

data class SimpleMessageResponse(
    @SerializedName("message") val message: String
)

data class QuestionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("quiz_id") val quizId: String,
    @SerializedName("question_text") val questionText: String,
    @SerializedName("option_a") val optionA: String,
    @SerializedName("option_b") val optionB: String,
    @SerializedName("option_c") val optionC: String,
    @SerializedName("option_d") val optionD: String,
    @SerializedName("correct_option") val correctOption: String?,
    @SerializedName("explanation") val explanation: String?
)

data class DoubtReplyRequest(
    @SerializedName("message") val message: String,
    @SerializedName("file_name") val fileName: String? = null,
    @SerializedName("file_data") val fileData: String? = null
)

data class UpdateDoubtStatusRequest(
    @SerializedName("status") val status: String
)

data class SubmitQuizRequest(
    @SerializedName("quizId") val quizId: String,
    @SerializedName("quiz_id") val quizIdSnake: String,
    @SerializedName("answers") val answers: Map<String, String>
) {
    constructor(quizId: String, answers: Map<String, String>) : this(quizId, quizId, answers)
}

data class SubmitQuizResponse(
    @SerializedName("message") val message: String,
    @SerializedName("score") val score: Int,
    @SerializedName("totalQuestions") val totalQuestionsCamel: Int? = null,
    @SerializedName("total_questions") val totalQuestionsSnake: Int? = null,
    @SerializedName("questions") val questions: List<QuestionResponse>
) {
    val totalQuestions: Int
        get() = totalQuestionsCamel ?: totalQuestionsSnake ?: questions.size

    constructor(message: String, score: Int, totalQuestions: Int, questions: List<QuestionResponse>) : 
        this(message, score, totalQuestions, totalQuestions, questions)
}

data class DoubtResponse(
    @SerializedName("id") val id: String,
    @SerializedName("student_email") val studentEmail: String,
    @SerializedName("student_name") val studentName: String,
    @SerializedName("title") val title: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("message") val message: String,
    @SerializedName("file_name") val fileName: String?,
    @SerializedName("file_data") val fileData: String?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String
)

data class DoubtRepliesResponse(
    @SerializedName("replies") val replies: List<DoubtReplyResponse>
)

data class DoubtReplyResponse(
    @SerializedName("id") val id: String,
    @SerializedName("doubt_id") val doubtId: String,
    @SerializedName("sender_email") val senderEmail: String,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("sender_role") val senderRole: String,
    @SerializedName("message") val message: String,
    @SerializedName("file_name") val fileName: String?,
    @SerializedName("file_data") val fileData: String?,
    @SerializedName("created_at") val createdAt: String
)

data class DoubtsListResponse(
    @SerializedName("doubts") val doubts: List<DoubtResponse>
)

// Admin models
data class AdminStudentsResponse(
    @SerializedName("students") val students: List<AdminStudentInfo>
)

data class AdminStudentInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("class") val classSelection: String,
    @SerializedName("status") val status: String,
    @SerializedName("monthly_fee") val monthlyFee: Double?,
    @SerializedName("mobile") val mobile: String?
)

data class AdminRecordResponse(
    @SerializedName("records") val records: List<AdminRecord>
)

data class AdminRecord(
    @SerializedName("id") val id: String,
    @SerializedName("student_email") val studentEmail: String,
    @SerializedName("date") val date: String? = null,
    @SerializedName("exam_date") val examDate: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("subject") val subject: String? = null,
    @SerializedName("marks_obtained") val marksObtained: Double? = null,
    @SerializedName("max_marks") val maxMarks: Double? = null,
    @SerializedName("month") val month: String? = null,
    @SerializedName("amount_paid") val amountPaid: Double? = null,
    @SerializedName("total_due") val totalDue: Double? = null,
    @SerializedName("payment_date") val paymentDate: String? = null,
    @SerializedName("adjusted_amount") val adjustedAmount: Double? = null,
    @SerializedName("remarks") val remarks: String? = null,
    @SerializedName("payment_mode") val paymentMode: String? = null,
    @SerializedName("transaction_reference_number") val transactionRefNo: String? = null
)

data class AdminQuizzesResponse(
    @SerializedName("quizzes") val quizzes: List<AdminQuizInfo>
)

data class AdminQuizInfo(
    @SerializedName("id") val id: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("class") val classSelection: String,
    @SerializedName("chapter") val chapter: String,
    @SerializedName("quiz_type") val quizType: String,
    @SerializedName("scheduled_start") val scheduledStart: String,
    @SerializedName("is_manual") val isManual: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("is_launched") val isLaunched: Boolean? = false
)

data class ManualQuizRequest(
    @SerializedName("subject") val subject: String,
    @SerializedName("class") val classSelection: String,
    @SerializedName("chapter") val chapter: String,
    @SerializedName("quiz_type") val quizType: String,
    @SerializedName("scheduled_start") val scheduledStart: String,
    @SerializedName("questions") val questions: List<ManualQuestion>
)

data class ManualQuestion(
    @SerializedName("question_text") val questionText: String,
    @SerializedName("option_a") val optionA: String,
    @SerializedName("option_b") val optionB: String,
    @SerializedName("option_c") val optionC: String,
    @SerializedName("option_d") val optionD: String,
    @SerializedName("correct_option") val correctOption: String, // "A", "B", "C", "D"
    @SerializedName("explanation") val explanation: String? = null
)

data class AiQuizRequest(
    @SerializedName("subject") val subject: String,
    @SerializedName("class") val classSelection: String,
    @SerializedName("chapter") val chapter: String,
    @SerializedName("quiz_type") val quizType: String,
    @SerializedName("scheduled_start") val scheduledStart: String
)

data class PendingStudentsResponse(
    @SerializedName("students") val students: List<PendingStudentInfo>
)

data class PendingStudentInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("class") val classSelection: String,
    @SerializedName("status") val status: String,
    @SerializedName("student_id") val studentId: String?,
    @SerializedName("rejected_by") val rejectedBy: String?,
    @SerializedName("rejected_date") val rejectedDate: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("is_resignup") val isResignup: Int? = 0
)

data class TransactionResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("student_email") val studentEmail: String,
    @SerializedName("month") val month: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("txn_ref_id") val txnRefId: String,
    @SerializedName("utr_number") val utrNumber: String?,
    @SerializedName("status") val status: String,
    @SerializedName("rejection_reason") val rejectionReason: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class EnquiryResponse(
    @SerializedName("enquiries") val enquiries: List<EnquiryInfo>
)

data class EnquiryInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("class") val classSelection: String,
    @SerializedName("subject") val subject: String? = null,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String, // "Pending", "Contacted", "Admitted", "Rejected"
    @SerializedName("remarks") val remarks: String? = null,
    @SerializedName("created_at") val createdAt: String
)

data class LaunchMultipleQuizzesRequest(
    @SerializedName("quiz_ids") val quizIds: List<String>
)

data class RemoteHomeworkItem(
    @SerializedName("id") val id: Any? = null,
    @SerializedName("class") val classLevel: String? = null,
    @SerializedName("subject") val subject: String? = null,
    @SerializedName("topic") val topic: String? = null,
    @SerializedName("question_text") val questionText: String? = null,
    @SerializedName("image_name") val imageName: String? = null,
    @SerializedName("image_data") val imageData: String? = null,
    @SerializedName("assigned_date") val assignedDate: String? = null,
    @SerializedName("created_by") val createdBy: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class StudentHomeworkResponse(
    @SerializedName("studentClass") val studentClass: String? = null,
    @SerializedName("homework") val homework: List<RemoteHomeworkItem> = emptyList()
)

data class AdminHomeworkResponse(
    @SerializedName("homework") val homework: List<RemoteHomeworkItem> = emptyList()
)

data class CreateHomeworkRequest(
    @SerializedName("class") val classLevel: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("topic") val topic: String? = null,
    @SerializedName("question_text") val questionText: String? = null,
    @SerializedName("image_data") val imageData: String? = null,
    @SerializedName("image_name") val imageName: String? = null,
    @SerializedName("assigned_date") val assignedDate: String
)

data class UpdateHomeworkRequest(
    @SerializedName("class") val classLevel: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("topic") val topic: String? = null,
    @SerializedName("question_text") val questionText: String? = null,
    @SerializedName("image_data") val imageData: String? = null,
    @SerializedName("image_name") val imageName: String? = null,
    @SerializedName("assigned_date") val assignedDate: String
)

data class RemoteNotificationItem(
    @SerializedName("id") val id: Any,
    @SerializedName("recipient_email") val recipientEmail: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("link_tab") val linkTab: String? = null,
    @SerializedName("is_read") val isRead: Int = 0,
    @SerializedName("is_hidden") val isHidden: Int = 0,
    @SerializedName("created_at") val createdAt: String? = null
)

data class NotificationsListResponse(
    @SerializedName("notifications") val notifications: List<RemoteNotificationItem> = emptyList(),
    @SerializedName("unreadCount") val unreadCount: Int = 0
)

data class SendCustomNotificationRequest(
    @SerializedName("recipient_email") val recipientEmail: String? = null,
    @SerializedName("target_class") val targetClass: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("link_tab") val linkTab: String? = null
)

data class PracticeQuizRequest(
    @SerializedName("class") val classLevel: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("numQuestions") val numQuestions: Int = 10
)

data class QuizHistoryItem(
    @SerializedName("id") val id: Any,
    @SerializedName("quiz_id") val quizId: Any,
    @SerializedName("score") val score: Int,
    @SerializedName("total_questions") val totalQuestions: Int,
    @SerializedName("submitted_at") val submittedAt: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("class") val classLevel: String,
    @SerializedName("chapter") val chapter: String? = null,
    @SerializedName("quiz_type") val quizType: String? = null
)

data class QuizHistoryResponse(
    @SerializedName("history") val history: List<QuizHistoryItem> = emptyList()
)

