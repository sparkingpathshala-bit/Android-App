package com.sparkingpathshala.app.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import androidx.core.net.toUri
import okio.Buffer
import java.io.IOException

data class MockDoubt(
    val id: String,
    val studentEmail: String,
    val studentName: String,
    val title: String,
    val subject: String,
    val message: String,
    val fileName: String?,
    val fileData: String?,
    var status: String,
    val createdAt: String
)

data class MockDoubtReply(
    val id: String,
    val doubtId: String,
    val senderEmail: String,
    val senderName: String,
    val senderRole: String,
    val message: String,
    val fileName: String?,
    val fileData: String?,
    val createdAt: String
)

data class MockQuiz(
    val id: String,
    val subject: String,
    val classSelection: String,
    val chapter: String,
    val quizType: String,
    val scheduledStart: String, // format: "yyyy-MM-dd HH:mm"
    val questions: List<QuestionResponse>,
    val isManual: Int,
    val createdAt: String
)

fun QuestionResponse.shuffled(): QuestionResponse {
    val optionsList = listOf(optionA, optionB, optionC, optionD)
    val correctIndex = when (correctOption?.uppercase()) {
        "A" -> 0
        "B" -> 1
        "C" -> 2
        "D" -> 3
        else -> 0
    }
    val correctText = optionsList.getOrNull(correctIndex) ?: optionA
    val shuffledList = optionsList.shuffled()
    val newCorrectIndex = shuffledList.indexOf(correctText).coerceAtLeast(0)
    val newCorrectOption = when (newCorrectIndex) {
        0 -> "A"
        1 -> "B"
        2 -> "C"
        3 -> "D"
        else -> "A"
    }
    return this.copy(
        optionA = shuffledList.getOrElse(0) { "" },
        optionB = shuffledList.getOrElse(1) { "" },
        optionC = shuffledList.getOrElse(2) { "" },
        optionD = shuffledList.getOrElse(3) { "" },
        correctOption = newCorrectOption
    )
}

