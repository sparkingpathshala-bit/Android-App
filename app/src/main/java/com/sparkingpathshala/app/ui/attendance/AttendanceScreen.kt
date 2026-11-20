package com.sparkingpathshala.app.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.local.AttendanceEntity
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.PrimaryBlue

@Composable
fun AttendanceScreen(
    viewModel: DashboardViewModel,
    paddingValues: PaddingValues = PaddingValues(),
    onBack: () -> Unit
) {
    val attendance by viewModel.attendance.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.syncAttendance()
    }

    val uniqueMonths = remember(attendance) {
        val calendar = java.util.Calendar.getInstance()
        val currentYear = calendar.get(java.util.Calendar.YEAR)
        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1 // 1-based (1 to 12)
        val monthsFromLogs = attendance.mapNotNull { extractYearMonth(it.date) }
        val allCurrentYearMonths = (1..currentMonth).map { month ->
            "$currentYear-${month.toString().padStart(2, '0')}"
        }
        val filteredLogs = monthsFromLogs.filter { logYm ->
            val parts = logYm.split("-")
            if (parts.size >= 2) {
                val logYear = parts[0].toIntOrNull() ?: 0
                val logMonth = parts[1].toIntOrNull() ?: 0
                logYear < currentYear || (logYear == currentYear && logMonth <= currentMonth)
            } else {
                false
            }
        }
        (filteredLogs + allCurrentYearMonths).distinct().sortedDescending()
    }

    var selectedMonth by remember { mutableStateOf("All") }
    var expandedDropdown by remember { mutableStateOf(false) }

    val filteredAttendance = remember(attendance, selectedMonth) {
        if (selectedMonth == "All") {
            attendance
        } else {
            attendance.filter { extractYearMonth(it.date) == selectedMonth }
        }
    }

    val totalDays = filteredAttendance.size
    val presentDays = filteredAttendance.count { it.isPresent }
    val absentDays = totalDays - presentDays
    val percentage = if (totalDays > 0) {
        (presentDays.toFloat() / totalDays * 100).toInt()
    } else 100

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Tracker", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Viewing: ${if (selectedMonth == "All") "All Months" else formatMonthYear(selectedMonth)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    
                    Box {
                        Button(
                            onClick = { expandedDropdown = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Filter Month", fontSize = 12.sp, color = Color.White)
                        }
                        
                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Months") },
                                onClick = {
                                    selectedMonth = "All"
                                    expandedDropdown = false
                                }
                            )
                            uniqueMonths.forEach { yyyyMM ->
                                DropdownMenuItem(
                                    text = { Text(formatMonthYear(yyyyMM)) },
                                    onClick = {
                                        selectedMonth = yyyyMM
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            // Overall summary card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Attendance Summary",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$percentage%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (percentage >= 75) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (percentage >= 75) "Great! You satisfy the 75% attendance criteria." else "Warning: Attendance falls short of the required 75% criteria.",
                            fontSize = 12.sp,
                            color = if (percentage >= 75) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Present", fontSize = 12.sp, color = Color.Gray)
                                Text(text = "$presentDays Days", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Absent", fontSize = 12.sp, color = Color.Gray)
                                Text(text = "$absentDays Days", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Total Days", fontSize = 12.sp, color = Color.Gray)
                                Text(text = "$totalDays Days", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Daily Logs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (filteredAttendance.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No logs recorded.", color = Color.Gray)
                    }
                }
            } else {
                items(filteredAttendance, key = { it.date }, contentType = { "attendance" }) { record ->
                    AttendanceItemRow(record = record)
                }
            }
        }
    }
}

@Composable
fun AttendanceItemRow(record: AttendanceEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = if (record.isPresent) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (record.isPresent) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (record.isPresent) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = record.date,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = record.remarks,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Text(
                text = if (record.isPresent) "PRESENT" else "ABSENT",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (record.isPresent) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}

private fun formatMonthYear(yyyyMM: String): String {
    if (yyyyMM.length < 7) return yyyyMM
    val parts = yyyyMM.split("-")
    if (parts.size < 2) return yyyyMM
    val year = parts[0]
    val monthName = when (parts[1]) {
        "01" -> "January"
        "02" -> "February"
        "03" -> "March"
        "04" -> "April"
        "05" -> "May"
        "06" -> "June"
        "07" -> "July"
        "08" -> "August"
        "09" -> "September"
        "10" -> "October"
        "11" -> "November"
        "12" -> "December"
        else -> parts[1]
    }
    return "$monthName $year"
}

private fun extractYearMonth(dateStr: String): String? {
    val clean = dateStr.trim()
    val parts = if (clean.contains("-")) {
        clean.split("-")
    } else if (clean.contains("/")) {
        clean.split("/")
    } else {
        return null
    }
    if (parts.size >= 3) {
        val first = parts[0]
        val second = parts[1]
        val third = parts[2]
        if (first.length == 4 && first.all { it.isDigit() }) {
            return "$first-${second.padStart(2, '0')}"
        } else if (third.length == 4 && third.all { it.isDigit() }) {
            return "$third-${second.padStart(2, '0')}"
        }
    } else if (parts.size == 2) {
        val first = parts[0]
        val second = parts[1]
        if (first.length == 4 && first.all { it.isDigit() }) {
            return "$first-${second.padStart(2, '0')}"
        }
    }
    return null
}
