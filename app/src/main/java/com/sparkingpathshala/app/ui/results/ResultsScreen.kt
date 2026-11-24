package com.sparkingpathshala.app.ui.results

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
import com.sparkingpathshala.app.ui.common.EmptyStateView
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold
import com.sparkingpathshala.app.ui.theme.SparkingIconBox

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
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                // Welcome and class info header
                item {
                    Column {
                        Text(
                            text = "Welcome back, ${user?.name ?: "Student"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Class: ${user?.classLevel ?: "Class 10"} • Roll: ${user?.rollNo ?: "SP-000"} • ",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 10.sp,
                                    color = Color(0xFF15803D),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Overall Performance Card with side-by-side circular charts
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Overall Performance",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Real-time visualization mapping academic exam percentages against attendance consistency.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { attendancePercentage / 100f },
                                            color = Color(0xFF10B981),
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                            strokeWidth = 7.dp,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Text(
                                            text = "$attendancePercentage%",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Attendance Ratio",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                // Circle 2: Academic Avg Score
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { averageScore / 100f },
                                            color = PrimaryBlue,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                            strokeWidth = 7.dp,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Text(
                                            text = "$averageScore%",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Academic Score",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.SemiBold
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
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        // Dropdown filter box
                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable { expandedMonth = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = selectedMonth, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
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
                        EmptyStateView(
                            icon = Icons.Default.Assessment,
                            title = "No Results Found",
                            subtitle = "No test results recorded for $selectedMonth yet.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        )
                    }
                } else {
                    items(filteredResults, key = { it.id }, contentType = { "result" }) { result ->
                        val rawPercentage = (result.marksObtained / result.maxMarks * 100)
                        val percentageStr = if (rawPercentage % 1.0 == 0.0) rawPercentage.toInt().toString() else String.format(java.util.Locale.US, "%.1f", rawPercentage)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                    SparkingIconBox(
                                        icon = Icons.Default.Assessment,
                                        containerColor = PrimaryBlue.copy(alpha = 0.1f),
                                        iconTint = PrimaryBlue,
                                        boxSize = 40.dp,
                                        iconSize = 22.dp,
                                        cornerRadius = 12.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = result.subject,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val marksObtainedStr = if (result.marksObtained % 1.0 == 0.0) result.marksObtained.toInt().toString() else result.marksObtained.toString()
                                        val maxMarksStr = if (result.maxMarks % 1.0 == 0.0) result.maxMarks.toInt().toString() else result.maxMarks.toString()
                                        Text(
                                            text = "Marks: $marksObtainedStr/$maxMarksStr (${result.examType})",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                
                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when {
                                            rawPercentage >= 80 -> Color(0xFFDCFCE7)
                                            rawPercentage >= 50 -> Color(0xFFFEF3C7)
                                            else -> Color(0xFFFEE2E2)
                                        }
                                    ) {
                                        Text(
                                            text = "$percentageStr%",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = when {
                                                rawPercentage >= 80 -> Color(0xFF15803D)
                                                rawPercentage >= 50 -> Color(0xFFB45309)
                                                else -> Color(0xFFB91C1C)
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = result.date,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
