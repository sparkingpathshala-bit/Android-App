package com.sparkingpathshala.app.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.remote.PendingStudentInfo
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApprovalsScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var studentsList by remember { mutableStateOf<List<PendingStudentInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    fun loadPending() {
        isLoading = true
        viewModel.getPendingStudents { result ->
            isLoading = false
            studentsList = result.getOrElse { emptyList() }
        }
    }

    LaunchedEffect(Unit) {
        loadPending()
    }

    val pendingStudents = remember(studentsList) {
        studentsList.filter { it.status == "pending_approval" }
    }

    val rejectedStudents = remember(studentsList) {
        studentsList.filter { it.status == "rejected" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Approvals Portal", fontWeight = FontWeight.Bold) },
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryBlue
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // PENDING SECTION
                    item {
                        Text(
                            text = "Pending Student Approvals",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SecondaryGold
                        )
                    }

                    if (pendingStudents.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "No pending applications at this time.", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        items(pendingStudents, key = { it.id }, contentType = { "pending_student" }) { student ->
                            ApprovalRowItem(
                                student = student,
                                onApprove = {
                                    viewModel.approveStudent(student.id) { result ->
                                        if (result.isSuccess) {
                                            Toast.makeText(context, "Student Approved!", Toast.LENGTH_SHORT).show()
                                            loadPending()
                                        } else {
                                            Toast.makeText(context, "Approval failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onReject = {
                                    viewModel.rejectStudent(student.id) { result ->
                                        if (result.isSuccess) {
                                            Toast.makeText(context, "Student Rejected!", Toast.LENGTH_SHORT).show()
                                            loadPending()
                                        } else {
                                            Toast.makeText(context, "Rejection failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onDelete = {
                                    viewModel.deleteStudent(student.id) { result ->
                                        if (result.isSuccess) {
                                            Toast.makeText(context, "Student Deleted Completely!", Toast.LENGTH_SHORT).show()
                                            loadPending()
                                        } else {
                                            Toast.makeText(context, "Deletion failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            )
                        }
                    }

                    // REJECTED SECTION
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Rejected Student Approvals",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SecondaryGold
                        )
                    }

                    if (rejectedStudents.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "No rejected applications at this time.", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        items(rejectedStudents, key = { it.id }, contentType = { "rejected_student" }) { student ->
                            ApprovalRowItem(
                                student = student,
                                onApprove = {
                                    viewModel.approveStudent(student.id) { result ->
                                        if (result.isSuccess) {
                                            Toast.makeText(context, "Student Approved!", Toast.LENGTH_SHORT).show()
                                            loadPending()
                                        } else {
                                            Toast.makeText(context, "Approval failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onReject = null, // Already rejected
                                onDelete = {
                                    viewModel.deleteStudent(student.id) { result ->
                                        if (result.isSuccess) {
                                            Toast.makeText(context, "Student Deleted Completely!", Toast.LENGTH_SHORT).show()
                                            loadPending()
                                        } else {
                                            Toast.makeText(context, "Deletion failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            )
                        }
                    }

                    // FOOTER
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "2026@sparking pathshala all right reserved",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Coded & Designed with ❤️ by manish.",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApprovalRowItem(
    student: PendingStudentInfo,
    onApprove: () -> Unit,
    onReject: (() -> Unit)?,
    onDelete: () -> Unit
) {
    val borderColor = when (student.status) {
        "pending_approval" -> SecondaryGold.copy(alpha = 0.8f)
        "rejected" -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
        else -> PrimaryBlue.copy(alpha = 0.1f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Student Main details
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = student.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (student.isResignup == 1) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = "RE-SIGNUP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Email: ",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = student.email,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    student.mobile?.let { mobile ->
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Mobile: ",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = mobile,
                            fontSize = 12.sp,
                            color = SecondaryGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Class: ",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = student.classSelection,
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // APPROVE (Yellow pill button with dark text)
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryGold,
                        contentColor = Color.Black
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier.weight(1.0f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text("APPROVE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // REJECT (Outline button with yellow text)
                if (onReject != null) {
                    OutlinedButton(
                        onClick = onReject,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryGold.copy(alpha = 0.8f)),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier.weight(1.0f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryGold),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("REJECT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // DELETE COMPLETELY (Outline button with gray text)
                OutlinedButton(
                    onClick = onDelete,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text("DELETE COMPLETELY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
