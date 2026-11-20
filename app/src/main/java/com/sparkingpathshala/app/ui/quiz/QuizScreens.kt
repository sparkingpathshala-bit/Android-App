package com.sparkingpathshala.app.ui.quiz

import android.widget.Toast
import androidx.core.content.edit
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.local.QuestionEntity
import com.sparkingpathshala.app.data.local.QuizEntity
import com.sparkingpathshala.app.data.local.isAdmin
import com.sparkingpathshala.app.data.local.isGuest
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    viewModel: DashboardViewModel,
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToPlayQuiz: (String) -> Unit,
    onNavigateToAdminQuizzes: () -> Unit = {},
    onBack: () -> Unit
) {
    val currentUser by viewModel.user.collectAsState()
    val isGuest = currentUser.isGuest()
    val isAdmin = currentUser.isAdmin()

    if (isAdmin) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Quiz Arena", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryBlue,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Admin Mode: Quiz Arena",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Quiz participation and scoring is strictly designed for Enrolled Students and Guest Learners.\n\nAs an Administrator, you can create, schedule, AI-generate, and manage quizzes in the Quiz Arena Builder.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onNavigateToAdminQuizzes,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Quiz Arena Builder", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back to Dashboard", color = Color.Gray)
                        }
                    }
                }
            }
        }
        return
    }

    var guestSelectedClass by remember { mutableStateOf("Class 10") }
    val enrolledClass = if (isGuest) guestSelectedClass else (currentUser?.classLevel ?: "Class 10")

    LaunchedEffect(Unit) {
        viewModel.syncQuizzes()
    }

    val quizzes by viewModel.quizzes.collectAsState()
    val classQuizzes = remember(quizzes, enrolledClass, isGuest) {
        quizzes.filter { 
            if (isGuest) !it.attempted
            else ((it.classLevel.equals(enrolledClass, ignoreCase = true) || 
                   it.classLevel.equals("All", ignoreCase = true) || 
                   it.classLevel.equals("All Classes", ignoreCase = true)) &&
                  !it.attempted)
        }
    }

    var selectedFilterSubject by remember { mutableStateOf("All") }
    val assignedSubjects = remember(classQuizzes) {
        listOf("All") + classQuizzes.map { it.subject }.distinct().sorted()
    }
    
    val filteredAssignedQuizzes = remember(classQuizzes, selectedFilterSubject) {
        if (selectedFilterSubject == "All") classQuizzes
        else classQuizzes.filter { it.subject == selectedFilterSubject }
    }

    // Practice Quiz configuration state
    val isDark = isSystemInDarkTheme()
    val brandColor = if (isDark) SecondaryGold else PrimaryBlue
    val cardBg = if (isDark) Color(0xFF251A1A) else Color.White
    val cardBorderColor = if (isDark) SecondaryGold.copy(alpha = 0.3f) else PrimaryBlue.copy(alpha = 0.15f)

    val streams = remember { listOf("All", "PCM", "PCB", "Arts", "Commerce") }
    var selectedStream by remember { mutableStateOf("All") }

    val practiceSubjects = remember(enrolledClass, selectedStream) {
        val numberPart = enrolledClass.filter { it.isDigit() }.toIntOrNull()
        if (numberPart != null) {
            when {
                numberPart in 1..5 -> {
                    listOf("Math", "Hindi", "English", "EVS", "SST", "Computer", "GK", "Mental Math")
                }
                numberPart in 6..9 -> {
                    listOf("Math", "Hindi", "English", "EVS", "SST", "Computer", "GK", "Hindi Grammar", "English Grammar", "Sanskrit")
                }
                else -> { // Class 10 to 12
                    when (selectedStream) {
                        "All" -> listOf("Physics", "Chemistry", "Math", "Biology", "English", "GK", "Sanskrit", "Geography", "History", "Political Science", "Hindi")
                        "PCM" -> listOf("Physics", "Chemistry", "Math", "English", "GK", "Hindi", "Sanskrit")
                        "PCB" -> listOf("Physics", "Chemistry", "Biology", "English", "GK", "Hindi", "Sanskrit")
                        "Arts" -> listOf("Geography", "History", "Political Science", "English", "Hindi", "Sanskrit", "GK")
                        "Commerce" -> listOf("Math", "English", "GK", "Hindi", "Economics", "Accountancy", "Business Studies")
                        else -> listOf("Physics", "Chemistry", "Math", "Biology", "English", "GK", "Sanskrit", "Geography", "History", "Political Science", "Hindi")
                    }
                }
            }
        } else {
            if (enrolledClass.contains("JEE", ignoreCase = true) || enrolledClass.contains("NEET", ignoreCase = true)) {
                listOf("Physics", "Chemistry", "Math", "Biology", "English")
            } else {
                listOf("Math", "Hindi", "English", "EVS", "SST", "Computer", "GK", "Mental Math")
            }
        }
    }
    var selectedPracticeSubject by remember(practiceSubjects) { mutableStateOf(practiceSubjects.firstOrNull() ?: "Math") }
    var expandedSubjectDropdown by remember { mutableStateOf(false) }
    var selectedQuestionCount by remember { mutableIntStateOf(5) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Arena", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { scaffoldPadding ->
        val effectiveBottomPadding = maxOf(
            scaffoldPadding.calculateBottomPadding(),
            paddingValues.calculateBottomPadding()
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    top = scaffoldPadding.calculateTopPadding(),
                    bottom = effectiveBottomPadding
                )
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Personalized Practice Quiz Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SecondaryGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personalized Practice Quiz",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = brandColor
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Test your skills with CBSE/ICSE board-aligned practice questions curated specifically for your class.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isGuest) {
                            var expandedGuestClass by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = guestSelectedClass,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Select Class Level", color = if (isDark) Color.LightGray else Color.Gray) },
                                    trailingIcon = {
                                        IconButton(onClick = { expandedGuestClass = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = brandColor)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandColor,
                                        unfocusedBorderColor = if (isDark) Color.Gray else Color.LightGray,
                                        focusedTextColor = if (isDark) Color.White else Color.Black,
                                        unfocusedTextColor = if (isDark) Color.LightGray else Color.DarkGray
                                    )
                                )
                                DropdownMenu(
                                    expanded = expandedGuestClass,
                                    onDismissRequest = { expandedGuestClass = false }
                                ) {
                                    (1..12).forEach { num ->
                                        val className = "Class $num"
                                        DropdownMenuItem(
                                            text = { Text(className) },
                                            onClick = {
                                                guestSelectedClass = className
                                                expandedGuestClass = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        val numberPart = enrolledClass.filter { it.isDigit() }.toIntOrNull()
                        if (numberPart != null && numberPart >= 10) {
                            Text(
                                text = "Select Stream / Subject Group",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = brandColor,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                streams.forEach { stream ->
                                    val isSelected = selectedStream == stream
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedStream = stream },
                                        label = {
                                            Text(
                                                text = stream,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 12.sp
                                            )
                                        },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = brandColor.copy(alpha = 0.2f),
                                            selectedLabelColor = brandColor,
                                            selectedLeadingIconColor = brandColor,
                                            containerColor = if (isDark) Color(0xFF1E2028) else Color(0xFFF1F5F9),
                                            labelColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                                            selectedBorderColor = brandColor
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Subject Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedPracticeSubject,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Practice Subject", color = if (isDark) Color.LightGray else Color.Gray) },
                                trailingIcon = {
                                    IconButton(onClick = { expandedSubjectDropdown = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = brandColor)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = brandColor,
                                    unfocusedBorderColor = if (isDark) Color.Gray else Color.LightGray,
                                    focusedTextColor = if (isDark) Color.White else Color.Black,
                                    unfocusedTextColor = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            )

                            DropdownMenu(
                                expanded = expandedSubjectDropdown,
                                onDismissRequest = { expandedSubjectDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                practiceSubjects.forEach { subj ->
                                    DropdownMenuItem(
                                        text = { Text(subj) },
                                        onClick = {
                                            selectedPracticeSubject = subj
                                            expandedSubjectDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Question Count Selector
                        Text(
                            text = "Number of Questions",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = brandColor,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(5, 7, 10, 15).forEach { count ->
                                val isSelected = selectedQuestionCount == count
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedQuestionCount = count },
                                    label = {
                                        Text(
                                            text = "$count Qs",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = brandColor.copy(alpha = 0.2f),
                                        selectedLabelColor = brandColor,
                                        containerColor = if (isDark) Color(0xFF1E2028) else Color(0xFFF1F5F9),
                                        labelColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                                        selectedBorderColor = brandColor
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        RealisticButton(
                            onClick = { 
                                onNavigateToPlayQuiz("practice_${enrolledClass}_${selectedPracticeSubject}_$selectedQuestionCount")
                            },
                            containerColor = if (isDark) SecondaryGold else PrimaryBlue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isDark) Color.Black else Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Practice Quiz",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isDark) Color.Black else Color.White
                            )
                        }
                    }
                }
            }

            // Section Title: Teacher Assigned Quizzes
            item {
                Text(
                    text = "Teacher Assigned Quizzes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = brandColor,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Horizontal Tab Chips for assigned quizzes filter
            if (classQuizzes.isNotEmpty()) {
                item {
                    val tabIdx = assignedSubjects.indexOf(selectedFilterSubject).let { if (it == -1) 0 else it }
                    PrimaryScrollableTabRow(
                        selectedTabIndex = tabIdx,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        contentColor = brandColor,
                        divider = {}
                    ) {
                        assignedSubjects.forEach { subject ->
                            Tab(
                                selected = selectedFilterSubject == subject,
                                onClick = { selectedFilterSubject = subject },
                                text = { Text(subject, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }
            }

            if (filteredAssignedQuizzes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No active assigned quizzes at this time.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredAssignedQuizzes, key = { it.id }, contentType = { "assigned_quiz" }) { quiz ->
                    QuizCardItem(quiz = quiz, onStart = { onNavigateToPlayQuiz(quiz.id) })
                }
            }

            item { Spacer(modifier = Modifier.height(36.dp)) }
        }
    }
}

@Composable
fun QuizCardItem(
    quiz: QuizEntity,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = quiz.subject.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "${quiz.numQuestions} Qs",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = quiz.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = quiz.description,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            val isAttempted = quiz.id != "practice_quiz" && quiz.attempted
            RealisticButton(
                onClick = onStart,
                containerColor = if (isAttempted) Color(0xFF2E7D32) else PrimaryBlue,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                Icon(if (isAttempted) Icons.Default.CheckCircle else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isAttempted) "View Results" else "Start Quiz", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

val QuestionListSaver = Saver<List<QuestionEntity>, ArrayList<QuestionEntity>>(
    save = { ArrayList(it) },
    restore = { it }
)

@Composable
fun QuizPlayScreen(
    viewModel: DashboardViewModel,
    quizId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var retryTrigger by rememberSaveable { mutableIntStateOf(0) }
    val questionsFlow = remember(quizId, retryTrigger) { viewModel.getQuestionsForQuiz(context, quizId) }
    val dbQuestions by questionsFlow.collectAsState(initial = emptyList())
    val quizzes by viewModel.quizzes.collectAsState()
    val currentQuiz = remember(quizzes, quizId) { quizzes.find { it.id == quizId } }
    
    var activeQuestions by rememberSaveable(stateSaver = QuestionListSaver) {
        mutableStateOf<List<QuestionEntity>>(emptyList())
    }
    
    val currentUser by viewModel.user.collectAsState()

    val isPractice = quizId == "practice_quiz" || quizId.startsWith("practice_")
    val practiceParts = remember(quizId) {
        if (quizId.startsWith("practice_") && quizId != "practice_quiz") {
            quizId.substringAfter("practice_").split("_")
        } else null
    }
    val fallbackClass = practiceParts?.getOrNull(0) ?: (currentUser?.classLevel ?: "Class 10")
    val fallbackSubject = practiceParts?.getOrNull(1) ?: "Math"
    val fallbackCount = practiceParts?.getOrNull(2)?.toIntOrNull() ?: 5

    val loadOfflineFallback = {
        if (isPractice) {
            val local = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions(fallbackClass, fallbackSubject, fallbackCount)
            if (local.isNotEmpty()) {
                activeQuestions = local
            }
        }
    }

    var showOfflineOption by remember { mutableStateOf(false) }
    LaunchedEffect(activeQuestions, retryTrigger) {
        if (activeQuestions.isEmpty()) {
            kotlinx.coroutines.delay(3000L)
            if (activeQuestions.isEmpty()) {
                showOfflineOption = true
            }
            kotlinx.coroutines.delay(3500L)
            if (activeQuestions.isEmpty()) {
                loadOfflineFallback()
            }
        } else {
            showOfflineOption = false
        }
    }

    LaunchedEffect(dbQuestions, retryTrigger) {
        if (dbQuestions.isNotEmpty() && activeQuestions.isEmpty()) {
            // 1. Deduplicate questions by ID and text to prevent repeats
            val unique = dbQuestions
                .distinctBy { it.id }
                .distinctBy { it.questionText.trim().lowercase() }

            // Extract question count if practice quiz
            val count = if (isPractice) {
                fallbackCount
            } else {
                unique.size
            }

            val isAlreadyAttempted = !isPractice && currentQuiz?.attempted == true
            if (isAlreadyAttempted) {
                activeQuestions = unique.take(count)
            } else {
                val userId = currentUser?.id ?: "guest"
                val prefs = context.getSharedPreferences("sparking_quiz_prefs", android.content.Context.MODE_PRIVATE)
                val seenKey = "seen_questions_${userId}_${quizId}"
                val seenSet = prefs.getStringSet(seenKey, emptySet()) ?: emptySet()

                // 2. Filter out previously seen questions to ensure freshness
                var filtered = unique.filter { it.id !in seenSet }

                // 3. If all seen or fewer than required count, cycle/reset seen history
                if (filtered.size < count) {
                    prefs.edit { remove(seenKey) }
                    filtered = unique
                }

                // 4. Randomly shuffle questions so every quiz session gives fresh variety
                activeQuestions = filtered.shuffled().take(count)
            }
        }
    }

    val questions = activeQuestions
    
    var currentQuestionIndex by rememberSaveable { mutableIntStateOf(0) }
    val userAnswers = rememberSaveable(
        saver = Saver(
            save = { map -> 
                android.os.Bundle().apply {
                    map.forEach { (k, v) -> putInt(k, v) }
                }
            },
            restore = { bundle -> 
                mutableStateMapOf<String, Int>().apply {
                    bundle.keySet().forEach { k -> put(k, bundle.getInt(k)) }
                }
            }
        )
    ) { mutableStateMapOf<String, Int>() }
    
    var isSubmitting by rememberSaveable { mutableStateOf(false) }
    var showResults by rememberSaveable(currentQuiz) { 
        mutableStateOf(currentQuiz?.attempted == true) 
    }
    var isReviewMode by rememberSaveable { mutableStateOf(false) }
    var finalScore by rememberSaveable(currentQuiz) { 
        mutableIntStateOf(currentQuiz?.score ?: 0) 
    }
    var finalTotalQuestions by rememberSaveable(currentQuiz) { 
        mutableIntStateOf(currentQuiz?.totalQuestions ?: currentQuiz?.numQuestions ?: 0) 
    }
    
    var showExitConfirmation by remember { mutableStateOf(false) }
    var showSubmitConfirmation by remember { mutableStateOf(false) }

    // Intercept system back button
    BackHandler(enabled = !showResults) {
        showExitConfirmation = true
    }

    val performQuizSubmission: () -> Unit = {
        if (!isSubmitting) {
            isSubmitting = true
            val payload = userAnswers.mapValues { entry ->
                ('A'.code + entry.value).toChar().toString()
            }
            viewModel.submitQuiz(quizId, payload) { result ->
                isSubmitting = false
                result.onSuccess { response ->
                    finalScore = response.score
                    finalTotalQuestions = response.totalQuestions

                    val gradedEntities = response.questions.map { res ->
                        QuestionEntity(
                            id = res.id,
                            quizId = res.quizId,
                            questionText = res.questionText,
                            optionA = res.optionA,
                            optionB = res.optionB,
                            optionC = res.optionC,
                            optionD = res.optionD,
                            correctAnswer = when (res.correctOption?.uppercase()) {
                                "A" -> 0
                                "B" -> 1
                                "C" -> 2
                                "D" -> 3
                                else -> 0
                            },
                            explanation = res.explanation ?: "N/A"
                        )
                    }
                    activeQuestions = activeQuestions.map { current ->
                        val graded = gradedEntities.find { it.id == current.id }
                        if (graded != null && graded.explanation.isNotBlank() && graded.explanation != "N/A") {
                            current.copy(explanation = graded.explanation)
                        } else {
                            current
                        }
                    }

                    showResults = true

                    val userId = currentUser?.id ?: "guest"
                    val prefs = context.getSharedPreferences("sparking_quiz_prefs", android.content.Context.MODE_PRIVATE)
                    val seenKey = "seen_questions_${userId}_${quizId}"
                    val seenSet = prefs.getStringSet(seenKey, emptySet())?.toMutableSet() ?: mutableSetOf()
                    seenSet.addAll(activeQuestions.map { it.id })
                    prefs.edit { putStringSet(seenKey, seenSet) }
                }.onFailure { err ->
                    val msg = if (err is retrofit2.HttpException) {
                        try {
                            val errorBody = err.response()?.errorBody()?.string()
                            val json = org.json.JSONObject(errorBody ?: "")
                            json.optString("error", "HTTP ${err.code()} Error")
                        } catch (e: Exception) {
                            err.localizedMessage ?: "Unknown HTTP Error"
                        }
                    } else {
                        err.localizedMessage ?: "Submission failed"
                    }
                    Toast.makeText(context, "Submission failed: $msg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    if (showSubmitConfirmation) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmation = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏁", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Quiz for Grading?", fontWeight = FontWeight.Bold, color = BrandRuby)
                }
            },
            text = {
                Column {
                    Text(
                        text = "You have answered ${userAnswers.size} of ${questions.size} questions.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (userAnswers.size < questions.size) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️ You have ${questions.size - userAnswers.size} unanswered questions. Unanswered questions will receive 0 marks.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Submit now to view your score breakdown, solutions, and earn streak XP!",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmation = false
                        performQuizSubmission()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandRuby),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit Now", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmation = false }) {
                    Text("Review Questions", color = Color.Gray)
                }
            }
        )
    }

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("Exit Quiz?", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
            text = { Text("Are you sure you want to exit? Your answers and quiz progress will be lost.") },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmation = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Exit", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmation = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    if (questions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = SecondaryGold,
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 3.5.dp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Curating Your Quiz",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aligning questions with official CBSE NCERT curriculum...",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    if (showOfflineOption) {
                        Spacer(modifier = Modifier.height(24.dp))
                        RealisticButton(
                            onClick = { loadOfflineFallback() },
                            containerColor = PrimaryBlue,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Start Immediately (Offline Bank)", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(onClick = onBack) {
                            Text("Go Back", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    } else if (isSubmitting) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = SecondaryGold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Submitting your answers for grading...", fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }
        }
    } else if (showResults) {
        if (isReviewMode) {
            QuizReviewContent(
                questions = questions,
                userAnswers = userAnswers,
                onBackToSummary = { isReviewMode = false }
            )
        } else {
            QuizSummaryScreen(
                score = finalScore,
                totalQuestions = finalTotalQuestions,
                isPractice = quizId == "practice_quiz" || quizId.startsWith("practice_"),
                onRetry = {
                    currentQuestionIndex = 0
                    userAnswers.clear()
                    isSubmitting = false
                    showResults = false
                    isReviewMode = false
                    finalScore = 0
                    finalTotalQuestions = 0
                    activeQuestions = emptyList()
                    retryTrigger++
                },
                onReview = {
                    isReviewMode = true
                },
                onHome = onBack
            )
        }
    } else {
        val safeIndex = currentQuestionIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0))
        val currentQuestion = questions[safeIndex]
        val selectedOption = userAnswers[currentQuestion.id]

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                if (questions.isNotEmpty()) "Question ${currentQuestionIndex + 1} of ${questions.size}" else "Quiz Arena",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text("॥ इतः सम्भवति ॥ · ${currentQuiz?.title ?: "Speed Arena"}", fontSize = 11.sp, color = FlameGold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (!showResults) {
                                showExitConfirmation = true
                            } else {
                                onBack()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = FlameGold,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable(enabled = !isSubmitting) {
                                    showSubmitConfirmation = true
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Submit", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("(${userAnswers.size}/${questions.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black.copy(alpha = 0.8f))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BrandRuby,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress tracker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentQuiz?.subject ?: currentQuiz?.title ?: "MCQ Practice Arena",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandRuby
                    )
                    Text(
                        text = "${userAnswers.size} Answered",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { if (questions.isNotEmpty()) ((currentQuestionIndex + 1).toFloat() / questions.size).coerceIn(0f, 1f) else 0f },
                    color = FlameGold,
                    trackColor = Color.LightGray.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Question Navigator Palette
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    questions.forEachIndexed { idx, q ->
                        val isAnswered = userAnswers.containsKey(q.id)
                        val isCurrent = idx == currentQuestionIndex
                        Surface(
                            shape = CircleShape,
                            color = when {
                                isCurrent -> FlameGold
                                isAnswered -> BrandRuby
                                else -> Color.LightGray.copy(alpha = 0.35f)
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { currentQuestionIndex = idx }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = when {
                                        isCurrent -> Color.Black
                                        isAnswered -> Color.White
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = currentQuestion.questionText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(18.dp),
                        textAlign = TextAlign.Start,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Option Cards
                val options = listOf(
                    currentQuestion.optionA,
                    currentQuestion.optionB,
                    currentQuestion.optionC,
                    currentQuestion.optionD
                )

                options.forEachIndexed { index, optionText ->
                    val isSelected = index == selectedOption
                    
                    val cardColor = if (isSelected) Color(0xFFFFF1F2) else MaterialTheme.colorScheme.surface
                    val borderColor = if (isSelected) BrandRuby else Color.LightGray.copy(alpha = 0.5f)
                    val labelColor = if (isSelected) BrandRuby else MaterialTheme.colorScheme.onSurface

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = selectedOption == null) {
                                userAnswers[currentQuestion.id] = index
                            },
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular tag: A, B, C, D
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        color = if (isSelected) labelColor.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A'.code + index).toChar().toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = labelColor,
                                    fontSize = 12.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = optionText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = labelColor
                            )

                            if (isSelected) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = BrandRuby,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation Controls (Previous & Next)
                val isLastQuestion = currentQuestionIndex == questions.size - 1

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentQuestionIndex > 0) {
                        RealisticOutlinedButton(
                            onClick = { currentQuestionIndex-- },
                            strokeColor = BrandRuby,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = BrandRuby)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous", fontWeight = FontWeight.Bold, color = BrandRuby)
                        }
                    }

                    if (!isLastQuestion) {
                        RealisticButton(
                            onClick = { currentQuestionIndex++ },
                            containerColor = BrandRuby,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("Next Question", fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Explicit, Unmissable Submit Quiz Button
                RealisticButton(
                    onClick = { showSubmitConfirmation = true },
                    enabled = !isSubmitting,
                    containerColor = FlameGold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLastQuestion) "🏁 Submit Quiz for Grading" else "🏁 Finish & Submit Quiz (${userAnswers.size}/${questions.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizReviewContent(
    questions: List<QuestionEntity>,
    userAnswers: Map<String, Int>,
    onBackToSummary: () -> Unit
) {
    if (questions.isEmpty()) {
        onBackToSummary()
        return
    }
    var reviewIndex by rememberSaveable { mutableIntStateOf(0) }
    val safeReviewIndex = reviewIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0))
    val currentQuestion = questions[safeReviewIndex]
    val userOption = userAnswers[currentQuestion.id]
    val correctOption = currentQuestion.correctAnswer

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Quiz Answers", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackToSummary) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress tracker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reviewing ${reviewIndex + 1} of ${questions.size}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { if (questions.isNotEmpty()) ((reviewIndex + 1).toFloat() / questions.size).coerceIn(0f, 1f) else 0f },
                color = SecondaryGold,
                trackColor = Color.LightGray.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = currentQuestion.questionText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Option Cards
            val options = listOf(
                currentQuestion.optionA,
                currentQuestion.optionB,
                currentQuestion.optionC,
                currentQuestion.optionD
            )

            options.forEachIndexed { index, optionText ->
                val isCorrect = index == correctOption
                val isSelected = index == userOption
                
                val cardColor = when {
                    isCorrect -> Color(0xFFE8F5E9) // correct green
                    isSelected -> Color(0xFFFFEBEE) // selected incorrect red
                    else -> MaterialTheme.colorScheme.surface
                }

                val borderColor = when {
                    isCorrect -> Color(0xFF4CAF50)
                    isSelected -> Color(0xFFF44336)
                    else -> Color.LightGray.copy(alpha = 0.5f)
                }

                val labelColor = when {
                    isCorrect -> Color(0xFF2E7D32)
                    isSelected -> Color(0xFFC62828)
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular tag: A, B, C, D
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = if (isCorrect || isSelected) labelColor.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                    text = ('A'.code + index).toChar().toString(),
                                fontWeight = FontWeight.Bold,
                                color = labelColor,
                                fontSize = 12.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = optionText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = labelColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Explanation Card
            val correctOptChar = ('A'.code + currentQuestion.correctAnswer.coerceIn(0, 3)).toChar()
            val correctOptionText = when (currentQuestion.correctAnswer) {
                0 -> currentQuestion.optionA
                1 -> currentQuestion.optionB
                2 -> currentQuestion.optionC
                3 -> currentQuestion.optionD
                else -> currentQuestion.optionA
            }
            val displayExplanation = buildString {
                append("Correct Answer: Option ($correctOptChar) \"$correctOptionText\"\n\n")
                val cleanExpl = currentQuestion.explanation.trim()
                if (cleanExpl.isNotBlank() &&
                    !cleanExpl.contains("Open Trivia", ignoreCase = true) &&
                    !cleanExpl.equals("N/A", ignoreCase = true) &&
                    !cleanExpl.contains("Category:", ignoreCase = true) &&
                    !cleanExpl.startsWith("Correct Answer:", ignoreCase = true) &&
                    !cleanExpl.startsWith("Option (", ignoreCase = true)
                ) {
                    append(cleanExpl)
                } else if (cleanExpl.startsWith("Option (", ignoreCase = true)) {
                    append(cleanExpl)
                } else {
                    append("This is the verified CBSE curriculum answer.")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Correct Answer & Explanation:", fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = displayExplanation,
                        fontSize = 13.sp,
                        color = Color(0xFF3E2723),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (reviewIndex > 0) reviewIndex--
                    },
                    enabled = reviewIndex > 0,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Previous")
                }
                
                Button(
                    onClick = {
                        if (reviewIndex + 1 < questions.size) {
                            reviewIndex++
                        } else {
                            onBackToSummary()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (reviewIndex + 1 < questions.size) "Next" else "Finish Review")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun QuizSummaryScreen(
    score: Int,
    totalQuestions: Int,
    isPractice: Boolean = false,
    onRetry: () -> Unit,
    onReview: () -> Unit,
    onHome: () -> Unit
) {
    val percentage = if (totalQuestions > 0) (score.toFloat() / totalQuestions * 100).toInt() else 0
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(PrimaryBlue, Color(0xFF3A0507))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = SecondaryGold,
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quiz Completed!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                Text(
                    text = when {
                        percentage >= 80 -> "Outstanding performance! Keep it up."
                        percentage >= 50 -> "Good effort! Practice more to perfect it."
                        else -> "Keep learning and try again!"
                    },
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Score Circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { percentage / 100f },
                        color = SecondaryGold,
                        strokeWidth = 8.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$score / $totalQuestions",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "$percentage%",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onReview,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Review Explanations", fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (isPractice) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play Again", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedButton(
                    onClick = onHome,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back to Dashboard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
