package com.sparkingpathshala.app.ui.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.ui.theme.RealisticButton
import kotlinx.coroutines.launch
import com.sparkingpathshala.app.data.remote.AdminQuizInfo
import com.sparkingpathshala.app.data.remote.AiQuizRequest
import com.sparkingpathshala.app.data.remote.ManualQuestion
import com.sparkingpathshala.app.data.remote.ManualQuizRequest
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.SecondaryGold

fun isQuizActive(scheduledStart: String): Boolean {
    if (scheduledStart.isBlank()) return true
    return com.sparkingpathshala.app.utils.DateUtils.isDatePassed(scheduledStart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuizzesScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var quizzes by remember { mutableStateOf<List<AdminQuizInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Active creator mode: "manual" or "ai"
    var creatorMode by remember { mutableStateOf("manual") }

    // Form inputs
    var subject by remember { mutableStateOf("Mathematics") }
    var classSelection by remember { mutableStateOf("Class 10") }
    var chapterName by remember { mutableStateOf("") }
    var quizType by remember { mutableStateOf("30 Min Quiz") }
    val defaultDate = remember {
        try {
            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getDefault()
            }.format(java.util.Date())
        } catch (e: Exception) {
            "20/06/2026"
        }
    }
    val defaultTime = remember {
        try {
            val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getDefault())
            val minutes = calendar.get(java.util.Calendar.MINUTE)
            if (minutes < 30) {
                calendar.set(java.util.Calendar.MINUTE, 30)
            } else {
                calendar.add(java.util.Calendar.HOUR_OF_DAY, 1)
                calendar.set(java.util.Calendar.MINUTE, 0)
            }
            val hourVal = calendar.get(java.util.Calendar.HOUR)
            val h = String.format(java.util.Locale.US, "%02d", if (hourVal == 0) 12 else hourVal)
            val m = String.format(java.util.Locale.US, "%02d", calendar.get(java.util.Calendar.MINUTE))
            val ampm = if (calendar.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM) "AM" else "PM"
            Triple(h, m, ampm)
        } catch (e: Exception) {
            Triple("10", "30", "AM")
        }
    }
    var scheduledDate by remember { mutableStateOf(defaultDate) }
    var scheduledHour by remember { mutableStateOf(defaultTime.first) }
    var scheduledMinute by remember { mutableStateOf(defaultTime.second) }
    var scheduledAmPm by remember { mutableStateOf(defaultTime.third) }

    val questionsList = remember { mutableStateListOf<ManualQuestion>() }

    // Form fields for adding new question
    var qText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctOpt by remember { mutableStateOf("A") }
    var explanation by remember { mutableStateOf("") }

    val subjects = com.sparkingpathshala.app.utils.AppConstants.ALL_SUBJECTS
    val classes = com.sparkingpathshala.app.utils.AppConstants.ALL_CLASSES
    val quizTypes = listOf("30 Min Quiz", "Weekly Quiz")
    val selectedQuizIds = remember { mutableStateListOf<String>() }

    val datePickerDialog = remember {
        val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata"))
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                scheduledDate = String.format(java.util.Locale.US, "%02d/%02d/%04d", dayOfMonth, month + 1, year)
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }


    val hoursOptions = remember { (1..12).map { String.format(java.util.Locale.US, "%02d", it) } }
    val minutesOptions = remember { listOf("00", "30") }
    val amPmOptions = remember { listOf("AM", "PM") }

    var expandedSubject by remember { mutableStateOf(false) }
    var expandedClass by remember { mutableStateOf(false) }
    var expandedQuizType by remember { mutableStateOf(false) }
    var expandedHour by remember { mutableStateOf(false) }
    var expandedMinute by remember { mutableStateOf(false) }
    var expandedAmPm by remember { mutableStateOf(false) }

    fun loadQuizzes() {
        isLoading = true
        selectedQuizIds.clear()
        viewModel.getAdminQuizzes { result ->
            quizzes = result.getOrElse { emptyList() }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadQuizzes()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Quiz Arena Builder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F0F12),
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
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Creator Panel Item
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Toggles row: Create Manually vs Generate with AI
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { creatorMode = "manual" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (creatorMode == "manual") MaterialTheme.colorScheme.primary else Color.Transparent,
                                    contentColor = if (creatorMode == "manual") MaterialTheme.colorScheme.onPrimary else Color.Gray
                                ),
                                shape = RoundedCornerShape(20.dp),
                                border = if (creatorMode == "manual") null else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                                modifier = Modifier.padding(end = 8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Create Manually", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { creatorMode = "ai" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (creatorMode == "ai") MaterialTheme.colorScheme.primary else Color.Transparent,
                                    contentColor = if (creatorMode == "ai") MaterialTheme.colorScheme.onPrimary else Color.Gray
                                ),
                                shape = RoundedCornerShape(20.dp),
                                border = if (creatorMode == "ai") null else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Generate with AI 🤖", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Section Title
                        Text(
                            text = if (creatorMode == "manual") "Manual Quiz Creator" else "AI Quiz Generator",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryGold
                        )

                        // Subject & Class row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Subject", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = subject,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold) },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledContainerColor = Color(0xFF16161B),
                                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { expandedSubject = true })
                                    DropdownMenu(
                                        expanded = expandedSubject,
                                        onDismissRequest = { expandedSubject = false }
                                    ) {
                                        subjects.forEach { sub ->
                                            DropdownMenuItem(text = { Text(sub) }, onClick = { subject = sub; expandedSubject = false })
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Class", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = classSelection,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold) },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledContainerColor = Color(0xFF16161B),
                                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { expandedClass = true })
                                    DropdownMenu(
                                        expanded = expandedClass,
                                        onDismissRequest = { expandedClass = false }
                                    ) {
                                        classes.forEach { cls ->
                                            DropdownMenuItem(text = { Text(cls) }, onClick = { classSelection = cls; expandedClass = false })
                                        }
                                    }
                                }
                            }
                        }

                        // Chapter Name & Quiz Type row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Chapter / Unit Name", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = chapterName,
                                    onValueChange = { chapterName = it },
                                    placeholder = { Text("e.g. Quadratic Equations", color = Color.Gray, fontSize = 13.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF16161B),
                                        unfocusedContainerColor = Color(0xFF16161B),
                                        focusedBorderColor = SecondaryGold,
                                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Quiz Type", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = quizType,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold) },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledContainerColor = Color(0xFF16161B),
                                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { expandedQuizType = true })
                                    DropdownMenu(
                                        expanded = expandedQuizType,
                                        onDismissRequest = { expandedQuizType = false }
                                    ) {
                                        quizTypes.forEach { type ->
                                            DropdownMenuItem(text = { Text(type) }, onClick = { quizType = type; expandedQuizType = false })
                                        }
                                    }
                                }
                            }
                        }

                        // Date Selector (Full Width)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Scheduled Date", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = scheduledDate,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = SecondaryGold) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color.White,
                                        disabledContainerColor = Color(0xFF16161B),
                                        disabledBorderColor = Color.Gray.copy(alpha = 0.3f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(modifier = Modifier.matchParentSize().clickable { datePickerDialog.show() })
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Time components row (Hour, Minute, AM/PM)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Hour (12h)", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = scheduledHour,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold) },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledContainerColor = Color(0xFF16161B),
                                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { expandedHour = true })
                                    DropdownMenu(
                                        expanded = expandedHour,
                                        onDismissRequest = { expandedHour = false }
                                    ) {
                                        hoursOptions.forEach { hr ->
                                            DropdownMenuItem(text = { Text(hr) }, onClick = { scheduledHour = hr; expandedHour = false })
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Minute", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = scheduledMinute,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold) },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledContainerColor = Color(0xFF16161B),
                                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { expandedMinute = true })
                                    DropdownMenu(
                                        expanded = expandedMinute,
                                        onDismissRequest = { expandedMinute = false }
                                    ) {
                                        minutesOptions.forEach { min ->
                                            DropdownMenuItem(text = { Text(min) }, onClick = { scheduledMinute = min; expandedMinute = false })
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("AM/PM", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = scheduledAmPm,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold) },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledContainerColor = Color(0xFF16161B),
                                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { expandedAmPm = true })
                                    DropdownMenu(
                                        expanded = expandedAmPm,
                                        onDismissRequest = { expandedAmPm = false }
                                    ) {
                                        amPmOptions.forEach { ampm ->
                                            DropdownMenuItem(text = { Text(ampm) }, onClick = { scheduledAmPm = ampm; expandedAmPm = false })
                                        }
                                    }
                                }
                            }
                        }

                        if (creatorMode == "manual") {
                            // Question appender block inline
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF16161B)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("Questions Added: ${questionsList.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    
                                    OutlinedTextField(
                                        value = qText,
                                        onValueChange = { qText = it },
                                        placeholder = { Text("Question Text", color = Color.Gray, fontSize = 13.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = SecondaryGold, unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f))
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(value = optA, onValueChange = { optA = it }, placeholder = { Text("Option A", color = Color.Gray, fontSize = 13.sp) }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                        OutlinedTextField(value = optB, onValueChange = { optB = it }, placeholder = { Text("Option B", color = Color.Gray, fontSize = 13.sp) }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(value = optC, onValueChange = { optC = it }, placeholder = { Text("Option C", color = Color.Gray, fontSize = 13.sp) }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                        OutlinedTextField(value = optD, onValueChange = { optD = it }, placeholder = { Text("Option D", color = Color.Gray, fontSize = 13.sp) }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                    }

                                    OutlinedTextField(
                                        value = explanation,
                                        onValueChange = { explanation = it },
                                        placeholder = { Text("Explanation", color = Color.Gray, fontSize = 13.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = SecondaryGold)
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Correct Option: ", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            listOf("A", "B", "C", "D").forEach { opt ->
                                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 4.dp)) {
                                                    RadioButton(
                                                        selected = correctOpt == opt,
                                                        onClick = { correctOpt = opt },
                                                        colors = RadioButtonDefaults.colors(selectedColor = SecondaryGold, unselectedColor = Color.Gray)
                                                    )
                                                    Text(opt, color = Color.White, fontSize = 11.sp)
                                                }
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                if (qText.isBlank() || optA.isBlank() || optB.isBlank()) {
                                                    Toast.makeText(context, "Question and Options A/B are required.", Toast.LENGTH_SHORT).show()
                                                    return@Button
                                                }
                                                questionsList.add(
                                                    ManualQuestion(
                                                        questionText = qText.trim(),
                                                        optionA = optA.trim(),
                                                        optionB = optB.trim(),
                                                        optionC = optC.trim(),
                                                        optionD = optD.trim(),
                                                        correctOption = correctOpt,
                                                        explanation = explanation.trim()
                                                    )
                                                )
                                                qText = ""; optA = ""; optB = ""; optC = ""; optD = ""; explanation = ""
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGold, contentColor = Color.Black),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Add Q", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Submit builder button
                        RealisticButton(
                            onClick = {
                                val effectiveChapter = chapterName.trim().ifBlank { "$subject Comprehensive Quiz" }
                                val dateStr = scheduledDate.trim()
                                val timeStr = "$scheduledHour:$scheduledMinute $scheduledAmPm"
                                val formattedStart = "$dateStr $timeStr"

                                val parsedDate = run {
                                    var date: java.util.Date? = null
                                    val inputFormats = listOf("dd/MM/yyyy hh:mm a", "dd-MM-yyyy hh:mm a", "yyyy-MM-dd HH:mm", "dd/MM/yyyy HH:mm")
                                    for (fmt in inputFormats) {
                                        try {
                                            val sdf = java.text.SimpleDateFormat(fmt, java.util.Locale.US).apply {
                                                timeZone = java.util.TimeZone.getDefault()
                                            }
                                            date = sdf.parse(formattedStart)
                                            if (date != null) break
                                        } catch (e: Exception) {
                                            // try next
                                        }
                                    }
                                    date ?: java.util.Date()
                                }

                                val dbFormattedStart = try {
                                    java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).apply {
                                        timeZone = java.util.TimeZone.getTimeZone("Asia/Kolkata")
                                    }.format(parsedDate)
                                } catch (e: Exception) {
                                    java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())
                                }

                                if (creatorMode == "manual") {
                                    // If user entered question in fields but didn't click "Add Q", auto-add it
                                    if (qText.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                                        questionsList.add(
                                            ManualQuestion(
                                                questionText = qText.trim(),
                                                optionA = optA.trim(),
                                                optionB = optB.trim(),
                                                optionC = optC.trim().ifBlank { "None of above" },
                                                optionD = optD.trim().ifBlank { "All of above" },
                                                correctOption = correctOpt,
                                                explanation = explanation.trim()
                                            )
                                        )
                                        qText = ""
                                        optA = ""
                                        optB = ""
                                        optC = ""
                                        optD = ""
                                        explanation = ""
                                    }

                                    val finalQuestions = if (questionsList.isNotEmpty()) {
                                        questionsList.toList()
                                    } else {
                                        // Auto-seed questions from bank if empty
                                        val bankQs = com.sparkingpathshala.app.data.local.QuestionBank.getQuestions(classSelection, subject, 5)
                                        if (bankQs.isNotEmpty()) {
                                            bankQs.map { q ->
                                                ManualQuestion(
                                                    questionText = q.questionText,
                                                    optionA = q.optionA,
                                                    optionB = q.optionB,
                                                    optionC = q.optionC,
                                                    optionD = q.optionD,
                                                    correctOption = when (q.correctAnswer) {
                                                        0 -> "A"
                                                        1 -> "B"
                                                        2 -> "C"
                                                        else -> "D"
                                                    },
                                                    explanation = q.explanation
                                                )
                                            }
                                        } else {
                                            listOf(
                                                ManualQuestion(
                                                    questionText = "What is the primary concept in $subject for $classSelection?",
                                                    optionA = "Concept A",
                                                    optionB = "Concept B",
                                                    optionC = "Concept C",
                                                    optionD = "Concept D",
                                                    correctOption = "A",
                                                    explanation = "Core foundational concept."
                                                )
                                            )
                                        }
                                    }

                                    isLoading = true
                                    viewModel.createManualQuiz(
                                        ManualQuizRequest(
                                            subject = subject,
                                            classSelection = classSelection,
                                            chapter = effectiveChapter,
                                            quizType = quizType,
                                            scheduledStart = dbFormattedStart,
                                            questions = finalQuestions
                                        )
                                    ) { result ->
                                        isLoading = false
                                        if (result.isSuccess) {
                                            android.widget.Toast.makeText(context, "Manual Quiz created & scheduled successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                            scope.launch { snackbarHostState.showSnackbar("Manual Quiz Scheduled successfully.") }
                                            chapterName = ""
                                            questionsList.clear()
                                            loadQuizzes()
                                        } else {
                                            scope.launch { snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Failed to schedule quiz") }
                                        }
                                    }
                                } else {
                                    isLoading = true
                                    viewModel.generateAiQuiz(
                                        AiQuizRequest(
                                            subject = subject,
                                            classSelection = classSelection,
                                            chapter = effectiveChapter,
                                            quizType = quizType,
                                            scheduledStart = dbFormattedStart
                                        )
                                    ) { result ->
                                        isLoading = false
                                        if (result.isSuccess) {
                                            android.widget.Toast.makeText(context, "AI Quiz generated & scheduled successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                            scope.launch { snackbarHostState.showSnackbar("AI Quiz generated and scheduled successfully.") }
                                            chapterName = ""
                                            loadQuizzes()
                                        } else {
                                            scope.launch { snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Failed to generate AI quiz") }
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            containerColor = if (creatorMode == "manual") MaterialTheme.colorScheme.primary else Color(0xFF7C4DFF),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(if (creatorMode == "manual") "Creating Quiz..." else "Generating AI Quiz...", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            } else {
                                Icon(
                                    imageVector = if (creatorMode == "manual") Icons.Default.Add else Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (creatorMode == "manual") "SCHEDULE MANUAL QUIZ" else "GENERATE AI QUIZ NOW",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Active Schedules Panel
                item {
                    val allSelected = quizzes.isNotEmpty() && quizzes.all { selectedQuizIds.contains(it.id) }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Active Schedules",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 12.dp)
                        )

                        if (quizzes.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        if (allSelected) {
                                            selectedQuizIds.clear()
                                        } else {
                                            quizzes.forEach { q ->
                                                if (!selectedQuizIds.contains(q.id)) {
                                                    selectedQuizIds.add(q.id)
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Checkbox(
                                        checked = allSelected,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                quizzes.forEach { q ->
                                                    if (!selectedQuizIds.contains(q.id)) {
                                                        selectedQuizIds.add(q.id)
                                                    }
                                                }
                                            } else {
                                                selectedQuizIds.clear()
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                    Text("Select All Quizzes", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        if (selectedQuizIds.isNotEmpty()) {
                                            isLoading = true
                                            viewModel.deleteMultipleQuizzes(selectedQuizIds.toList()) { result ->
                                                isLoading = false
                                                if (result.isSuccess) {
                                                    scope.launch { snackbarHostState.showSnackbar("Selected quizzes deleted successfully!") }
                                                    selectedQuizIds.clear()
                                                    loadQuizzes()
                                                } else {
                                                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                                                    scope.launch { snackbarHostState.showSnackbar("Failed to delete selected quizzes: $errorMsg") }
                                                }
                                            }
                                        }
                                    },
                                    enabled = selectedQuizIds.isNotEmpty(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red.copy(alpha = 0.8f),
                                        contentColor = Color.White,
                                        disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                                        disabledContentColor = Color.Gray
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Delete Selected (${selectedQuizIds.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.15f))
                    }
                }

                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = SecondaryGold)
                        }
                    }
                } else if (quizzes.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            Text("No active schedules found.", color = Color.Gray)
                        }
                    }
                } else {
                    items(quizzes, key = { it.id }, contentType = { "admin_quiz" }) { quiz ->
                        val isLaunched = isQuizActive(quiz.scheduledStart)
                        val isSelected = selectedQuizIds.contains(quiz.id)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                selectedQuizIds.add(quiz.id)
                                            } else {
                                                selectedQuizIds.remove(quiz.id)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = MaterialTheme.colorScheme.outline
                                        ),
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        // Class & Subject Row with distinctive pill chips
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // Class Chip (High Visibility)
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                                            ) {
                                                Text(
                                                    text = quiz.classSelection.ifBlank { "All Classes" },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                    maxLines = 1
                                                )
                                            }

                                            // Subject Chip
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f))
                                            ) {
                                                Text(
                                                    text = quiz.subject,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            // Mode Badge
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = if (quiz.isManual == 1) "MANUAL" else "AI-GEN",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Topic / Chapter - Prominent & Highly Legible
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Topic: ${quiz.chapter.ifBlank { "General Practice" }}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Schedule Slot & Quiz Type
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = null,
                                                modifier = Modifier.size(13.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Slot: ${quiz.scheduledStart.ifBlank { "Immediate" }} • ${quiz.quizType}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (isLaunched) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                        ) {
                                            Text(
                                                text = "SCHEDULED",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteQuiz(quiz.id) { result ->
                                                if (result.isSuccess) {
                                                    scope.launch { snackbarHostState.showSnackbar("Quiz schedule deleted.") }
                                                    loadQuizzes()
                                                } else {
                                                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                                                    scope.launch { snackbarHostState.showSnackbar("Failed to delete quiz: $errorMsg") }
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