class MockInterceptor(private val useMock: Boolean = true) : Interceptor {
    private val gson = com.google.gson.Gson()
    companion object {
        var currentUserClass: String = "Class 10"
        var currentUserEmail: String = "sajjankumar@gmail.com"
        var currentUserName: String = "Sajjan Kumar Mallik"
        var currentUserId: String = "student_101"

        val launchedQuizIds = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()
        val attemptedQuizIds = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

        fun parseDate(dateStr: String): java.util.Date? {
            val formats = listOf("yyyy-MM-dd HH:mm", "dd/MM/yyyy HH:mm", "dd-MM-yyyy HH:mm")
            for (format in formats) {
                try {
                    val sdf = java.text.SimpleDateFormat(format, java.util.Locale.US).apply {
                        timeZone = java.util.TimeZone.getTimeZone("Asia/Kolkata")
                    }
                    val date = sdf.parse(dateStr)
                    if (date != null) return date
                } catch (e: Exception) {
                    // Try next format
                }
            }
            return null
        }

        val mockApprovedStudentsList = mutableListOf(
            AdminStudentInfo("student_101", "Sajjan Kumar Mallik", "sajjankumar@gmail.com", "Class 10", "approved", 1500.0, "9876543210"),
            AdminStudentInfo("student_102", "Manish Dutta", "manishdutta31@gmail.com", "Class 11", "approved", 1500.0, "9876543211"),
            AdminStudentInfo("student_103", "Govind Kumar Mallik", "govindmallik@gmail.com", "Class 12", "approved", 2000.0, "9876543212"),
            AdminStudentInfo("student_104", "Aditya Nanda", "aditya.nanda@gmail.com", "Class 10", "approved", 1500.0, "9810234567"),
            AdminStudentInfo("student_105", "Sneha Kumari", "sneha.kumari@gmail.com", "Class 11", "approved", 1800.0, "9910456789"),
            AdminStudentInfo("student_106", "Rohan Sharma", "rohan.sharma@gmail.com", "Class 10", "approved", 1500.0, "9711345678"),
            AdminStudentInfo("student_107", "Pooja Patel", "pooja.patel@gmail.com", "Class 9", "approved", 1200.0, "9560456123"),
            AdminStudentInfo("student_108", "Aryan Dev", "aryan.dev@gmail.com", "Class 12", "approved", 2000.0, "9818456789"),
            AdminStudentInfo("student_109", "Karan Singh", "karan.singh@gmail.com", "Class 10", "approved", 1500.0, "9999123456"),
            AdminStudentInfo("student_110", "Meera Sharma", "meera.sharma@gmail.com", "Class 11", "approved", 1800.0, "9899456123"),
            AdminStudentInfo("student_111", "Riya Banerjee", "riya.banerjee@gmail.com", "Class 12", "approved", 2000.0, "9871456321"),
            AdminStudentInfo("student_112", "Aniket Kumar", "aniket.kumar@gmail.com", "Class 9", "approved", 1200.0, "9560123456"),
            AdminStudentInfo("student_113", "Shreya Bose", "shreya.bose@gmail.com", "Class 10", "approved", 1500.0, "9811234567"),
            AdminStudentInfo("student_114", "Vikram Singh", "vikram.singh@gmail.com", "Class 11", "approved", 1800.0, "9958123456"),
            AdminStudentInfo("student_115", "Nehal Gupta", "nehal.gupta@gmail.com", "Class 12", "approved", 2000.0, "9810789456"),
            AdminStudentInfo("student_116", "Yash Gupta", "yash.gupta@gmail.com", "Class 10", "approved", 1500.0, "9873456123"),
            AdminStudentInfo("student_117", "Test User", "test@gmail.com", "Class 10", "approved", 1500.0, "9999999999")
        )

        val pendingStudentsList = mutableListOf(
            PendingStudentInfo(
                id = "pending_01",
                name = "Harish Chander",
                email = "harish.c@gmail.com",
                mobile = "9876012345",
                classSelection = "Class 10",
                status = "pending_approval",
                studentId = null,
                rejectedBy = null,
                rejectedDate = null,
                createdAt = "2026-06-18T12:00:00Z"
            ),
            PendingStudentInfo(
                id = "pending_02",
                name = "Kavita Roy",
                email = "kavita.roy@gmail.com",
                mobile = "9910023456",
                classSelection = "Class 12",
                status = "pending_approval",
                studentId = null,
                rejectedBy = null,
                rejectedDate = null,
                createdAt = "2026-06-19T11:00:00Z"
            )
        )

        val mockQuizzesList = mutableListOf(
            MockQuiz(
                id = "quiz_01",
                subject = "Physics",
                classSelection = "Class 10",
                chapter = "Optics",
                quizType = "15-min",
                scheduledStart = "2026-06-09 10:00",
                questions = listOf(
                    QuestionResponse(
                        id = "q_01",
                        quizId = "quiz_01",
                        questionText = "What is the focal length of a flat plane mirror?",
                        optionA = "0",
                        optionB = "Infinity",
                        optionC = "10 cm",
                        optionD = "-10 cm",
                        correctOption = "B",
                        explanation = "A plane mirror has an infinite focal length because it doesn't converge or diverge rays."
                    ),
                    QuestionResponse(
                        id = "q_02",
                        quizId = "quiz_01",
                        questionText = "Which lens is used to correct myopia?",
                        optionA = "Convex",
                        optionB = "Concave",
                        optionC = "Bifocal",
                        optionD = "Cylindrical",
                        correctOption = "B",
                        explanation = "Myopia (short-sightedness) requires a diverging concave lens to bring the image onto the retina."
                    )
                ).map { it.shuffled() },
                isManual = 1,
                createdAt = "2026-06-09T08:00:00Z"
            )
        )

        val enquiriesList = mutableListOf(
            EnquiryInfo("enq_01", "Rajesh Kumar", "rajesh.k@gmail.com", "9812345678", "Class 10", "Mathematics", "Interested in registration for Class 10 CBSE boards crash course.", "Pending", "Pending call-back", "2026-06-18T10:00:00Z"),
            EnquiryInfo("enq_02", "Karan Verma", "karan.v@gmail.com", "9911223344", "Class 11", "Physics", "Enquiring about evening batch timings and weekly test series packages.", "Contacted", "Spoke with father, will join next Monday.", "2026-06-19T11:30:00Z"),
            EnquiryInfo("enq_03", "Priya Sharma", "priya.sharma@gmail.com", "9560443322", "Class 12", "Chemistry", "Wants to enroll in JEE organic chemistry special weekend classes.", "Admitted", "First installment fee paid.", "2026-06-19T15:00:00Z")
        )

        val doubtsList = mutableListOf(
            MockDoubt(
                id = "doubt_01",
                studentEmail = "sajjankumar@gmail.com",
                studentName = "Sajjan Kumar Mallik",
                title = "Query regarding Optics refraction",
                subject = "Physics",
                message = "Why does a concave lens always form a virtual image?",
                fileName = "lens_diagram.jpg",
                fileData = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
                status = "replied",
                createdAt = "2026-06-08T15:30:00Z"
            ),
            MockDoubt(
                id = "doubt_02",
                studentEmail = "sajjankumar@gmail.com",
                studentName = "Sajjan Kumar Mallik",
                title = "Trigonometry values question",
                subject = "Mathematics",
                message = "Can someone explain the derivation of sin^2 x + cos^2 x = 1?",
                fileName = null,
                fileData = null,
                status = "pending",
                createdAt = "2026-06-09T09:00:00Z"
            )
        )

        val repliesList = mutableListOf(
            MockDoubtReply(
                id = "rep_01",
                doubtId = "doubt_01",
                senderEmail = "sajjanmallik@sparkingpathshala.in",
                senderName = "Sajjan Mallik",
                senderRole = "admin",
                message = "Because light rays passing through a concave lens diverge. Since they never meet on the other side, we trace them backward to form a virtual image.",
                fileName = null,
                fileData = null,
                createdAt = "2026-06-08T16:00:00Z"
            )
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!useMock) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
        val uri = request.url.toUri()
        val path = uri.path

        val requestBodyString = try {
            val buffer = Buffer()
            request.body?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            ""
        }

        // Simulate network latency (100ms)
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Pending Admin Approval check
        if ((path.endsWith("api/auth/login") || path.endsWith("api/auth/signup")) && requestBodyString.contains("pending@sparkingpathshala.in")) {
            return Response.Builder()
                .code(403)
                .message("Forbidden")
                .request(request)
                .protocol(Protocol.HTTP_2)
                .body("{\"message\":\"Your registration request is pending admin approval. Please contact the administrator.\"}".toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
                .addHeader("content-type", "application/json")
                .build()
        }

        val responseString = when {
            path.endsWith("api/auth/login") -> {
                val isAdmin = requestBodyString.contains("sajjanmallik@sparkingpathshala.in") ||
                              requestBodyString.contains("manishdutta@sparkingpathshala.in") ||
                              requestBodyString.contains("govindmallik@sparkingpathshala.in") ||
                              requestBodyString.contains("admin")
                if (isAdmin) {
                    currentUserClass = "All"
                    val name = when {
                        requestBodyString.contains("manishdutta") -> "Manish Dutta"
                        requestBodyString.contains("govindmallik") -> "Govind Mallik"
                        else -> "Sajjan Mallik"
                    }
                    val email = when {
                        requestBodyString.contains("manishdutta") -> "manishdutta@sparkingpathshala.in"
                        requestBodyString.contains("govindmallik") -> "govindmallik@sparkingpathshala.in"
                        else -> "sajjanmallik@sparkingpathshala.in"
                    }
                    """
                    {
                        "token": "bearer_mock_jwt_token_admin",
                        "user": {
                            "id": "admin_001",
                            "name": "$name",
                            "email": "$email",
                            "class": "All",
                            "role": "admin",
                            "status": "approved"
                        }
                    }
                    """.trimIndent()
                } else {
                    val matchedStudent = synchronized(mockApprovedStudentsList) {
                        mockApprovedStudentsList.find { requestBodyString.contains(it.email) }
                    }
                    
                    // Check if student account is deleted or inactive
                    if (matchedStudent != null && (matchedStudent.status == "deleted" || matchedStudent.status == "inactive")) {
                        return Response.Builder()
                            .code(403)
                            .message("Forbidden")
                            .request(request)
                            .protocol(Protocol.HTTP_2)
                            .body("{\"message\":\"Your account has been deactivated. Please contact the administrator.\"}".toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
                            .addHeader("content-type", "application/json")
                            .build()
                    }
                    
                    val studentName = matchedStudent?.name ?: "Sajjan Kumar Mallik"
                    val studentEmail = matchedStudent?.email ?: "sajjankumar@gmail.com"
                    val studentClass = matchedStudent?.classSelection ?: "Class 10"
                    val studentId = matchedStudent?.id ?: "student_101"
                    val studentStatus = matchedStudent?.status ?: "approved"
                    
                    currentUserEmail = studentEmail
                    currentUserName = studentName
                    currentUserId = studentId
                    currentUserClass = studentClass
                    
                    """
                    {
                        "token": "bearer_mock_jwt_token_${studentId}",
                        "user": {
                            "id": "$studentId",
                            "name": "$studentName",
                            "email": "$studentEmail",
                            "class": "$studentClass",
                            "role": "student",
                            "status": "$studentStatus"
                        }
                    }
                    """.trimIndent()
                }
            }
            path.endsWith("api/auth/signup") -> {
                """
                {
                    "message": "Registration successful! Awaiting Admin approval."
                }
                """.trimIndent()
            }
            path.endsWith("api/student/dashboard") -> {
                """
                {
                    "attendance": [
                        {"id": "att_1", "date": "2026-06-18", "status": "Present"},
                        {"id": "att_2", "date": "2026-06-19", "status": "Present"}
                    ],
                    "results": [
                        {"id": "res_1", "subject": "Physics", "marks_obtained": 88.0, "max_marks": 100.0, "exam_date": "2026-05-31"},
                        {"id": "res_2", "subject": "Chemistry", "marks_obtained": 92.5, "max_marks": 100.0, "exam_date": "2026-05-31"},
                        {"id": "res_3", "subject": "Maths", "marks_obtained": 95.0, "max_marks": 100.0, "exam_date": "2026-06-01"}
                    ],
                    "fees": [
                        {"id": "fee_1", "student_email": "$currentUserEmail", "amount_paid": 1500.0, "total_due": 1500.0, "payment_date": "2026-06-15", "status": "Paid", "month": "May", "adjusted_amount": 0.0, "remarks": "Regular Payment", "payment_mode": "UPI", "transaction_reference_number": "344343242666"},
                        {"id": "fee_2", "student_email": "$currentUserEmail", "amount_paid": 0.0, "total_due": 1500.0, "payment_date": "N/A", "status": "Unpaid", "month": "June", "adjusted_amount": 0.0, "remarks": "Auto-initialized", "payment_mode": "UPI", "transaction_reference_number": null}
                    ]
                }
                """.trimIndent()
            }
            path.contains("api/library/resources/") && path.endsWith("download") -> {
                """
                {
                    "fileName": "Tenses Exercises.pdf",
                    "fileData": "data:application/pdf;base64,JVBERi0xLjQKJedfX2VuZG9iagoxIDAgb2JqPDwvVHlwZS9DYXRhbG9nL1BhZ2VzIDIgMCBSPj5lbmRvYmoKMiAwIG9iajw8L1R5cGUvUGFnZXMvS2lkc1szIDAgUl0vQ291bnQgMT4+ZW5kb2JqCjMgMCBvYmo8PC9VHlwZS9QYWdlL01lZGlhQm94WzAgMCA1OTUgODQyXS9QYXJlbnQgMiAwIFI+PmVuZG9iagp0cmFpbGVyPDwvUm9vdCAxIDAgUj4+CiUlRU9G"
                }
                """.trimIndent()
            }
            path.contains("api/library/resources/") -> {
                val category = path.substringAfterLast("/")
                val resourcesJson = when (category) {
                    "guides" -> """
                        [
                            {
                                "id": "mat_01",
                                "title": "Light: Reflection & Refraction Guide",
                                "category": "guides",
                                "class": "Class 10",
                                "subject": "Physics",
                                "file_name": "light_notes.pdf",
                                "file_size": "2.4 MB",
                                "file_data": "stored_path_1"
                            }
                        ]
                    """.trimIndent()
                    "question-bank" -> """
                        [
                            {
                                "id": "mat_02",
                                "title": "Acids & Bases Practice Question Bank",
                                "category": "question-bank",
                                "class": "Class 10",
                                "subject": "Chemistry",
                                "file_name": "chemistry_practice.pdf",
                                "file_size": "1.1 MB",
                                "file_data": "stored_path_2"
                            }
                        ]
                    """.trimIndent()
                    "test-series" -> """
                        [
                            {
                                "id": "mat_03",
                                "title": "Maths Weekly Evaluation Test Series",
                                "category": "test-series",
                                "class": "Class 10",
                                "subject": "Mathematics",
                                "file_name": "maths_test.pdf",
                                "file_size": "1.5 MB",
                                "file_data": "stored_path_3"
                            }
                        ]
                    """.trimIndent()
                    "notes" -> """
                        [
                            {
                                "id": "mat_04",
                                "title": "Carbon & Its Compounds Concept Notes",
                                "category": "notes",
                                "class": "Class 10",
                                "subject": "Chemistry",
                                "file_name": "carbon_notes.pdf",
                                "file_size": "1.9 MB",
                                "file_data": "stored_path_4"
                            }
                        ]
                    """.trimIndent()
                    else -> "[]"
                }
                
                """
                {
                    "resources": $resourcesJson
                }
                """.trimIndent()
            }
            path.endsWith("api/doubts") -> {
                try {
                    val jsonMap = gson.fromJson(requestBodyString, Map::class.java)
                    val title = jsonMap["title"] as? String ?: ""
                    val subject = jsonMap["subject"] as? String ?: ""
                    val message = jsonMap["message"] as? String ?: ""
                    val fileName = (jsonMap["fileName"] as? String)?.takeIf { it.isNotEmpty() && it != "null" }
                    val fileData = (jsonMap["fileData"] as? String)?.takeIf { it.isNotEmpty() && it != "null" }
                    
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
                    sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                    val createdAt = sdf.format(java.util.Date())
                    
                    val newDoubt = MockDoubt(
                        id = "doubt_${System.currentTimeMillis()}",
                        studentEmail = currentUserEmail,
                        studentName = currentUserName,
                        title = title,
                        subject = subject,
                        message = message,
                        fileName = fileName,
                        fileData = fileData,
                        status = "pending",
                        createdAt = createdAt
                    )
                    synchronized(doubtsList) {
                        doubtsList.add(newDoubt)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                """
                {
                    "message": "Doubt registered successfully!"
                }
                """.trimIndent()
            }
            path.endsWith("api/student/quiz/active") -> {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US)
                sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Kolkata")
                val now = java.util.Date()
                
                val matchedQuizzes = synchronized(mockQuizzesList) {
                    mockQuizzesList.filter { q ->
                        val classMatches = q.classSelection.equals(currentUserClass, ignoreCase = true) || 
                                           q.classSelection.equals("All", ignoreCase = true) || 
                                           q.classSelection.equals("All Classes", ignoreCase = true)
                        if (!classMatches) return@filter false
                        
                        val isLaunchedVal = synchronized(launchedQuizIds) { launchedQuizIds.contains(q.id) } || (parseDate(q.scheduledStart)?.let { now.after(it) } ?: false)
                        
                        isLaunchedVal
                    }
                }
                
                val activeQuizzesList = if (matchedQuizzes.isEmpty()) {
                    listOf(
                        MockQuiz(
                            id = "quiz_default",
                            subject = "General Knowledge",
                            classSelection = currentUserClass,
                            chapter = "General",
                            quizType = "15-min",
                            scheduledStart = "2026-06-09 10:00",
                            questions = listOf(
                                QuestionResponse(
                                    id = "q_def_1",
                                    quizId = "quiz_default",
                                    questionText = "Which planet is known as the Red Planet?",
                                    optionA = "Earth",
                                    optionB = "Mars",
                                    optionC = "Jupiter",
                                    optionD = "Saturn",
                                    correctOption = "B",
                                    explanation = "Mars is called the Red Planet because of iron oxide on its surface."
                                ).shuffled()
                            ),
                            isManual = 0,
                            createdAt = "2026-06-09T08:00:00Z"
                        )
                    )
                } else {
                    matchedQuizzes
                }
                
                val activeList = activeQuizzesList.map { activeQuiz ->
                    val quizMap = mapOf(
                        "id" to activeQuiz.id,
                        "subject" to activeQuiz.subject,
                        "chapter" to activeQuiz.chapter,
                        "quiz_type" to activeQuiz.quizType,
                        "scheduled_start" to activeQuiz.scheduledStart,
                        "class" to activeQuiz.classSelection
                    )
                    val questionsList = activeQuiz.questions.map { q ->
                        mapOf(
                            "id" to q.id,
                            "quizId" to q.quizId,
                            "questionText" to q.questionText,
                            "optionA" to q.optionA,
                            "optionB" to q.optionB,
                            "optionC" to q.optionC,
                            "optionD" to q.optionD,
                            "correctOption" to q.correctOption,
                            "correctAnswer" to when (q.correctOption?.uppercase()) {
                                "A" -> 0
                                "B" -> 1
                                "C" -> 2
                                "D" -> 3
                                else -> 0
                            },
                            "explanation" to (q.explanation ?: "N/A")
                        )
                    }
                    mapOf(
                        "quiz" to quizMap,
                        "questions" to questionsList,
                        "attempted" to synchronized(attemptedQuizIds) { attemptedQuizIds.contains(activeQuiz.id) },
                        "total_questions" to activeQuiz.questions.size
                    )
                }
                gson.toJson(activeList)
            }
            path.endsWith("api/auth/delete-account") -> {
                // Mark the current user as deleted in the approved list
                synchronized(mockApprovedStudentsList) {
                    val index = mockApprovedStudentsList.indexOfFirst { it.email == currentUserEmail }
                    if (index != -1) {
                        val s = mockApprovedStudentsList[index]
                        mockApprovedStudentsList[index] = s.copy(status = "deleted")
                    }
                }
                """
                {
                    "message": "Your account and all associated data have been permanently deleted."
                }
                """.trimIndent()
            }
            path.endsWith("api/auth/update-password") -> {
                """
                {
                    "message": "Password updated successfully!"
                }
                """.trimIndent()
            }
            path.endsWith("api/doubts/student") -> {
                val list = synchronized(doubtsList) {
                    doubtsList.filter { it.studentEmail == currentUserEmail }.map { d ->
                        mapOf(
                            "id" to d.id,
                            "student_email" to d.studentEmail,
                            "student_name" to d.studentName,
                            "title" to d.title,
                            "subject" to d.subject,
                            "message" to d.message,
                            "file_name" to d.fileName,
                            "file_data" to d.fileData,
                            "status" to d.status,
                            "created_at" to d.createdAt
                        )
                    }
                }
                gson.toJson(mapOf("doubts" to list))
            }
            path.endsWith("api/admin/doubts") -> {
                val list = synchronized(doubtsList) {
                    doubtsList.map { d ->
                        mapOf(
                            "id" to d.id,
                            "student_email" to d.studentEmail,
                            "student_name" to d.studentName,
                            "title" to d.title,
                            "subject" to d.subject,
                            "message" to d.message,
                            "file_name" to d.fileName,
                            "file_data" to d.fileData,
                            "status" to d.status,
                            "created_at" to d.createdAt
                        )
                    }
                }
                gson.toJson(mapOf("doubts" to list))
            }
            path.contains("api/doubts/") && path.endsWith("/replies") -> {
                val doubtId = path.substringAfter("api/doubts/").substringBefore("/replies")
                if (request.method == "POST") {
                    try {
                        val jsonMap = gson.fromJson(requestBodyString, Map::class.java)
                        val msg = jsonMap["message"] as? String ?: ""
                        val fileName = (jsonMap["fileName"] as? String)?.takeIf { it.isNotEmpty() && it != "null" }
                        val fileData = (jsonMap["fileData"] as? String)?.takeIf { it.isNotEmpty() && it != "null" }
                        
                        val token = request.header("Authorization") ?: ""
                        val isSenderAdmin = token.contains("admin")
                        
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
                        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                        val createdAt = sdf.format(java.util.Date())
                        
                        val newReply = MockDoubtReply(
                            id = "rep_${System.currentTimeMillis()}",
                            doubtId = doubtId,
                            senderEmail = if (isSenderAdmin) "sajjanmallik@sparkingpathshala.in" else currentUserEmail,
                            senderName = if (isSenderAdmin) "Sajjan Mallik" else currentUserName,
                            senderRole = if (isSenderAdmin) "admin" else "student",
                            message = msg,
                            fileName = fileName,
                            fileData = fileData,
                            createdAt = createdAt
                        )
                        synchronized(repliesList) {
                            repliesList.add(newReply)
                        }
                        
                        // Update parent doubt status
                        synchronized(doubtsList) {
                            val doubt = doubtsList.find { it.id == doubtId }
                            if (doubt != null) {
                                doubt.status = if (isSenderAdmin) "replied" else "pending"
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    """
                    {
                        "message": "Reply registered successfully!"
                    }
                    """.trimIndent()
                } else {
                    val list = synchronized(repliesList) {
                        repliesList.filter { it.doubtId == doubtId }.map { r ->
                            mapOf(
                                "id" to r.id,
                                "doubt_id" to r.doubtId,
                                "sender_email" to r.senderEmail,
                                "sender_name" to r.senderName,
                                "sender_role" to r.senderRole,
                                "message" to r.message,
                                "file_name" to r.fileName,
                                "file_data" to r.fileData,
                                "created_at" to r.createdAt
                            )
                        }
                    }
                    gson.toJson(mapOf("replies" to list))
                }
            }
            path.contains("api/admin/doubts/") && path.endsWith("/status") -> {
                val doubtId = path.substringAfter("api/admin/doubts/").substringBefore("/status")
                try {
                    val jsonMap = gson.fromJson(requestBodyString, Map::class.java)
                    val newStatus = jsonMap["status"] as? String ?: ""
                    synchronized(doubtsList) {
                        val doubt = doubtsList.find { it.id == doubtId }
                        if (doubt != null) {
                            doubt.status = newStatus
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                """
                {
                    "message": "Doubt ticket status updated successfully!"
                }
                """.trimIndent()
            }
            path.contains("api/admin/doubts/") -> {
                val doubtId = path.substringAfter("api/admin/doubts/").trim('/')
                synchronized(doubtsList) {
                    doubtsList.removeAll { it.id == doubtId }
                }
                synchronized(repliesList) {
                    repliesList.removeAll { it.doubtId == doubtId }
                }
                """
                {
                    "message": "Doubt ticket deleted successfully!"
                }
                """.trimIndent()
            }
            path.endsWith("api/student/quiz/submit") -> {
                val requestMap = try {
                    gson.fromJson(requestBodyString, Map::class.java)
                } catch (e: Exception) {
                    emptyMap<String, Any>()
                }
                val quizId = (requestMap["quizId"] as? String ?: requestMap["quiz_id"] as? String ?: "").takeIf { it.isNotEmpty() } ?: ""
                val answersObj = requestMap["answers"] as? Map<*, *>
                
                val targetQuiz = synchronized(mockQuizzesList) {
                    mockQuizzesList.find { it.id == quizId }
                }
                
                if (targetQuiz != null) {
                    synchronized(attemptedQuizIds) {
                        attemptedQuizIds.add(quizId)
                    }
                    var score = 0
                    val questionsList = targetQuiz.questions
                    for (q in questionsList) {
                        val submittedAns = (answersObj?.get(q.id) as? String) ?: ""
                        if (submittedAns.isNotEmpty() && submittedAns.uppercase() == q.correctOption?.uppercase()) {
                            score++
                        }
                    }
                    
                    val questionsListMap = questionsList.map { q ->
                        mapOf(
                            "id" to q.id,
                            "quiz_id" to q.quizId,
                            "question_text" to q.questionText,
                            "option_a" to q.optionA,
                            "option_b" to q.optionB,
                            "option_c" to q.optionC,
                            "option_d" to q.optionD,
                            "correct_option" to q.correctOption,
                            "explanation" to (q.explanation ?: "N/A")
                        )
                    }
                    
                    gson.toJson(
                        mapOf(
                            "message" to "Quiz submitted successfully!",
                            "score" to score,
                            "total_questions" to questionsList.size,
                            "questions" to questionsListMap
                        )
                    )
                } else {
                    synchronized(attemptedQuizIds) {
                        attemptedQuizIds.add(quizId)
                    }
                    // Fallback to hardcoded response if the quiz is not found
                    val fallbackQuestions = listOf(
                        mapOf(
                            "id" to "q_01",
                            "quiz_id" to "quiz_01",
                            "question_text" to "What is the focal length of a flat plane mirror?",
                            "option_a" to "0",
                            "option_b" to "Infinity",
                            "option_c" to "10 cm",
                            "option_d" to "-10 cm",
                            "correct_option" to "B",
                            "explanation" to "A plane mirror has an infinite focal length because it doesn't converge or diverge rays."
                        ),
                        mapOf(
                            "id" to "q_02",
                            "quiz_id" to "quiz_01",
                            "question_text" to "Which lens is used to correct myopia?",
                            "option_a" to "Convex",
                            "option_b" to "Concave",
                            "option_c" to "Bifocal",
                            "option_d" to "Cylindrical",
                            "correct_option" to "B",
                            "explanation" to "Myopia (short-sightedness) requires a diverging concave lens to bring the image onto the retina."
                        )
                    )
                    gson.toJson(
                        mapOf(
                            "message" to "Quiz submitted successfully!",
                            "score" to 2,
                            "total_questions" to 2,
                            "questions" to fallbackQuestions
                        )
                    )
                }
            }
            path.endsWith("api/admin/students") -> {
                val list = synchronized(mockApprovedStudentsList) {
                    mockApprovedStudentsList.map { student ->
                        mapOf(
                            "id" to student.id,
                            "name" to student.name,
                            "email" to student.email,
                            "class" to student.classSelection,
                            "status" to student.status,
                            "monthly_fee" to student.monthlyFee,
                            "mobile" to student.mobile
                        )
                    }
                }
                gson.toJson(mapOf("students" to list))
            }
            path.contains("api/admin/records/") -> {
                if (request.method != "GET") {
                    val recordType = path.substringAfter("api/admin/records/").substringBefore("?").trim('/')
                    if (recordType == "students") {
                        try {
                            val jsonMap = gson.fromJson(requestBodyString, Map::class.java)
                            val name = jsonMap["name"] as? String ?: ""
                            val email = jsonMap["email"] as? String ?: ""
                            val classSelection = jsonMap["class"] as? String ?: ""
                            val monthlyFeeVal = (jsonMap["monthly_fee"] as? Number)?.toDouble() ?: 1500.0
                            val mobile = jsonMap["mobile"] as? String ?: ""
                            
                            val newStudent = AdminStudentInfo(
                                id = "student_${System.currentTimeMillis()}",
                                name = name,
                                email = email,
                                classSelection = classSelection,
                                status = "approved",
                                monthlyFee = if (monthlyFeeVal.isNaN()) 1500.0 else monthlyFeeVal,
                                mobile = mobile
                            )
                            synchronized(mockApprovedStudentsList) {
                                mockApprovedStudentsList.add(newStudent)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    """
                    {
                      "message": "Record processed successfully!"
                    }
                    """.trimIndent()
                } else {
                    val recordType = path.substringAfter("api/admin/records/").substringBefore("?").trim('/')
                    when (recordType) {
                        "attendance" -> {
                            val list = listOf(
                                mapOf(
                                    "id" to "rec_att_01",
                                    "student_email" to "sajjankumar@gmail.com",
                                    "date" to "2026-06-19",
                                    "status" to "Present",
                                    "remarks" to "On time"
                                ),
                                mapOf(
                                    "id" to "rec_att_02",
                                    "student_email" to "manishdutta31@gmail.com",
                                    "date" to "2026-06-19",
                                    "status" to "Present",
                                    "remarks" to "On time"
                                ),
                                mapOf(
                                    "id" to "rec_att_03",
                                    "student_email" to "govindmallik@gmail.com",
                                    "date" to "2026-06-19",
                                    "status" to "Absent",
                                    "remarks" to "Sick leave"
                                )
                            )
                            gson.toJson(mapOf("records" to list))
                        }
                        "test_results" -> {
                            val list = listOf(
                                mapOf(
                                    "id" to "rec_res_01",
                                    "student_email" to "sajjankumar@gmail.com",
                                    "exam_date" to "2026-06-18",
                                    "subject" to "Physics",
                                    "marks_obtained" to 85.0,
                                    "max_marks" to 100.0
                                ),
                                mapOf(
                                    "id" to "rec_res_02",
                                    "student_email" to "manishdutta31@gmail.com",
                                    "exam_date" to "2026-06-18",
                                    "subject" to "Physics",
                                    "marks_obtained" to 90.0,
                                    "max_marks" to 100.0
                                ),
                                mapOf(
                                    "id" to "rec_res_03",
                                    "student_email" to "govindmallik@gmail.com",
                                    "exam_date" to "2026-06-18",
                                    "subject" to "Physics",
                                    "marks_obtained" to 75.0,
                                    "max_marks" to 100.0
                                )
                            )
                            gson.toJson(mapOf("records" to list))
                        }
                        else -> { // fees
                            val list = listOf(
                                mapOf(
                                    "id" to "rec_fee_01",
                                    "student_email" to "sajjankumar@gmail.com",
                                    "month" to "June",
                                    "amount_paid" to 1500.0,
                                    "total_due" to 1500.0,
                                    "payment_date" to "2026-06-19",
                                    "status" to "Paid",
                                    "payment_mode" to "UPI",
                                    "transaction_reference_number" to "TXN987654"
                                ),
                                mapOf(
                                    "id" to "rec_fee_02",
                                    "student_email" to "manishdutta31@gmail.com",
                                    "month" to "June",
                                    "amount_paid" to 0.0,
                                    "total_due" to 1500.0,
                                    "payment_date" to "N/A",
                                    "status" to "Unpaid",
                                    "payment_mode" to "Cash",
                                    "transaction_reference_number" to null
                                )
                            )
                            gson.toJson(mapOf("records" to list))
                        }
                    }
                }
            }
            path.endsWith("api/library/resources") && request.method == "POST" -> {
                """
                {
                  "message": "Digital resource uploaded and indexed successfully!"
                }
                """.trimIndent()
            }
            path.contains("api/library/resources/") && request.method == "DELETE" -> {
                """
                {
                  "message": "Resource deleted completely from library!"
                }
                """.trimIndent()
            }
            path.contains("api/admin/quizzes/") && request.method == "DELETE" -> {
                val quizId = path.substringAfter("api/admin/quizzes/").trim('/')
                synchronized(mockQuizzesList) {
                    mockQuizzesList.removeAll { it.id == quizId }
                }
                """
                {
                  "message": "Quiz deleted successfully."
                }
                """.trimIndent()
            }
            path.contains("api/admin/quizzes/") && path.endsWith("/launch") && request.method == "POST" -> {
                val quizId = path.substringAfter("api/admin/quizzes/").substringBefore("/launch").trim('/')
                synchronized(launchedQuizIds) {
                    launchedQuizIds.add(quizId)
                }
                """
                {
                  "message": "Quiz launched successfully manually."
                }
                """.trimIndent()
            }
            path.endsWith("api/admin/quizzes/launch-multiple") && request.method == "POST" -> {
                try {
                    val jsonMap = gson.fromJson(requestBodyString, Map::class.java)
                    val quizIdsList = jsonMap["quiz_ids"] as? List<*>
                    if (quizIdsList != null) {
                        synchronized(launchedQuizIds) {
                            for (qId in quizIdsList) {
                                (qId as? String)?.let { launchedQuizIds.add(it) }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                """
                {
                  "message": "Multiple quizzes launched successfully."
                }
                """.trimIndent()
            }
            path.endsWith("api/admin/quizzes") -> {
                val now = java.util.Date()
                
                val list = synchronized(mockQuizzesList) {
                    mockQuizzesList.map { q ->
                        val isLaunchedVal = synchronized(launchedQuizIds) { launchedQuizIds.contains(q.id) } || (parseDate(q.scheduledStart)?.let { now.after(it) } ?: false)
                        mapOf(
                            "id" to q.id,
                            "subject" to q.subject,
                            "class" to q.classSelection,
                            "chapter" to q.chapter,
                            "quiz_type" to q.quizType,
                            "scheduled_start" to q.scheduledStart,
                            "is_manual" to q.isManual,
                            "created_at" to q.createdAt,
                            "is_launched" to isLaunchedVal
                        )
                    }
                }
                gson.toJson(mapOf("quizzes" to list))
            }
            path.endsWith("api/admin/quiz/manual") -> {
                try {
                    val jsonMap = gson.fromJson(requestBodyString, Map::class.java)
                    val subject = jsonMap["subject"] as? String ?: ""
                    val classSelection = jsonMap["class"] as? String ?: ""
                    val chapter = jsonMap["chapter"] as? String ?: ""
                    val quizType = jsonMap["quiz_type"] as? String ?: ""
                    val scheduledStart = jsonMap["scheduled_start"] as? String ?: ""
                    val questionsArray = jsonMap["questions"] as? List<*>
                    
                    val quizId = "quiz_man_${System.currentTimeMillis()}"
                    val questionsList = mutableListOf<QuestionResponse>()
                    if (questionsArray != null) {
                        for (i in questionsArray.indices) {
                            val qMap = questionsArray[i] as? Map<*, *> ?: continue
                            questionsList.add(
                                QuestionResponse(
                                    id = "q_man_${quizId}_$i",
                                    quizId = quizId,
                                    questionText = qMap["question_text"] as? String ?: "",
                                    optionA = qMap["option_a"] as? String ?: "",
                                    optionB = qMap["option_b"] as? String ?: "",
                                    optionC = qMap["option_c"] as? String ?: "",
                                    optionD = qMap["option_d"] as? String ?: "",
                                    correctOption = qMap["correct_option"] as? String ?: "",
                                    explanation = qMap["explanation"] as? String ?: ""
                                ).shuffled()
                            )
                        }
                    }
                    
                    val newQuiz = MockQuiz(
                        id = quizId,
                        subject = subject,
                        classSelection = classSelection,
                        chapter = chapter,
                        quizType = quizType,
                        scheduledStart = scheduledStart,
                        questions = questionsList,
                        isManual = 1,
                        createdAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }.format(java.util.Date())
                    )
                    
                    synchronized(mockQuizzesList) {
                        mockQuizzesList.add(newQuiz)
                    }
                    
                    gson.toJson(
                        mapOf(
                            "message" to "Manual quiz scheduled and added successfully!",
                            "quizId" to quizId
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    gson.toJson(mapOf("message" to "Error scheduling manual quiz: ${e.localizedMessage}"))
                }
            }
            path.endsWith("api/admin/quiz/generate-ai") -> {
                try {
                    val jsonMap = gson.fromJson(requestBodyString, Map::class.java)
                    val subject = jsonMap["subject"] as? String ?: ""
                    val classSelection = jsonMap["class"] as? String ?: ""
                    val chapter = jsonMap["chapter"] as? String ?: ""
                    val quizType = jsonMap["quiz_type"] as? String ?: ""
                    val scheduledStart = jsonMap["scheduled_start"] as? String ?: ""
                    
                    val quizId = "quiz_ai_${System.currentTimeMillis()}"
                    
                    // Retrieve 5 questions from QuestionBank as fallback
                    val entities = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions(classSelection, subject, 5)
                    val questionsList = if (entities.isNotEmpty()) {
                        entities.mapIndexed { idx, q ->
                            val correctOptStr = when (q.correctAnswer) {
                                0 -> "A"
                                1 -> "B"
                                2 -> "C"
                                3 -> "D"
                                else -> "A"
                            }
                            QuestionResponse(
                                id = "q_ai_${quizId}_$idx",
                                quizId = quizId,
                                questionText = q.questionText,
                                optionA = q.optionA,
                                optionB = q.optionB,
                                optionC = q.optionC,
                                optionD = q.optionD,
                                correctOption = correctOptStr,
                                explanation = q.explanation
                            ).shuffled()
                        }
                    } else {
                        // Return default question if bank is empty
                        listOf(
                            QuestionResponse(
                                id = "q_ai_${quizId}_0",
                                quizId = quizId,
                                questionText = "What is the capital of India?",
                                optionA = "New Delhi",
                                optionB = "Mumbai",
                                optionC = "Kolkata",
                                optionD = "Chennai",
                                correctOption = "A",
                                explanation = "New Delhi is the capital city of India."
                            ).shuffled()
                        )
                    }
                    
                    val newQuiz = MockQuiz(
                        id = quizId,
                        subject = subject,
                        classSelection = classSelection,
                        chapter = chapter,
                        quizType = quizType,
                        scheduledStart = scheduledStart,
                        questions = questionsList,
                        isManual = 0,
                        createdAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }.format(java.util.Date())
                    )
                    
                    synchronized(mockQuizzesList) {
                        mockQuizzesList.add(newQuiz)
                    }
                    
                    gson.toJson(
                        mapOf(
                            "message" to "AI quiz generated and scheduled successfully!",
                            "quizId" to quizId
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    gson.toJson(mapOf("message" to "Error generating AI quiz: ${e.localizedMessage}"))
                }
            }
            path.endsWith("api/admin/pending-students") -> {
                val list = synchronized(pendingStudentsList) {
                    pendingStudentsList.map { student ->
                        mapOf(
                            "id" to student.id,
                            "name" to student.name,
                            "email" to student.email,
                            "mobile" to student.mobile,
                            "class" to student.classSelection,
                            "status" to student.status,
                            "student_id" to student.studentId,
                            "rejected_by" to student.rejectedBy,
                            "rejected_date" to student.rejectedDate,
                            "created_at" to student.createdAt,
                            "is_resignup" to student.isResignup
                        )
                    }
                }
                gson.toJson(mapOf("students" to list))
            }
            path.contains("api/admin/approve-student/") -> {
                val studentId = path.substringAfter("api/admin/approve-student/").trim('/')
                synchronized(pendingStudentsList) {
                    val index = pendingStudentsList.indexOfFirst { it.id == studentId }
                    if (index != -1) {
                        val s = pendingStudentsList[index]
                        pendingStudentsList[index] = s.copy(status = "approved")
                        
                        synchronized(mockApprovedStudentsList) {
                            if (mockApprovedStudentsList.none { it.id == s.id }) {
                                mockApprovedStudentsList.add(
                                    AdminStudentInfo(
                                        id = s.id,
                                        name = s.name,
                                        email = s.email,
                                        classSelection = s.classSelection,
                                        status = "approved",
                                        monthlyFee = 1500.0,
                                        mobile = s.mobile
                                    )
                                )
                            }
                        }
                    }
                }
                """
                {
                  "message": "Student approved successfully!"
                }
                """.trimIndent()
            }
            path.contains("api/admin/reject-student/") -> {
                val studentId = path.substringAfter("api/admin/reject-student/").trim('/')
                synchronized(pendingStudentsList) {
                    val index = pendingStudentsList.indexOfFirst { it.id == studentId }
                    if (index != -1) {
                        val s = pendingStudentsList[index]
                        pendingStudentsList[index] = s.copy(
                            status = "rejected",
                            rejectedBy = "Sajjan Mallik",
                            rejectedDate = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }.format(java.util.Date())
                        )
                    }
                }
                """
                {
                  "message": "Student registration request rejected."
                }
                """.trimIndent()
            }
            path.contains("api/admin/students/") && path.endsWith("/status") && request.method == "PUT" -> {
                val studentId = path.substringAfter("api/admin/students/").substringBefore("/status").trim('/')
                try {
                    val jsonMap = gson.fromJson(requestBodyString, Map::class.java)
                    val status = jsonMap["status"] as? String ?: ""
                    synchronized(mockApprovedStudentsList) {
                        val index = mockApprovedStudentsList.indexOfFirst { it.id == studentId }
                        if (index != -1) {
                            val s = mockApprovedStudentsList[index]
                            mockApprovedStudentsList[index] = s.copy(status = status)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                """
                {
                  "message": "Student status updated successfully."
                }
                """.trimIndent()
            }
            path.contains("api/admin/students/") && request.method == "PUT" -> {
                val studentId = path.substringAfter("api/admin/students/").trim('/')
                try {
                    val jsonMap = gson.fromJson(requestBodyString, Map::class.java)
                    val name = jsonMap["name"] as? String ?: ""
                    val classSelection = jsonMap["class"] as? String ?: ""
                    val mobile = jsonMap["mobile"] as? String ?: ""
                    synchronized(mockApprovedStudentsList) {
                        val index = mockApprovedStudentsList.indexOfFirst { it.id == studentId }
                        if (index != -1) {
                            val s = mockApprovedStudentsList[index]
                            mockApprovedStudentsList[index] = s.copy(
                                name = if (name.isNotEmpty()) name else s.name,
                                classSelection = if (classSelection.isNotEmpty()) classSelection else s.classSelection,
                                mobile = if (mobile.isNotEmpty()) mobile else s.mobile
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                """
                {
                  "message": "Student record updated successfully."
                }
                """.trimIndent()
            }
            path.contains("api/admin/students/") && request.method == "DELETE" -> {
                val studentId = path.substringAfter("api/admin/students/").trim('/')
                synchronized(pendingStudentsList) {
                    pendingStudentsList.removeAll { it.id == studentId }
                }
                synchronized(mockApprovedStudentsList) {
                    mockApprovedStudentsList.removeAll { it.id == studentId }
                }
                """
                {
                  "message": "Student record processed successfully."
                }
                """.trimIndent()
            }
            path.endsWith("api/payments/initialize") -> {
                """
                {
                  "txnRefId": "SP-MOC-17482937"
                }
                """.trimIndent()
            }
            path.endsWith("api/payments/verify") -> {
                """
                {
                  "message": "UTR reference number verified and approved successfully!"
                }
                """.trimIndent()
            }
            path.endsWith("api/enquiries") || path.endsWith("api/admin/enquiries") -> {
                val list = enquiriesList.map { enq ->
                    mapOf(
                        "id" to enq.id,
                        "name" to enq.name,
                        "email" to enq.email,
                        "mobile" to enq.mobile,
                        "class" to enq.classSelection,
                        "subject" to enq.subject,
                        "message" to enq.message,
                        "status" to enq.status,
                        "remarks" to enq.remarks,
                        "created_at" to enq.createdAt
                    )
                }
                gson.toJson(mapOf("enquiries" to list))
            }
            (path.contains("api/enquiries/") || path.contains("api/admin/enquiries/")) && request.method == "PUT" -> {
                val id = if (path.contains("api/enquiries/")) {
                    path.substringAfter("api/enquiries/").substringBefore("/status").substringBefore("?").trim('/')
                } else {
                    path.substringAfter("api/admin/enquiries/").substringBefore("?").trim('/')
                }
                val jsonMap = try {
                    gson.fromJson(requestBodyString, Map::class.java)
                } catch (e: Exception) {
                    emptyMap<String, Any>()
                }
                val index = enquiriesList.indexOfFirst { it.id == id }
                if (index != -1) {
                    val existing = enquiriesList[index]
                    enquiriesList[index] = existing.copy(
                        name = jsonMap["name"] as? String ?: existing.name,
                        email = jsonMap["email"] as? String ?: existing.email,
                        mobile = jsonMap["mobile"] as? String ?: existing.mobile,
                        classSelection = jsonMap["classSelection"] as? String ?: jsonMap["class"] as? String ?: existing.classSelection,
                        subject = if (jsonMap.containsKey("subject")) jsonMap["subject"] as? String ?: "" else existing.subject,
                        message = jsonMap["message"] as? String ?: existing.message,
                        status = jsonMap["status"] as? String ?: existing.status,
                        remarks = if (jsonMap.containsKey("remarks")) jsonMap["remarks"] as? String else existing.remarks
                    )
                    """
                    {
                        "message": "Enquiry updated successfully!"
                    }
                    """.trimIndent()
                } else {
                    return Response.Builder()
                        .code(404)
                        .message("Not Found")
                        .request(request)
                        .protocol(Protocol.HTTP_2)
                        .body("{\"message\":\"Enquiry not found\"}".toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
                        .build()
                }
            }
            (path.contains("api/enquiries/") || path.contains("api/admin/enquiries/")) && request.method == "DELETE" -> {
                val id = if (path.contains("api/enquiries/")) {
                    path.substringAfter("api/enquiries/").substringBefore("?").trim('/')
                } else {
                    path.substringAfter("api/admin/enquiries/").substringBefore("?").trim('/')
                }
                val removed = enquiriesList.removeIf { it.id == id }
                if (removed) {
                    """
                    {
                        "message": "Enquiry deleted successfully!"
                    }
                    """.trimIndent()
                } else {
                    return Response.Builder()
                        .code(404)
                        .message("Not Found")
                        .request(request)
                        .protocol(Protocol.HTTP_2)
                        .body("{\"message\":\"Enquiry not found\"}".toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
                        .build()
                }
            }
            path.contains("api/student/homework") || path.contains("api/admin/homework") -> {
                when (request.method) {
                    "POST" -> "{\"message\": \"Homework published successfully!\", \"homeworkId\": 101}"
                    "PUT" -> "{\"message\": \"Homework updated successfully!\"}"
                    "DELETE" -> "{\"message\": \"Homework deleted successfully!\"}"
                    else -> "{\"studentClass\": \"Class 10\", \"homework\": []}"
                }
            }
            path.contains("api/notifications") -> {
                when (request.method) {
                    "POST" -> "{\"message\": \"Notification broadcasted successfully!\"}"
                    "PUT" -> "{\"message\": \"Notification updated successfully!\"}"
                    "DELETE" -> "{\"message\": \"Notification deleted successfully!\"}"
                    else -> "{\"notifications\": [], \"unreadCount\": 0}"
                }
            }
            path.contains("api/student/quiz/practice") -> {
                """
                {
                    "quiz": {
                        "id": "quiz_prac_1",
                        "subject": "Mathematics",
                        "chapter": "Core Practice",
                        "class": "Class 10",
                        "quiz_type": "practice",
                        "scheduled_start": "practice"
                    },
                    "questions": [],
                    "attempted": false
                }
                """.trimIndent()
            }
            path.contains("api/student/quiz/history") -> {
                "{\"history\": []}"
            }
            path.contains("api/admin/quiz/refresh-fallback-bank") -> {
                "{\"message\": \"Fallback bank refreshed successfully!\"}"
            }
            path.contains("api/admin/quiz/fallback-bank-stats") -> {
                "{\"total_questions\": 100, \"classes\": []}"
            }
            else -> {
                throw IOException("Mock response not defined for path: $path")
            }
        }

        return Response.Builder()
            .code(200)
            .message("OK")
            .request(chain.request())
            .protocol(Protocol.HTTP_2)
            .body(responseString.toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }
}
