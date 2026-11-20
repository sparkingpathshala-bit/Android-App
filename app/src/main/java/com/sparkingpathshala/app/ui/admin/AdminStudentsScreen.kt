package com.sparkingpathshala.app.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.remote.AdminStudentInfo
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStudentsScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var studentsList by remember { mutableStateOf<List<AdminStudentInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedClassFilter by remember { mutableStateOf("All") }
    var sortOption by remember { mutableStateOf("Name (A-Z)") }
    var displayedLimit by remember { mutableIntStateOf(15) }

    LaunchedEffect(searchQuery, selectedClassFilter, sortOption) {
        displayedLimit = 15
    }

    val classOptions = com.sparkingpathshala.app.utils.AppConstants.CLASS_OPTIONS_WITH_ALL

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<AdminStudentInfo?>(null) }
    
    var showAddDialog by remember { mutableStateOf(false) }

    fun loadStudents() {
        isLoading = true
        viewModel.getAdminStudents { result ->
            isLoading = false
            studentsList = result.getOrElse { emptyList() }
        }
    }

    LaunchedEffect(Unit) {
        loadStudents()
    }

    val filteredStudents = remember(studentsList, searchQuery, selectedClassFilter, sortOption) {
        val filtered = studentsList.filter { student ->
            val matchesClass = selectedClassFilter == "All" || student.classSelection.equals(selectedClassFilter, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                student.name.contains(searchQuery, ignoreCase = true) ||
                student.email.contains(searchQuery, ignoreCase = true) ||
                (student.mobile ?: "").contains(searchQuery) ||
                student.classSelection.contains(searchQuery, ignoreCase = true)
            matchesClass && matchesQuery
        }

        when (sortOption) {
            "Name (A-Z)" -> filtered.sortedBy { it.name.lowercase() }
            "Name (Z-A)" -> filtered.sortedByDescending { it.name.lowercase() }
            "Class" -> filtered.sortedBy { it.classSelection }
            "Latest" -> filtered.sortedByDescending { it.id }
            else -> filtered
        }
    }

    val paginatedStudents = remember(filteredStudents, displayedLimit) {
        filteredStudents.take(displayedLimit)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Students", fontWeight = FontWeight.Bold) },
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Student", color = Color.White) },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White) },
                containerColor = PrimaryBlue,
                onClick = { showAddDialog = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, email, class, or mobile...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SecondaryGold,
                        focusedLabelColor = SecondaryGold,
                        cursorColor = SecondaryGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )

                // Class filter chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(classOptions, key = { it }) { cls ->
                        FilterChip(
                            selected = selectedClassFilter == cls,
                            onClick = { selectedClassFilter = cls },
                            label = { Text(cls, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else if (filteredStudents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No students found.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(paginatedStudents, key = { it.id }, contentType = { "admin_student" }) { student ->
                            StudentRowItem(
                                student = student,
                                onLockToggle = {
                                    val newStatus = if (student.status == "inactive") "approved" else "inactive"
                                    viewModel.updateStudentStatus(student.id, newStatus) { result ->
                                        if (result.isSuccess) {
                                            val action = if (newStatus == "approved") "Unlocked" else "Locked"
                                            Toast.makeText(context, "Student $action Successfully!", Toast.LENGTH_SHORT).show()
                                            loadStudents()
                                        } else {
                                            Toast.makeText(context, "Action failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onEdit = {
                                    selectedStudent = student
                                    showEditDialog = true
                                },
                                onDelete = {
                                    viewModel.deleteStudent(student.id) { result ->
                                        if (result.isSuccess) {
                                            Toast.makeText(context, "Student Deleted Successfully!", Toast.LENGTH_SHORT).show()
                                            loadStudents()
                                        } else {
                                            Toast.makeText(context, "Delete failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            )
                        }

                        if (filteredStudents.size > displayedLimit) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    OutlinedButton(
                                        onClick = { displayedLimit += 15 },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Load More (${paginatedStudents.size} of ${filteredStudents.size})", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Edit Dialog
            val currentStudent = selectedStudent
            if (showEditDialog && currentStudent != null) {
                EditStudentDialog(
                    student = currentStudent,
                    onDismiss = { showEditDialog = false },
                    onSave = { updatedMap ->
                        viewModel.updateStudent(currentStudent.id, updatedMap) { result ->
                            if (result.isSuccess) {
                                Toast.makeText(context, "Student Details Updated!", Toast.LENGTH_SHORT).show()
                                showEditDialog = false
                                loadStudents()
                            } else {
                                Toast.makeText(context, "Update failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }

            // Add Dialog
            if (showAddDialog) {
                AddStudentDialog(
                    onDismiss = { showAddDialog = false },
                    onAdd = { name, email, classLevel, mobile, monthlyFee ->
                        val data = mapOf(
                            "name" to name,
                            "email" to email,
                            "classSelection" to classLevel,
                            "mobile" to mobile,
                            "monthlyFee" to monthlyFee
                        )
                        viewModel.addAdminRecord("students", data) { result ->
                            if (result.isSuccess) {
                                Toast.makeText(context, "Student Added Successfully!", Toast.LENGTH_SHORT).show()
                                showAddDialog = false
                                loadStudents()
                            } else {
                                Toast.makeText(context, "Failed to add student: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StudentRowItem(
    student: AdminStudentInfo,
    onLockToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLocked = student.status == "inactive"
    val borderColor = if (isLocked) Color.Red.copy(alpha = 0.6f) else PrimaryBlue.copy(alpha = 0.15f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                    
                    if (isLocked) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = "LOCKED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Email: ", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Text(text = student.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold)
                }

                student.mobile?.let { mobile ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Mobile: ", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text(text = mobile, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Class: ", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text(text = student.classSelection, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Fee: ", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text(text = "₹${student.monthlyFee ?: 0.0}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onLockToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isLocked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = CircleShape,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(if (isLocked) "UNLOCK" else "LOCK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onEdit,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                    shape = CircleShape,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text("EDIT DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                var showConfirmDelete by remember { mutableStateOf(false) }
                if (showConfirmDelete) {
                    AlertDialog(
                        onDismissRequest = { showConfirmDelete = false },
                        title = { Text("Delete Student?", fontWeight = FontWeight.Bold, color = Color.Red) },
                        text = { Text("Are you sure you want to permanently delete this student record?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showConfirmDelete = false
                                    onDelete()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Delete", color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showConfirmDelete = false }) {
                                Text("Cancel", color = Color.Gray)
                            }
                        }
                    )
                }

                OutlinedButton(
                    onClick = { showConfirmDelete = true },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                    shape = CircleShape,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text("DELETE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EditStudentDialog(
    student: AdminStudentInfo,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any>) -> Unit
) {
    var name by remember { mutableStateOf(student.name) }
    var email by remember { mutableStateOf(student.email) }
    var mobile by remember { mutableStateOf(student.mobile ?: "") }
    var classSelection by remember { mutableStateOf(student.classSelection) }
    var monthlyFee by remember { mutableStateOf(student.monthlyFee?.toString() ?: "0.0") }
    var status by remember { mutableStateOf(student.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Student Profile", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") })
                OutlinedTextField(value = classSelection, onValueChange = { classSelection = it }, label = { Text("Class") })
                OutlinedTextField(
                    value = monthlyFee, 
                    onValueChange = { monthlyFee = it }, 
                    label = { Text("Monthly Fee") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val map = mapOf(
                        "name" to name.trim(),
                        "email" to email.trim(),
                        "classSelection" to classSelection.trim(),
                        "mobile" to mobile.trim(),
                        "monthlyFee" to (monthlyFee.toDoubleOrNull() ?: 0.0),
                        "status" to if (status == "active") "approved" else status
                    )
                    onSave(map)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var classLevel by remember { mutableStateOf("Class 10") }
    var monthlyFee by remember { mutableStateOf("1500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Student", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") })
                OutlinedTextField(value = classLevel, onValueChange = { classLevel = it }, label = { Text("Class") })
                OutlinedTextField(
                    value = monthlyFee, 
                    onValueChange = { monthlyFee = it }, 
                    label = { Text("Monthly Fee") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onAdd(name.trim(), email.trim(), classLevel.trim(), mobile.trim(), monthlyFee.toDoubleOrNull() ?: 0.0)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Create Student", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
