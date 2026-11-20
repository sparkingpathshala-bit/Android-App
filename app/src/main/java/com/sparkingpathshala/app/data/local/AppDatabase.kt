package com.sparkingpathshala.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUserDirect(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearUser()
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAttendanceFlow(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: List<AttendanceEntity>)

    @Query("DELETE FROM attendance")
    suspend fun clearAttendance()

    @Transaction
    suspend fun replaceAllAttendance(attendance: List<AttendanceEntity>) {
        clearAttendance()
        insertAttendance(attendance)
    }
}

@Dao
interface FeeDao {
    @Query("SELECT * FROM fees ORDER BY dueDate ASC")
    fun getFeesFlow(): Flow<List<FeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFees(fees: List<FeeEntity>)

    @Query("DELETE FROM fees")
    suspend fun clearFees()

    @Transaction
    suspend fun replaceAllFees(fees: List<FeeEntity>) {
        clearFees()
        insertFees(fees)
    }
}

@Dao
interface ResultDao {
    @Query("SELECT * FROM results ORDER BY date DESC")
    fun getResultsFlow(): Flow<List<ResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<ResultEntity>)

    @Query("DELETE FROM results")
    suspend fun clearResults()

    @Transaction
    suspend fun replaceAllResults(results: List<ResultEntity>) {
        clearResults()
        insertResults(results)
    }
}

@Dao
interface StudyMaterialDao {
    @Query("SELECT * FROM study_materials ORDER BY title ASC")
    fun getAllMaterialsFlow(): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials WHERE classLevel = :classLevel ORDER BY title ASC")
    fun getMaterialsByClassFlow(classLevel: String): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials WHERE id = :id LIMIT 1")
    suspend fun getMaterialById(id: String): StudyMaterialEntity?

    @Query("SELECT * FROM study_materials WHERE isDownloaded = 1")
    suspend fun getAllDownloadedMaterials(): List<StudyMaterialEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<StudyMaterialEntity>)

    @Update
    suspend fun updateMaterial(material: StudyMaterialEntity)

    @Query("DELETE FROM study_materials")
    suspend fun clearMaterials()

    @Query("DELETE FROM study_materials WHERE isDownloaded = 0")
    suspend fun clearNonDownloadedMaterials()

    @Query("DELETE FROM study_materials WHERE id = :id")
    suspend fun deleteMaterial(id: String)

    @Transaction
    suspend fun replaceMaterials(materials: List<StudyMaterialEntity>) {
        clearNonDownloadedMaterials()
        insertMaterials(materials)
    }
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes ORDER BY title ASC")
    fun getQuizzesFlow(): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes ORDER BY title ASC")
    suspend fun getAllQuizzes(): List<QuizEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<QuizEntity>)

    @Query("UPDATE quizzes SET attempted = :attempted, score = :score WHERE id = :quizId")
    suspend fun updateQuizResult(quizId: String, attempted: Boolean, score: Int)

    @Query("DELETE FROM quizzes WHERE id = :id")
    suspend fun deleteQuiz(id: String)

    @Query("DELETE FROM quizzes")
    suspend fun clearQuizzes()

    @Transaction
    suspend fun replaceAllQuizzes(quizzes: List<QuizEntity>) {
        clearQuizzes()
        insertQuizzes(quizzes)
    }
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE quizId = :quizId ORDER BY id ASC")
    fun getQuestionsFlow(quizId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    suspend fun getQuestionsByIds(ids: List<String>): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("DELETE FROM questions WHERE quizId = :quizId")
    suspend fun deleteQuestionsByQuizId(quizId: String)

    @Query("DELETE FROM questions")
    suspend fun clearQuestions()

    @Transaction
    suspend fun replaceQuestions(quizId: String, questions: List<QuestionEntity>) {
        deleteQuestionsByQuizId(quizId)
        insertQuestions(questions)
    }
}

