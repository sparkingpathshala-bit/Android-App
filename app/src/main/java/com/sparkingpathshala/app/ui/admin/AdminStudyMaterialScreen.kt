package com.sparkingpathshala.app.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import kotlinx.coroutines.launch
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.common.EmptyStateView
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStudyMaterialScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var activeTab by remember { mutableStateOf("upload") } // "upload" or "manage"

    LaunchedEffect(Unit) {
        viewModel.syncStudyMaterials()
    }

    LaunchedEffect(activeTab) {
        if (activeTab == "manage") {
            viewModel.syncStudyMaterials()
        }
    }

    // Form fields
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("question-bank") }
    var classSelection by remember { mutableStateOf("Class 10") }
    var subject by remember { mutableStateOf("Physics") }
    var batch by remember { mutableStateOf("") }
    var allowedEmailsInput by remember { mutableStateOf("") }
    var studentsList by remember { mutableStateOf<List<com.sparkingpathshala.app.data.remote.AdminStudentInfo>>(emptyList()) }
    var expandedStudentDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getAdminStudents { result ->
            studentsList = result.getOrElse { emptyList() }
        }
    }

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedFileSizeStr by remember { mutableStateOf("") }
    var base64Data by remember { mutableStateOf("") }

    var isUploading by remember { mutableStateOf(false) }

    val categories = com.sparkingpathshala.app.utils.AppConstants.MATERIAL_CATEGORIES
    val classes = com.sparkingpathshala.app.utils.AppConstants.CLASS_OPTIONS_WITH_ALL
    val subjectsList = com.sparkingpathshala.app.utils.AppConstants.ALL_SUBJECTS

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedClass by remember { mutableStateOf(false) }
    var expandedSubject by remember { mutableStateOf(false) }

    // Helper to get file details
    fun getFileDetails(uri: Uri) {
        selectedFileName = com.sparkingpathshala.app.utils.FileUtils.getUriFileName(context, uri)
        val sizeMb = com.sparkingpathshala.app.utils.FileUtils.getFileSizeMB(context, uri)
        selectedFileSizeStr = String.format(java.util.Locale.US, "%.2f MB", sizeMb)
        
        scope.launch {
            val res = com.sparkingpathshala.app.utils.FileUtils.readUriToBase64(context, uri, "data:application/pdf;base64,")
            res.onSuccess { pair -> base64Data = pair.second }
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            getFileDetails(uri)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Digital Library", fontWeight = FontWeight.Bold) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // Header Row with title and switch buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Digital Library Management",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // UPLOAD MATERIAL pill
                        Button(
                            onClick = { activeTab = "upload" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeTab == "upload") SecondaryGold else Color.Transparent,
                                contentColor = if (activeTab == "upload") Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = if (activeTab == "upload") null else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text("UPLOAD MATERIAL", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // MANAGE MATERIALS pill
                        Button(
                            onClick = { activeTab = "manage" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeTab == "manage") SecondaryGold else Color.Transparent,
                                contentColor = if (activeTab == "manage") Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = if (activeTab == "manage") null else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text("MANAGE MATERIALS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                HorizontalDivider(color = Color.Gray.copy(alpha = 0.15f), modifier = Modifier.padding(bottom = 16.dp))

                if (activeTab == "upload") {
                    // Upload form (scrollable)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title Field
                        Column {
                            RequiredLabel("Title")
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("e.g. Physics Chapter 1 Notes", color = Color.Gray, fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF16161B),
                                    unfocusedContainerColor = Color(0xFF16161B),
                                    focusedBorderColor = SecondaryGold,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            )
                        }

                        // Category & Class Field Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                RequiredLabel("Category")
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = categories.find { it.first == category }?.second ?: "Select Category",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold) },
                                        modifier = Modifier.fillMaxWidth().clickable { expandedCategory = true },
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledContainerColor = Color(0xFF16161B),
                                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f),
                                            disabledTrailingIconColor = SecondaryGold
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { expandedCategory = true })
                                    DropdownMenu(
                                        expanded = expandedCategory,
                                        onDismissRequest = { expandedCategory = false }
                                    ) {
                                        categories.forEach { (catId, catLabel) ->
                                            DropdownMenuItem(
                                                text = { Text(catLabel) },
                                                onClick = {
                                                    category = catId
                                                    expandedCategory = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                RequiredLabel("Class")
                                Spacer(modifier = Modifier.height(6.dp))
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
                                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f),
                                            disabledTrailingIconColor = SecondaryGold
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { expandedClass = true })
                                    DropdownMenu(
                                        expanded = expandedClass,
                                        onDismissRequest = { expandedClass = false }
                                    ) {
                                        classes.forEach { cls ->
                                            DropdownMenuItem(
                                                text = { Text(cls) },
                                                onClick = {
                                                    classSelection = cls
                                                    expandedClass = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Subject Selection
                        Column {
                            RequiredLabel("Subject")
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = if (subject.isEmpty()) "Select Subject" else subject,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color.White,
                                        disabledContainerColor = Color(0xFF16161B),
                                        disabledBorderColor = Color.Gray.copy(alpha = 0.3f),
                                        disabledTrailingIconColor = SecondaryGold
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(modifier = Modifier.matchParentSize().clickable { expandedSubject = true })
                                DropdownMenu(
                                    expanded = expandedSubject,
                                    onDismissRequest = { expandedSubject = false },
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                ) {
                                    subjectsList.forEach { sub ->
                                        DropdownMenuItem(
                                            text = { Text(sub) },
                                            onClick = {
                                                subject = sub
                                                expandedSubject = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
 
                        // Batch Field (Optional)
                        Column {
                            Text("Batch / Section (Optional)", color = Color.LightGray, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = batch,
                                onValueChange = { batch = it },
                                placeholder = { Text("e.g. Batch A / Morning", color = Color.Gray, fontSize = 13.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = SecondaryGold,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                    focusedContainerColor = Color(0xFF16161B),
                                    unfocusedContainerColor = Color(0xFF16161B)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        if (category == "premium-material") {
                            Column {
                                Text("Choose Student of Selected Class (Optional)", color = Color.LightGray, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                val filteredStudents = remember(studentsList, classSelection) {
                                    if (classSelection == "All") studentsList else studentsList.filter { it.classSelection.equals(classSelection, ignoreCase = true) }
                                }

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = "Select student from $classSelection",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold) },
                                        modifier = Modifier.fillMaxWidth().clickable { expandedStudentDropdown = true },
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledContainerColor = Color(0xFF16161B),
                                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f),
                                            disabledTrailingIconColor = SecondaryGold
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { expandedStudentDropdown = true })
                                    DropdownMenu(
                                        expanded = expandedStudentDropdown,
                                        onDismissRequest = { expandedStudentDropdown = false }
                                    ) {
                                        if (filteredStudents.isEmpty()) {
                                            DropdownMenuItem(
                                                text = { Text("No students found in $classSelection") },
                                                onClick = { expandedStudentDropdown = false }
                                            )
                                        } else {
                                            filteredStudents.forEach { std ->
                                                DropdownMenuItem(
                                                    text = { Text("${std.name} (${std.email})") },
                                                    onClick = {
                                                        allowedEmailsInput = if (allowedEmailsInput.isBlank()) std.email else "$allowedEmailsInput, ${std.email}"
                                                        expandedStudentDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                RequiredLabel("Allowed Student Emails (Optional - Leave blank for all students in class)")
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = allowedEmailsInput,
                                    onValueChange = { allowedEmailsInput = it },
                                    placeholder = { Text("e.g. student1@gmail.com (Leave blank for whole class)", color = Color.Gray, fontSize = 13.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = SecondaryGold,
                                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                        focusedContainerColor = Color(0xFF16161B),
                                        unfocusedContainerColor = Color(0xFF16161B)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
 
                        // File Selector Card
                        Column {
                            RequiredLabel("File (PDF/Image)")
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { filePickerLauncher.launch(arrayOf("application/pdf", "image/*")) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF16161B)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF2C2C35),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "Browse...",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = if (selectedFileUri == null) "No file selected." else "$selectedFileName ($selectedFileSizeStr)",
                                        color = if (selectedFileUri == null) Color.Gray else Color.White,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (selectedFileUri != null) {
                                        IconButton(onClick = {
                                            selectedFileUri = null
                                            base64Data = ""
                                            selectedFileName = ""
                                        }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear file", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isUploading) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = SecondaryGold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (title.isBlank()) {
                                        scope.launch { snackbarHostState.showSnackbar("Title is mandatory.") }
                                        return@Button
                                    }
                                    if (subject.isBlank()) {
                                        scope.launch { snackbarHostState.showSnackbar("Subject is mandatory.") }
                                        return@Button
                                    }
                                    if (category == "premium-material" && classSelection == "All") {
                                        scope.launch { snackbarHostState.showSnackbar("Please select a specific Class (Class 1 to 12) for Premium Material.") }
                                        return@Button
                                    }
                                    if (base64Data.isBlank()) {
                                        scope.launch { snackbarHostState.showSnackbar("Please attach a file (PDF or image).") }
                                        return@Button
                                    }
                                    isUploading = true
                                    viewModel.uploadStudyMaterial(
                                        title = title.trim(),
                                        category = category,
                                        classSelection = classSelection,
                                        subject = subject.trim(),
                                        fileName = selectedFileName,
                                        fileSize = selectedFileSizeStr,
                                        fileData = base64Data,
                                        allowedEmails = if (category == "premium-material") allowedEmailsInput.trim() else null
                                    ) { result ->
                                        isUploading = false
                                        if (result.isSuccess) {
                                            scope.launch { snackbarHostState.showSnackbar("Material uploaded successfully!") }
                                            title = ""
                                            selectedFileUri = null
                                            base64Data = ""
                                            selectedFileName = ""
                                            allowedEmailsInput = ""
                                            viewModel.syncAll()
                                        } else {
                                            scope.launch { snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Upload failed") }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SecondaryGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("UPLOAD CONTENT", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                } else {
                    // Manage view with Filtering, Sorting, and Pagination
                    val allMaterials by viewModel.studyMaterials.collectAsState()
                    var manageSearchQuery by remember { mutableStateOf("") }
                    var selectedManageCategory by remember { mutableStateOf("all") }
                    var selectedManageClass by remember { mutableStateOf("All") }
                    var selectedSortOption by remember { mutableStateOf("Newest First") }
                    var sortExpanded by remember { mutableStateOf(false) }
                    var currentPage by remember { mutableIntStateOf(0) }
                    val pageSize = 10

                    var materialToEdit by remember { mutableStateOf<com.sparkingpathshala.app.data.local.StudyMaterialEntity?>(null) }
                    var materialToDelete by remember { mutableStateOf<com.sparkingpathshala.app.data.local.StudyMaterialEntity?>(null) }

                    val sortOptions = listOf("Newest First", "Oldest First", "Title (A-Z)", "Title (Z-A)", "Class (Ascending)", "Subject (A-Z)")

                    val manageCategoryFilters = listOf(
                        "all" to "All",
                        "premium-material" to "Premium Material",
                        "notes" to "Notes",
                        "guides" to "Guides",
                        "question-bank" to "Question Bank",
                        "test-series" to "Test Series"
                    )

                    val filteredAndSortedMaterials = remember(allMaterials, manageSearchQuery, selectedManageCategory, selectedManageClass, selectedSortOption) {
                        allMaterials.filter { item ->
                            val matchesCategory = selectedManageCategory == "all" || item.category.equals(selectedManageCategory, ignoreCase = true)
                            val matchesClass = selectedManageClass == "All" || item.classLevel.equals(selectedManageClass, ignoreCase = true)
                            val matchesQuery = manageSearchQuery.isBlank() ||
                                    item.title.contains(manageSearchQuery, ignoreCase = true) ||
                                    item.subject.contains(manageSearchQuery, ignoreCase = true) ||
                                    item.classLevel.contains(manageSearchQuery, ignoreCase = true) ||
                                    (item.allowedEmails?.contains(manageSearchQuery, ignoreCase = true) == true)
                            matchesCategory && matchesClass && matchesQuery
                        }.let { list ->
                            when (selectedSortOption) {
                                "Oldest First" -> list.reversed()
                                "Title (A-Z)" -> list.sortedBy { it.title.lowercase() }
                                "Title (Z-A)" -> list.sortedByDescending { it.title.lowercase() }
                                "Class (Ascending)" -> list.sortedBy { it.classLevel }
                                "Subject (A-Z)" -> list.sortedBy { it.subject.lowercase() }
                                else -> list // Default Newest / standard order
                            }
                        }
                    }

                    val totalPages = maxOf(1, (filteredAndSortedMaterials.size + pageSize - 1) / pageSize)
                    val safePage = currentPage.coerceIn(0, totalPages - 1)
                    val paginatedMaterials = filteredAndSortedMaterials.drop(safePage * pageSize).take(pageSize)

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Category Filter Chips
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(manageCategoryFilters, key = { it.first }) { (catKey, catName) ->
                                val isSelected = selectedManageCategory == catKey
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedManageCategory = catKey
                                        currentPage = 0
                                    },
                                    label = { Text(catName, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (catKey == "premium-material") SecondaryGold else PrimaryBlue,
                                        selectedLabelColor = if (catKey == "premium-material") Color.Black else Color.White,
                                        containerColor = Color(0xFF16161B),
                                        labelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (catKey == "premium-material") SecondaryGold.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        }

                        // Class Filter Chips
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(classes, key = { it }) { cls ->
                                val isSelected = selectedManageClass == cls
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedManageClass = cls
                                        currentPage = 0
                                    },
                                    label = { Text(cls, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SecondaryGold,
                                        selectedLabelColor = Color.Black,
                                        containerColor = Color(0xFF16161B),
                                        labelColor = Color.LightGray
                                    )
                                )
                            }
                        }

                        // Search & Sorting Controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = manageSearchQuery,
                                onValueChange = {
                                    manageSearchQuery = it
                                    currentPage = 0
                                },
                                placeholder = { Text("Search materials...", color = Color.Gray, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)) },
                                trailingIcon = {
                                    if (manageSearchQuery.isNotEmpty()) {
                                        IconButton(onClick = { manageSearchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )

                            Box {
                                OutlinedButton(
                                    onClick = { sortExpanded = true },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.height(46.dp)
                                ) {
                                    Icon(Icons.Default.FilterList, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(selectedSortOption, fontSize = 11.sp, maxLines = 1)
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
                                                currentPage = 0
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        if (filteredAndSortedMaterials.isEmpty()) {
                            EmptyStateView(
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                title = "No Materials Found",
                                subtitle = "No study materials match current search or filters.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(24.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 12.dp)
                            ) {
                                items(paginatedMaterials, key = { it.id }, contentType = { "admin_material" }) { material ->
                                    val isPremium = material.category.equals("premium-material", ignoreCase = true)
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isPremium) SecondaryGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = if (isPremium) SecondaryGold else Color(0xFF2C2C35)
                                                    ) {
                                                        Text(
                                                            text = if (isPremium) "PREMIUM" else material.category.uppercase(),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isPremium) Color.Black else SecondaryGold,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    val isHidden = material.status == "disabled"
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = if (isHidden) Color(0xFF442525) else Color(0xFF1E3D2F)
                                                    ) {
                                                        Text(
                                                            text = if (isHidden) "HIDDEN" else "ACTIVE",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isHidden) Color(0xFFF87171) else Color(0xFF4ADE80),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(6.dp))

                                                Text(
                                                    text = material.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Text(
                                                    text = "Class: ${material.classLevel}  •  Subject: ${material.subject}",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                                if (isPremium && !material.allowedEmails.isNullOrBlank()) {
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Text(
                                                        text = "Student Access: ${material.allowedEmails}",
                                                        fontSize = 11.sp,
                                                        color = SecondaryGold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }

                                            // Action Buttons: Edit, Toggle Visibility, Delete
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                // Edit Button
                                                IconButton(
                                                    onClick = { materialToEdit = material }
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Material", tint = SecondaryGold, modifier = Modifier.size(20.dp))
                                                }

                                                // Toggle Visibility
                                                val isHidden = material.status == "disabled"
                                                IconButton(
                                                    onClick = {
                                                        val newStatus = if (isHidden) "active" else "disabled"
                                                        viewModel.updateLibraryResource(
                                                            id = material.id,
                                                            title = material.title,
                                                            category = material.category,
                                                            classSelection = material.classLevel,
                                                            subject = material.subject,
                                                            status = newStatus,
                                                            allowedEmails = material.allowedEmails
                                                        ) { result ->
                                                            if (result.isSuccess) {
                                                                val msg = if (isHidden) "Material set to Active!" else "Material is now Hidden!"
                                                                scope.launch { snackbarHostState.showSnackbar(msg) }
                                                                viewModel.syncAll()
                                                            } else {
                                                                scope.launch { snackbarHostState.showSnackbar("Failed to update status") }
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = if (isHidden) "Show Material" else "Hide Material",
                                                        tint = if (isHidden) Color.Gray else Color(0xFF4ADE80),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }

                                                // Delete Button
                                                IconButton(
                                                    onClick = { materialToDelete = material }
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Material", tint = Color(0xFFF87171), modifier = Modifier.size(20.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Pagination Controls Footer
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { if (safePage > 0) currentPage-- },
                                    enabled = safePage > 0,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Previous")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Prev", fontSize = 12.sp)
                                }

                                Text(
                                    text = "Page ${safePage + 1} of $totalPages (${filteredAndSortedMaterials.size} items)",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )

                                OutlinedButton(
                                    onClick = { if (safePage < totalPages - 1) currentPage++ },
                                    enabled = safePage < totalPages - 1,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Next", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next")
                                }
                            }
                        }
                    }

                    // Edit Material Dialog
                    materialToEdit?.let { item ->
                        EditMaterialDialog(
                            material = item,
                            categories = categories,
                            classes = classes,
                            subjectsList = subjectsList,
                            studentsList = studentsList,
                            onDismiss = { materialToEdit = null },
                            onSave = { updatedTitle, updatedCategory, updatedClass, updatedSubject, updatedStatus, updatedAllowedEmails ->
                                viewModel.updateLibraryResource(
                                    id = item.id,
                                    title = updatedTitle,
                                    category = updatedCategory,
                                    classSelection = updatedClass,
                                    subject = updatedSubject,
                                    status = updatedStatus,
                                    allowedEmails = if (updatedCategory == "premium-material") updatedAllowedEmails else null
                                ) { res ->
                                    materialToEdit = null
                                    if (res.isSuccess) {
                                        scope.launch { snackbarHostState.showSnackbar("Material updated successfully!") }
                                        viewModel.syncAll()
                                    } else {
                                        scope.launch { snackbarHostState.showSnackbar(res.exceptionOrNull()?.message ?: "Update failed") }
                                    }
                                }
                            }
                        )
                    }

                    // Delete Confirmation Dialog
                    materialToDelete?.let { item ->
                        AlertDialog(
                            onDismissRequest = { materialToDelete = null },
                            title = { Text("Delete Study Material", fontWeight = FontWeight.Bold) },
                            text = { Text("Are you sure you want to completely delete \"${item.title}\"? This action cannot be undone.") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val id = item.id
                                        materialToDelete = null
                                        viewModel.deleteResource(id) { result ->
                                            if (result.isSuccess) {
                                                scope.launch { snackbarHostState.showSnackbar("Material deleted successfully!") }
                                                viewModel.syncAll()
                                            } else {
                                                scope.launch { snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Failed to delete") }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                                ) {
                                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                OutlinedButton(onClick = { materialToDelete = null }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditMaterialDialog(
    material: com.sparkingpathshala.app.data.local.StudyMaterialEntity,
    categories: List<Pair<String, String>>,
    classes: List<String>,
    subjectsList: List<String>,
    studentsList: List<com.sparkingpathshala.app.data.remote.AdminStudentInfo>,
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, classSelection: String, subject: String, status: String, allowedEmails: String?) -> Unit
) {
    var editTitle by remember { mutableStateOf(material.title) }
    var editCategory by remember { mutableStateOf(material.category) }
    var editClass by remember { mutableStateOf(material.classLevel) }
    var editSubject by remember { mutableStateOf(material.subject) }
    var editStatus by remember { mutableStateOf(material.status) }
    var editAllowedEmails by remember { mutableStateOf(material.allowedEmails ?: "") }

    var expandedCat by remember { mutableStateOf(false) }
    var expandedCls by remember { mutableStateOf(false) }
    var expandedSub by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }
    var expandedStudentPicker by remember { mutableStateOf(false) }

    val filteredStudentsForEdit = remember(studentsList, editClass) {
        if (editClass == "All") studentsList else studentsList.filter { it.classSelection.equals(editClass, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Study Material", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Column {
                    RequiredLabel("Title")
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Category & Class Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        RequiredLabel("Category")
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = categories.find { it.first == editCategory }?.second ?: editCategory,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().clickable { expandedCat = true },
                                enabled = false
                            )
                            Box(modifier = Modifier.matchParentSize().clickable { expandedCat = true })
                            DropdownMenu(
                                expanded = expandedCat,
                                onDismissRequest = { expandedCat = false }
                            ) {
                                categories.forEach { (catId, catLabel) ->
                                    DropdownMenuItem(
                                        text = { Text(catLabel) },
                                        onClick = {
                                            editCategory = catId
                                            expandedCat = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        RequiredLabel("Class")
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = editClass,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().clickable { expandedCls = true },
                                enabled = false
                            )
                            Box(modifier = Modifier.matchParentSize().clickable { expandedCls = true })
                            DropdownMenu(
                                expanded = expandedCls,
                                onDismissRequest = { expandedCls = false }
                            ) {
                                classes.forEach { cls ->
                                    DropdownMenuItem(
                                        text = { Text(cls) },
                                        onClick = {
                                            editClass = cls
                                            expandedCls = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Subject
                Column {
                    RequiredLabel("Subject")
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = editSubject,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().clickable { expandedSub = true },
                            enabled = false
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { expandedSub = true })
                        DropdownMenu(
                            expanded = expandedSub,
                            onDismissRequest = { expandedSub = false }
                        ) {
                            subjectsList.forEach { sub ->
                                DropdownMenuItem(
                                    text = { Text(sub) },
                                    onClick = {
                                        editSubject = sub
                                        expandedSub = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Status Selection
                Column {
                    Text("Visibility Status", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (editStatus == "disabled") "Hidden (Disabled)" else "Visible (Active)",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().clickable { expandedStatus = true },
                            enabled = false
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { expandedStatus = true })
                        DropdownMenu(
                            expanded = expandedStatus,
                            onDismissRequest = { expandedStatus = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Visible (Active)") },
                                onClick = {
                                    editStatus = "active"
                                    expandedStatus = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Hidden (Disabled)") },
                                onClick = {
                                    editStatus = "disabled"
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }

                // Premium Material Student Picker
                if (editCategory == "premium-material") {
                    Column {
                        Text("Target Student of $editClass (Optional)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = "Select student to add email...",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().clickable { expandedStudentPicker = true },
                                enabled = false
                            )
                            Box(modifier = Modifier.matchParentSize().clickable { expandedStudentPicker = true })
                            DropdownMenu(
                                expanded = expandedStudentPicker,
                                onDismissRequest = { expandedStudentPicker = false }
                            ) {
                                if (filteredStudentsForEdit.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No students found in $editClass") },
                                        onClick = { expandedStudentPicker = false }
                                    )
                                } else {
                                    filteredStudentsForEdit.forEach { std ->
                                        DropdownMenuItem(
                                            text = { Text("${std.name} (${std.email})") },
                                            onClick = {
                                                editAllowedEmails = if (editAllowedEmails.isBlank()) std.email else "$editAllowedEmails, ${std.email}"
                                                expandedStudentPicker = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Allowed Emails (Leave blank for whole class)", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editAllowedEmails,
                            onValueChange = { editAllowedEmails = it },
                            placeholder = { Text("e.g. student@gmail.com") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (editTitle.isNotBlank() && editSubject.isNotBlank()) {
                        onSave(editTitle.trim(), editCategory, editClass, editSubject.trim(), editStatus, editAllowedEmails.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryGold, contentColor = Color.Black)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RequiredLabel(text: String) {
    com.sparkingpathshala.app.ui.common.RequiredFieldLabel(label = text)
}
