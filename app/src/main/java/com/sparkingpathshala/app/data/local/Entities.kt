package com.sparkingpathshala.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val rollNo: String,
    val classLevel: String,
    val token: String,
    val role: String = "student",
    val loginTimestamp: Long = System.currentTimeMillis()
)

fun UserEntity?.isAdmin(): Boolean {
    if (this == null) return false
    val r = (role ?: "").lowercase().trim()
    val e = (email ?: "").lowercase().trim()
    return r == "admin" || r == "billing_admin" || r == "owner" || r.contains("admin") || e == "sajjanmallik@sparkingpathshala.in"
}

fun UserEntity?.isGuest(): Boolean {
    if (this == null) return false
    val r = role ?: ""
    return r.equals("guest", ignoreCase = true)
}

fun UserEntity?.isStudent(): Boolean {
    if (this == null) return false
    return !isAdmin() && !isGuest()
}

@Entity(
    tableName = "attendance",
    indices = [Index(value = ["date"])]
)
data class AttendanceEntity(
    @PrimaryKey val date: String, // format: "yyyy-MM-dd"
    val isPresent: Boolean,
    val remarks: String
)

@Entity(
    tableName = "fees",
    indices = [
        Index(value = ["dueDate"]),
        Index(value = ["status"]),
        Index(value = ["isPaid"])
    ]
)
data class FeeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Double,
    val dueDate: String,
    val isPaid: Boolean,
    val paidDate: String,
    val receiptUrl: String,
    val status: String = "Unpaid",
    val month: String = "",
    val amountPaid: Double = 0.0,
    val adjustedAmount: Double = 0.0,
    val remarks: String = "",
    val paymentMode: String = ""
)

@Entity(
    tableName = "results",
    indices = [
        Index(value = ["date"]),
        Index(value = ["subject"]),
        Index(value = ["examType"])
    ]
)
data class ResultEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val marksObtained: Double,
    val maxMarks: Double,
    val examType: String,
    val date: String
)

@Entity(
    tableName = "study_materials",
    indices = [
        Index(value = ["classLevel"]),
        Index(value = ["subject"]),
        Index(value = ["category"]),
        Index(value = ["isDownloaded"])
    ]
)
data class StudyMaterialEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val subject: String,
    val fileUrl: String,
    val localPath: String,
    val isDownloaded: Boolean,
    val classLevel: String,
    val category: String = "guides",
    val status: String = "active",
    val allowedEmails: String? = null
)

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val subject: String,
    val numQuestions: Int,
    val classLevel: String,
    val attempted: Boolean = false,
    val score: Int? = null,
    val totalQuestions: Int? = null
)

@Entity(
    tableName = "questions",
    indices = [Index(value = ["quizId"])]
)
data class QuestionEntity(
    @PrimaryKey val id: String,
    val quizId: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: Int, // 0 to 3
    val explanation: String
) : Serializable

fun QuestionEntity.shuffledOptions(): QuestionEntity {
    val optionsList = listOf(optionA, optionB, optionC, optionD)
    val correctText = optionsList.getOrNull(correctAnswer) ?: optionA
    val shuffled = optionsList.shuffled()
    val newCorrectAnswer = shuffled.indexOf(correctText).coerceAtLeast(0)
    return this.copy(
        optionA = shuffled.getOrElse(0) { "" },
        optionB = shuffled.getOrElse(1) { "" },
        optionC = shuffled.getOrElse(2) { "" },
        optionD = shuffled.getOrElse(3) { "" },
        correctAnswer = newCorrectAnswer
    )
}

@Entity(
    tableName = "homework",
    indices = [
        Index(value = ["classLevel"]),
        Index(value = ["createdAt"]),
        Index(value = ["subject"]),
        Index(value = ["dueDate"])
    ]
)
data class HomeworkEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val subject: String,
    val classLevel: String,
    val topic: String? = null,
    val batch: String? = null,
    val fileUrl: String = "",
    val fileType: String = "PDF", // "IMAGE", "PDF", "DOCX", "TEXT"
    val localPath: String = "",
    val isDownloaded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: String = "",
    val maxMarks: Int = 100
) : Serializable

@Entity(
    tableName = "guest_telemetry",
    indices = [
        Index(value = ["guestId"]),
        Index(value = ["lastActiveTimestamp"])
    ]
)
data class GuestTelemetryEntity(
    @PrimaryKey val id: String,
    val guestId: String,
    val deviceName: String,
    val deviceModel: String,
    val location: String,
    val ipAddress: String,
    val firstSeenTimestamp: Long = System.currentTimeMillis(),
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val actionsCount: Int = 1
) : Serializable

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["recipientRole", "targetClass"]),
        Index(value = ["isRead"])
    ]
)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val recipientRole: String = "student", // "student", "admin", "all"
    val targetClass: String = "All",
    val targetUserId: String? = null,
    val title: String,
    val message: String,
    val category: String = "GENERAL", // "HOMEWORK", "DOUBT", "FEE", "RESULTS", "ATTENDANCE", "UPDATE", "SIGNUP", "QUIZ", "SECURITY"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionRoute: String? = null,
    val priority: String = "NORMAL" // "HIGH", "NORMAL", "LOW"
) : Serializable


