package com.sparkingpathshala.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.sparkingpathshala.app.data.local.isAdmin
import com.sparkingpathshala.app.data.remote.AdminRecord
import com.sparkingpathshala.app.data.remote.AdminStudentInfo
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRecordsScreen(
    viewModel: DashboardViewModel,
    initialTab: Int = 0,
    outerPadding: PaddingValues = PaddingValues(),
    onBack: () -> Unit
) {
    val currentUser by viewModel.user.collectAsState()
    val isBillingAdmin = currentUser.isAdmin()

    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val tabs = remember(isBillingAdmin) {
        if (isBillingAdmin) {
            listOf("Attendance", "Test Results", "Fees")
        } else {
            listOf("Attendance", "Test Results")
        }
    }

    var records by remember { mutableStateOf<List<AdminRecord>>(emptyList()) }
    var students by remember { mutableStateOf<List<AdminStudentInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQueries by remember { mutableStateOf("") }

    // Filtering & Sorting State
    var selectedClassFilter by remember { mutableStateOf("All") }
    var selectedSortOption by remember { mutableStateOf("Date (Newest)") }
    var currentPage by remember { mutableIntStateOf(1) }
    val pageSize = 10

    var showAddDialog by remember { mutableStateOf(false) }
    var editingRecord by remember { mutableStateOf<AdminRecord?>(null) }
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val currentType = when (selectedTab) {
        0 -> "attendance"
        1 -> "test_results"
        else -> "fees"
    }

    val classesList = com.sparkingpathshala.app.utils.AppConstants.CLASS_OPTIONS_WITH_ALL

    // Load data helper
    fun loadData() {
        isLoading = true
        records = emptyList()
        val queriedType = currentType
        viewModel.getAdminRecords(queriedType) { result ->
            if (queriedType == currentType) {
                records = result.getOrElse { emptyList() }
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedTab) {
        currentPage = 1
        loadData()
    }

    LaunchedEffect(Unit) {
        viewModel.getAdminStudents { result ->
            students = result.getOrElse { emptyList() }
        }
    }

    // Map student emails to their classSelection for class filtering
    val studentClassMap = remember(students) {
        students.associate { it.email.lowercase().trim() to it.classSelection }
    }

    val filteredRecords = remember(records, searchQueries, selectedClassFilter, selectedSortOption, studentClassMap, currentType) {
        var list = records

        // Search query
        if (searchQueries.isNotBlank()) {
            list = list.filter {
                it.studentEmail.contains(searchQueries, ignoreCase = true) ||
                (it.subject ?: "").contains(searchQueries, ignoreCase = true) ||
                (it.date ?: "").contains(searchQueries, ignoreCase = true)
            }
        }

        // Class filter
        if (selectedClassFilter != "All") {
            list = list.filter { record ->
                val studentClass = studentClassMap[record.studentEmail.lowercase().trim()]
                studentClass.equals(selectedClassFilter, ignoreCase = true)
            }
        }

        // Sorting
        when (selectedSortOption) {
            "Date (Newest)" -> list.sortedByDescending { it.date ?: it.examDate ?: it.paymentDate ?: "" }
            "Date (Oldest)" -> list.sortedBy { it.date ?: it.examDate ?: it.paymentDate ?: "" }
            "Score (High to Low)" -> list.sortedByDescending { it.marksObtained ?: 0.0 }
            "Score (Low to High)" -> list.sortedBy { it.marksObtained ?: 0.0 }
            "Student (A-Z)" -> list.sortedBy { it.studentEmail.lowercase() }
            else -> list
        }
    }

    // Reset pagination if filtered records shrink
    val totalPages = maxOf(1, (filteredRecords.size + pageSize - 1) / pageSize)
    val pagedRecords = remember(filteredRecords, currentPage, totalPages) {
        val safePage = currentPage.coerceIn(1, totalPages)
        val fromIndex = (safePage - 1) * pageSize
        filteredRecords.drop(fromIndex).take(pageSize)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manage Student Records", fontWeight = FontWeight.Bold) },
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
            if (currentType != "fees" || isBillingAdmin) {
                ExtendedFloatingActionButton(
                    text = { Text("Add Entry", color = Color.White, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White) },
                    containerColor = PrimaryBlue,
                    onClick = { 
                        editingRecord = null
                        showAddDialog = true 
                    }
                )
            }
        }
    ) { scaffoldPadding ->
        val effectiveBottomPadding = maxOf(
            scaffoldPadding.calculateBottomPadding(),
            outerPadding.calculateBottomPadding()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = scaffoldPadding.calculateTopPadding(),
                    bottom = effectiveBottomPadding
                )
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryBlue
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title, fontWeight = FontWeight.SemiBold) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            // Attendance Date Lock Banner
            if (currentType == "attendance") {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LockClock,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Attendance Lock: Active for Today ($todayDate). Back-date & Future marking disabled.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Filters & Controls Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQueries,
                    onValueChange = { searchQueries = it; currentPage = 1 },
                    placeholder = { Text("Search by student email or subject...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQueries.isNotEmpty()) {
                            IconButton(onClick = { searchQueries = ""; currentPage = 1 }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Class Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    classesList.forEach { cls ->
                        FilterChip(
                            selected = selectedClassFilter == cls,
                            onClick = {
                                selectedClassFilter = cls
                                currentPage = 1
                            },
                            label = { Text(cls, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Sort Dropdown & Record Count
                if (currentType == "test_results" || currentType == "attendance") {
                    var sortExpanded by remember { mutableStateOf(false) }
                    val sortOptions = if (currentType == "test_results") {
                        listOf("Date (Newest)", "Date (Oldest)", "Score (High to Low)", "Score (Low to High)", "Student (A-Z)")
                    } else {
                        listOf("Date (Newest)", "Date (Oldest)", "Student (A-Z)")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Records: ${filteredRecords.size}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )

                        Box {
                            OutlinedButton(
                                onClick = { sortExpanded = true },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(selectedSortOption, fontSize = 12.sp)
                            }
                            DropdownMenu(
                                expanded = sortExpanded,
                                onDismissRequest = { sortExpanded = false }
                            ) {
                                sortOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt, fontSize = 13.sp) },
                                        onClick = {
                                            selectedSortOption = opt
                                            sortExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (currentType == "fees" && !isBillingAdmin) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = "Access Locked: Only Sajjan Mallik (Primary Billing Admin) can modify fees data.",
                          color = MaterialTheme.colorScheme.onErrorContainer,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (filteredRecords.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No records found for selected filters.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(pagedRecords, key = { it.id }, contentType = { "admin_record" }) { record ->
                        RecordItemRow(
                            record = record,
                            type = currentType,
                            onEdit = {
                                if (currentType != "fees" || isBillingAdmin) {
                                    editingRecord = record
                                    showAddDialog = true
                                }
                            },
                            onDelete = {
                                if (currentType != "fees" || isBillingAdmin) {
                                    viewModel.deleteAdminRecord(currentType, record.id) { result ->
                                        if (result.isSuccess) {
                                            scope.launch { snackbarHostState.showSnackbar("Record deleted successfully.") }
                                            viewModel.syncAll()
                                            loadData()
                                        } else {
                                            scope.launch { snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Delete failed") }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                // Pagination Controls
                if (totalPages > 1) {
                    Surface(
                        tonalElevation = 3.dp,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { if (currentPage > 1) currentPage-- },
                                enabled = currentPage > 1
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Previous")
                            }

                            Text(
                                text = "Page $currentPage of $totalPages",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            TextButton(
                                onClick = { if (currentPage < totalPages) currentPage++ },
                                enabled = currentPage < totalPages
                            ) {
                                Text("Next")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Add/Edit Dialog
    if (showAddDialog) {
        RecordDialog(
            type = currentType,
            record = editingRecord,
            students = students,
            todayDate = todayDate,
            onDismiss = { showAddDialog = false },
            onSubmit = { payload ->
                showAddDialog = false
                if (editingRecord == null) {
                    viewModel.addAdminRecord(currentType, payload) { result ->
                        if (result.isSuccess) {
                            scope.launch { snackbarHostState.showSnackbar("Record created successfully.") }
                            viewModel.syncAll()
                            loadData()
                        } else {
                            scope.launch { snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Failed to save") }
                        }
                    }
                } else {
                    val recordId = editingRecord?.id
                    if (recordId != null) {
                        viewModel.updateAdminRecord(currentType, recordId, payload) { result ->
                            if (result.isSuccess) {
                                scope.launch { snackbarHostState.showSnackbar("Record updated successfully.") }
                                viewModel.syncAll()
                                loadData()
                            } else {
                                scope.launch { snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Failed to update") }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun RecordItemRow(
    record: AdminRecord,
    type: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.studentEmail,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                when (type) {
                    "attendance" -> {
                        Text(text = "Date: ${record.date ?: "N/A"}", fontSize = 12.sp)
                        Text(
                            text = "Status: ${record.status ?: "Absent"}", 
                            fontWeight = FontWeight.Bold, 
                            color = if (record.status == "Present") MaterialTheme.colorScheme.primary else Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    "test_results" -> {
                        Text(text = "Subject: ${record.subject ?: "N/A"}", fontSize = 12.sp)
                        Text(text = "Date: ${record.examDate ?: "N/A"}", fontSize = 12.sp)
                        Text(
                            text = "Score: ${record.marksObtained ?: 0.0} / ${record.maxMarks ?: 100.0}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    "fees" -> {
                        Text(text = "Month: ${record.month ?: "N/A"}", fontSize = 12.sp)
                        Text(text = "Total: ₹${record.totalDue ?: 0.0} | Paid: ₹${record.amountPaid ?: 0.0}", fontSize = 12.sp)
                        if (record.remarks?.isNotBlank() == true) {
                            Text(text = "Remarks: ${record.remarks}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (record.status == "Paid") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = record.status ?: "Unpaid",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (record.status == "Paid") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDialog(
    type: String,
    record: AdminRecord?,
    students: List<AdminStudentInfo>,
    todayDate: String,
    onDismiss: () -> Unit,
    onSubmit: (Map<String, Any>) -> Unit
) {
    var studentEmail by remember { mutableStateOf(record?.studentEmail ?: "") }
    var selectedClassByAdmin by remember { mutableStateOf("Class 10") }
    var expandedClassMenu by remember { mutableStateOf(false) }

    // Filter students by selectedClassByAdmin
    val filteredStudentsForDialog = remember(students, selectedClassByAdmin) {
        students.filter { it.classSelection.equals(selectedClassByAdmin, ignoreCase = true) }
    }
    
    // Attendance Fields: Strictly locked to today's date
    var status by remember { mutableStateOf(record?.status ?: "Present") }
    
    // Test Result Fields
    var examDate by remember { mutableStateOf(record?.examDate ?: todayDate) }
    var subject by remember { mutableStateOf(record?.subject ?: "Mathematics") }
    var marksObtained by remember { mutableStateOf(record?.marksObtained?.toString() ?: "") }
    var maxMarks by remember { mutableStateOf(record?.maxMarks?.toString() ?: "100") }
    var expandedSubjectMenu by remember { mutableStateOf(false) }
    val subjectsList = remember { listOf("Mathematics", "Physics", "Chemistry", "Biology", "English", "SST", "Computer Science", "EVS", "Hindi") }
    
    // Fees Fields
    var month by remember { mutableStateOf(record?.month ?: "") }
    var totalDue by remember { mutableStateOf(record?.totalDue?.toString() ?: "1500") }
    var amountPaid by remember { mutableStateOf(record?.amountPaid?.toString() ?: "0") }
    var paymentDate by remember { mutableStateOf(record?.paymentDate ?: todayDate) }
    var feeStatus by remember { mutableStateOf(record?.status ?: "Unpaid") }
    var adjustedAmount by remember { mutableStateOf(record?.adjustedAmount?.toString() ?: "0") }
    var remarks by remember { mutableStateOf(record?.remarks ?: "") }
    var paymentMode by remember { mutableStateOf(record?.paymentMode ?: "Cash") }
    var txnRef by remember { mutableStateOf(record?.transactionRefNo ?: "") }

    var expandedStudent by remember { mutableStateOf(false) }

    val classesOptions = com.sparkingpathshala.app.utils.AppConstants.ALL_CLASSES

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (record == null) "Add New $type" else "Edit $type",
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Class selector dropdown to filter students easily
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedClassByAdmin,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filter Student by Class") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expandedClassMenu = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedClassMenu,
                        onDismissRequest = { expandedClassMenu = false }
                    ) {
                        classesOptions.forEach { cls ->
                            DropdownMenuItem(
                                text = { Text(cls) },
                                onClick = {
                                    selectedClassByAdmin = cls
                                    expandedClassMenu = false
                                }
                            )
                        }
                    }
                }

                // Student selection dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = studentEmail,
                        onValueChange = { studentEmail = it },
                        label = { Text("Student Email *") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expandedStudent = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedStudent,
                        onDismissRequest = { expandedStudent = false }
                    ) {
                        val displayStudents = if (filteredStudentsForDialog.isNotEmpty()) filteredStudentsForDialog else students
                        displayStudents.forEach { std ->
                            DropdownMenuItem(
                                text = { Text("${std.name} (${std.email}) - ${std.classSelection}") },
                                onClick = {
                                    studentEmail = std.email
                                    expandedStudent = false
                                }
                            )
                        }
                    }
                }

                when (type) {
                    "attendance" -> {
                        // Date field strictly locked to Today
                        OutlinedTextField(
                            value = todayDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Date (Locked to Today)") },
                            supportingText = { Text("Attendance is recorded strictly for today's date.") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text("Status:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = status == "Present",
                                    onClick = { status = "Present" }
                                )
                                Text("Present")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = status == "Absent",
                                    onClick = { status = "Absent" }
                                )
                                Text("Absent")
                            }
                        }
                    }
                    "test_results" -> {
                        // Subject Selection
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = subject,
                                onValueChange = { subject = it },
                                label = { Text("Subject *") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { expandedSubjectMenu = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = expandedSubjectMenu,
                                onDismissRequest = { expandedSubjectMenu = false }
                            ) {
                                subjectsList.forEach { sub ->
                                    DropdownMenuItem(
                                        text = { Text(sub) },
                                        onClick = {
                                            subject = sub
                                            expandedSubjectMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = examDate,
                            onValueChange = { examDate = it },
                            label = { Text("Exam Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = marksObtained,
                            onValueChange = { marksObtained = it },
                            label = { Text("Marks Obtained *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = maxMarks,
                            onValueChange = { maxMarks = it },
                            label = { Text("Max Marks *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    "fees" -> {
                        OutlinedTextField(
                            value = month,
                            onValueChange = { month = it },
                            label = { Text("Month (e.g. June)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = totalDue,
                            onValueChange = { totalDue = it },
                            label = { Text("Total Due Amount") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = amountPaid,
                            onValueChange = { amountPaid = it },
                            label = { Text("Amount Paid") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = paymentDate,
                            onValueChange = { paymentDate = it },
                            label = { Text("Payment Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = adjustedAmount,
                            onValueChange = { adjustedAmount = it },
                            label = { Text("Adjusted Amount") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Remarks") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = paymentMode,
                            onValueChange = { paymentMode = it },
                            label = { Text("Payment Mode (e.g. Cash, UPI)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = txnRef,
                            onValueChange = { txnRef = it },
                            label = { Text("Transaction Reference #") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text("Fee Status:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = feeStatus == "Paid",
                                    onClick = { feeStatus = "Paid" }
                                )
                                Text("Paid")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = feeStatus == "Unpaid",
                                    onClick = { feeStatus = "Unpaid" }
                                )
                                Text("Unpaid")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                onClick = {
                    if (studentEmail.isBlank()) return@Button
                    val map = mutableMapOf<String, Any>()
                    map["student_email"] = studentEmail.trim()
                    
                    when (type) {
                        "attendance" -> {
                            map["date"] = todayDate
                            map["status"] = status
                        }
                        "test_results" -> {
                            map["subject"] = subject
                            map["exam_date"] = examDate
                            map["marks_obtained"] = marksObtained.toDoubleOrNull() ?: 0.0
                            map["max_marks"] = maxMarks.toDoubleOrNull() ?: 100.0
                        }
                        "fees" -> {
                            map["month"] = month
                            map["total_due"] = totalDue.toDoubleOrNull() ?: 1500.0
                            map["amount_paid"] = amountPaid.toDoubleOrNull() ?: 0.0
                            map["payment_date"] = paymentDate
                            map["status"] = feeStatus
                            map["adjusted_amount"] = adjustedAmount.toDoubleOrNull() ?: 0.0
                            map["remarks"] = remarks
                            map["payment_mode"] = paymentMode
                            if (txnRef.isNotBlank()) map["transaction_reference_number"] = txnRef
                        }
                    }
                    onSubmit(map)
                }
            ) {
                Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
