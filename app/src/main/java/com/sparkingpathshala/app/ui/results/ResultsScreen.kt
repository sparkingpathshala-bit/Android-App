package com.sparkingpathshala.app.ui.results
// [Backend linked] getQuizHistory() in AppRepository feeds quiz attempt history here.
// Wire: viewModel.loadQuizHistory() → repository.getQuizHistory(studentId)


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val results by viewModel.results.collectAsState()
    val attendance by viewModel.attendance.collectAsState()
    val user by viewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.syncResults()
    }

    var selectedMonth by remember { mutableStateOf("All Months") }
    var expandedMonth by remember { mutableStateOf(false) }

    val months = listOf(
        "All Months", "January", "February", "March", "April", "May",
        "June", "July", "August", "September", "October", "November", "December"
    )

    // Dynamic Overall calculations
    val attendancePercentage = remember(attendance) {
        val totalDays = attendance.size
        val presentDays = attendance.count { it.isPresent }
        if (totalDays > 0) (presentDays.toFloat() / totalDays * 100).toInt() else 0
    }

    val averageScore = remember(results) {
        val validResults = results.filter { it.maxMarks > 0 }
        if (validResults.isNotEmpty()) {
            (validResults.sumOf { it.marksObtained.toDouble() / it.maxMarks } / validResults.size * 100).toInt()
        } else 0
    }

    val filteredResults = remember(results, selectedMonth) {
        if (selectedMonth == "All Months") {
            results
        } else {
            val monthIndex = months.indexOf(selectedMonth)
            val monthStr = String.format(java.util.Locale.US, "-%02d-", monthIndex)
            results.filter { it.date.contains(monthStr) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results & Performance", fontWeight = FontWeight.Bold) },
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
                .background(Color(0xFF0F0F12))
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Welcome and class info header
                item {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            text = "Welcome back, ${user?.name ?: "Student"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Class: ${user?.classLevel ?: "Class 10"} | Student ID: ${user?.rollNo ?: "SP-000"} | ",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Status: ",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Active",
                                fontSize = 12.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Overall Performance Card with side-by-side circular charts
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF16161B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Overall Performance",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryGold
                            )

                            Text(
                                text = "Real-time visualization mapping academic exam percentages against attendance ratios to track overall consistency.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Circle 1: Attendance Ratio
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(76.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { attendancePercentage / 100f },
                                            color = SecondaryGold,
                                            trackColor = Color.DarkGray.copy(alpha = 0.5f),
                                            strokeWidth = 6.dp,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Text(
                                            text = "$attendancePercentage%",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Overall Attendance Ratio",
                                        color = Color.Gray,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Circle 2: Academic Avg Score
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(76.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { averageScore / 100f },
                                            color = SecondaryGold,
                                            trackColor = Color.DarkGray.copy(alpha = 0.5f),
                                            strokeWidth = 6.dp,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Text(
                                            text = "$averageScore%",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Academic Avg Score",
                                        color = Color.Gray,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Weekly Test Results section with Month filter dropdown
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Test Results",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Dropdown filter box
                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF16161B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                                modifier = Modifier.clickable { expandedMonth = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = selectedMonth, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(16.dp))
                                }
                            }

                            DropdownMenu(
                                expanded = expandedMonth,
                                onDismissRequest = { expandedMonth = false }
                            ) {
                                months.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m) },
                                        onClick = {
                                            selectedMonth = m
                                            expandedMonth = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // List of filtered results
                if (filteredResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No results found for this month.", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(filteredResults, key = { it.id }, contentType = { "result" }) { result ->
                        val rawPercentage = (result.marksObtained / result.maxMarks * 100)
                        val percentageStr = if (rawPercentage % 1.0 == 0.0) rawPercentage.toInt().toString() else String.format(java.util.Locale.US, "%.1f", rawPercentage)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16161B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(Color(0xFFE65100).copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Assessment, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = result.subject,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val marksObtainedStr = if (result.marksObtained % 1.0 == 0.0) result.marksObtained.toInt().toString() else result.marksObtained.toString()
                                        val maxMarksStr = if (result.maxMarks % 1.0 == 0.0) result.maxMarks.toInt().toString() else result.maxMarks.toString()
                                        Text(
                                            text = "Marks: $marksObtainedStr/$maxMarksStr (${result.examType})",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$percentageStr%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = when {
                                            rawPercentage >= 85 -> Color(0xFF4CAF50)
                                            rawPercentage >= 50 -> SecondaryGold
                                            else -> Color(0xFFEF5350)
                                        }
                                    )
                                    Text(
                                        text = result.date,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