@Dao
interface HomeworkDao {
    @Query("SELECT * FROM homework ORDER BY createdAt DESC")
    fun getAllHomeworkFlow(): Flow<List<HomeworkEntity>>

    @Query("SELECT * FROM homework WHERE classLevel = :classLevel OR classLevel = 'All' ORDER BY createdAt DESC")
    fun getHomeworkByClassFlow(classLevel: String): Flow<List<HomeworkEntity>>

    @Query("SELECT * FROM homework WHERE id = :id LIMIT 1")
    suspend fun getHomeworkById(id: String): HomeworkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(items: List<HomeworkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleHomework(item: HomeworkEntity)

    @Update
    suspend fun updateHomework(item: HomeworkEntity)

    @Query("DELETE FROM homework WHERE id = :id")
    suspend fun deleteHomework(id: String)

    @Query("DELETE FROM homework WHERE createdAt < :cutoffTimestamp")
    suspend fun purgeOldHomework(cutoffTimestamp: Long): Int

    @Query("DELETE FROM homework WHERE isDownloaded = 0")
    suspend fun clearNonDownloadedHomework()

    @Query("DELETE FROM homework")
    suspend fun clearHomework()

    @Transaction
    suspend fun replaceHomework(items: List<HomeworkEntity>) {
        clearNonDownloadedHomework()
        insertHomework(items)
    }
}

@Dao
interface GuestTelemetryDao {
    @Query("SELECT * FROM guest_telemetry ORDER BY lastActiveTimestamp DESC")
    fun getAllGuestsFlow(): Flow<List<GuestTelemetryEntity>>

    @Query("SELECT * FROM guest_telemetry WHERE guestId = :guestId LIMIT 1")
    suspend fun getGuestById(guestId: String): GuestTelemetryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuest(guest: GuestTelemetryEntity)

    @Update
    suspend fun updateGuest(guest: GuestTelemetryEntity)

    @Query("DELETE FROM guest_telemetry WHERE lastActiveTimestamp < :cutoffTimestamp")
    suspend fun purgeOldGuests(cutoffTimestamp: Long): Int
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE (recipientRole = 'all' OR recipientRole = :role) AND (targetClass = 'All' OR targetClass = 'all' OR targetClass = :classLevel OR :classLevel = 'All') ORDER BY timestamp DESC")
    fun getNotificationsForStudentFlow(role: String, classLevel: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE recipientRole = 'all' OR recipientRole = 'admin' ORDER BY timestamp DESC")
    fun getNotificationsForAdminFlow(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE (recipientRole = 'all' OR recipientRole = :role) AND (targetClass = 'All' OR targetClass = 'all' OR targetClass = :classLevel OR :classLevel = 'All') AND isRead = 0")
    fun getUnreadCountForStudentFlow(role: String, classLevel: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM notifications WHERE (recipientRole = 'all' OR recipientRole = 'admin') AND isRead = 0")
    fun getUnreadCountForAdminFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotificationIgnore(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()

    @Query("DELETE FROM notifications WHERE timestamp < :cutoffTimestamp")
    suspend fun purgeOldNotifications(cutoffTimestamp: Long): Int

    @Query("DELETE FROM notifications WHERE id NOT IN (SELECT id FROM notifications ORDER BY timestamp DESC LIMIT :keepLimit)")
    suspend fun trimNotificationHistory(keepLimit: Int): Int
}

@Database(
    entities = [
        UserEntity::class,
        AttendanceEntity::class,
        FeeEntity::class,
        ResultEntity::class,
        StudyMaterialEntity::class,
        QuizEntity::class,
        QuestionEntity::class,
        HomeworkEntity::class,
        GuestTelemetryEntity::class,
        NotificationEntity::class
    ],
    version = 12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun feeDao(): FeeDao
    abstract fun resultDao(): ResultDao
    abstract fun studyMaterialDao(): StudyMaterialDao
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
    abstract fun homeworkDao(): HomeworkDao
    abstract fun guestTelemetryDao(): GuestTelemetryDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sparking_pathshala_db"
                )
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
