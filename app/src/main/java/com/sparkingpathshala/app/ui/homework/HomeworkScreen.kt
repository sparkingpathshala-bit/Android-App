package com.sparkingpathshala.app.ui.homework

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.local.HomeworkEntity
import com.sparkingpathshala.app.data.local.isAdmin
import com.sparkingpathshala.app.data.local.isGuest
import com.sparkingpathshala.app.security.SecurityUtils
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold
import com.sparkingpathshala.app.ui.theme.RealisticButton
import com.sparkingpathshala.app.ui.theme.RealisticOutlinedButton

enum class HomeworkSortOption(val title: String) {
    LATEST("Latest First"),
    DUE_DATE("Due Date"),
    TITLE("Title A-Z"),
    SUBJECT("Subject")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun HomeworkScreen(
    viewModel: DashboardViewModel,
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToSignup: () -> Unit = {},
    onNavigateToAdminUpload: () -> Unit = {}
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val homeworkList by viewModel.homeworkList.collectAsState()
    val isSyncing by viewModel.isHomeworkSyncing.collectAsState()

    BackHandler {
        onNavigateBack()
    }

    val isGuest = user.isGuest()
    val isAdmin = user.isAdmin()
    val userClass = user?.classLevel ?: "Class 10"

    LaunchedEffect(Unit) {
        if (!isGuest) {
            viewModel.syncHomework()
        }
    }

    var showUploadDialog by remember { mutableStateOf(false) }
    var editingHomework by remember { mutableStateOf<HomeworkEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("All") }
    var selectedClass by remember { mutableStateOf("All") }
    var filterDownloadedOnly by remember { mutableStateOf(false) }
    var viewingHomework by remember { mutableStateOf<HomeworkEntity?>(null) }
    val classOptions = remember { listOf("All") + (1..12).map { "Class $it" } }
    var sortOption by remember { mutableStateOf(HomeworkSortOption.LATEST) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showGuestGateDialog by remember { mutableStateOf(false) }
    var showPurgeNotice by remember { mutableStateOf(true) }
    var displayedLimit by remember { mutableIntStateOf(10) }

    LaunchedEffect(searchQuery, selectedSubject, selectedClass, sortOption, filterDownloadedOnly) {
        displayedLimit = 10
    }

    val subjects = remember(homeworkList) {
        val nonAllSubjects = homeworkList
            .mapNotNull { it.subject?.trim() }
            .filter { it.isNotBlank() && !it.equals("All", ignoreCase = true) }
            .distinct()
            .sorted()
        listOf("All") + nonAllSubjects
    }

    // Filter & Sort Homework
    val filteredList = remember(homeworkList, searchQuery, selectedSubject, selectedClass, sortOption, isAdmin, isGuest, userClass, filterDownloadedOnly) {
        homeworkList.filter { item ->
            val itemClass = item.classLevel ?: ""
            val itemSubject = item.subject ?: ""
            val itemTitle = item.title ?: ""
            val itemDesc = item.description ?: ""
            val itemTopic = item.topic ?: ""

            val matchesClass = if (isAdmin || isGuest) {
                selectedClass == "All" || itemClass.equals(selectedClass, ignoreCase = true)
            } else {
                itemClass.equals(userClass, ignoreCase = true) || itemClass.equals("All", ignoreCase = true)
            }
            val matchesSubject = selectedSubject == "All" || itemSubject.equals(selectedSubject, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    itemTitle.contains(searchQuery, ignoreCase = true) ||
                    itemDesc.contains(searchQuery, ignoreCase = true) ||
                    itemTopic.contains(searchQuery, ignoreCase = true)
            val matchesDownloaded = !filterDownloadedOnly || item.isDownloaded
            matchesClass && matchesSubject && matchesQuery && matchesDownloaded
        }.let { list ->
            when (sortOption) {
                HomeworkSortOption.LATEST -> list.sortedByDescending { it.createdAt }
                HomeworkSortOption.DUE_DATE -> list.sortedBy { (it.dueDate ?: "").ifBlank { "9999" } }
                HomeworkSortOption.TITLE -> list.sortedBy { (it.title ?: "").lowercase() }
                HomeworkSortOption.SUBJECT -> list.sortedBy { (it.subject ?: "").lowercase() }
            }
        }
    }

    val paginatedList = filteredList.take(displayedLimit)
    val hasMore = filteredList.size > displayedLimit

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Homework & Assignments", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = if (isAdmin) "Manage all classes" else if (isGuest) "Viewing all classes (Guest Mode)" else "Curriculum tasks for $userClass",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.syncHomework() },
                        enabled = !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = PrimaryBlue
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh Homework")
                        }
                    }
                    if (isAdmin) {
                        IconButton(onClick = { showUploadDialog = true }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add Homework", tint = PrimaryBlue)
                        }
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            HomeworkSortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.title) },
                                    onClick = {
                                        sortOption = option
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (sortOption == option) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryBlue)
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    onClick = { showUploadDialog = true },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Homework / Assignment", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { scaffoldPadding ->
        val effectiveBottomPadding = maxOf(
            scaffoldPadding.calculateBottomPadding(),
            paddingValues.calculateBottomPadding()
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
            if (isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimaryBlue,
                    trackColor = PrimaryBlue.copy(alpha = 0.15f)
                )
            }

            // Storage Auto-purge Badge with Close Option
            if (showPurgeNotice) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Homework older than 30 days auto purged",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showPurgeNotice = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // If user is Guest, show locked gate with Signup, Login, and Dismiss
            if (isGuest) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Student Portal Required",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Guest users cannot view or download homework and assignments.\n\nPlease Sign Up for an enrolled student profile or Log In to access your assignments.",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            RealisticButton(
                                text = "Create Student Account",
                                onClick = onNavigateToSignup,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            RealisticOutlinedButton(
                                text = "Sign In with Existing Account",
                                onClick = onNavigateToLogin,
                                borderColor = PrimaryBlue,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Dismiss", color = Color.Gray)
                            }
                        }
                    }
                }
            } else {
                // Class Filter Chips for Admin
                if (isAdmin) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(classOptions, key = { "cls_$it" }) { cls ->
                            FilterChip(
                                selected = selectedClass == cls,
                                onClick = { selectedClass = cls },
                                label = { Text(cls, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search homework by title, topic, or description...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                // Filter Row: Downloaded Toggle + Subject Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(key = "toggle_downloaded") {
                        FilterChip(
                            selected = filterDownloadedOnly,
                            onClick = { filterDownloadedOnly = !filterDownloadedOnly },
                            label = { Text("Downloaded Homework", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (filterDownloadedOnly) Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (filterDownloadedOnly) Color.White else PrimaryBlue
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2E7D32),
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )
                    }
                    if (subjects.size > 1) {
                        items(subjects, key = { "subject_$it" }) { subj ->
                            FilterChip(
                                selected = selectedSubject == subj,
                                onClick = { selectedSubject = subj },
                                label = { Text(subj, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                // Admin Quick Upload Banner
                if (isAdmin) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { showUploadDialog = true },
                        colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Add Homework / Assignment",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PrimaryBlue
                                    )
                                    Text(
                                        text = "Create or attach tasks for any class level",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Homework Items List
                if (paginatedList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AssignmentLate,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (filterDownloadedOnly) "No downloaded homework" else "No homework found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = if (filterDownloadedOnly) "Download homework assignments to view them offline anytime." else if (searchQuery.isNotEmpty()) "Try changing your search or filters" else if (isAdmin) "No assignments created yet. Tap below to upload one." else "No assignments uploaded for $userClass yet",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            if (isAdmin && !filterDownloadedOnly) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { showUploadDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Homework Now", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    val uniquePaginatedList = remember(paginatedList) {
                        paginatedList.distinctBy { it.id }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uniquePaginatedList, key = { "hw_${it.id}" }) { item ->
                            HomeworkCard(
                                item = item,
                                isAdmin = isAdmin,
                                onView = { viewingHomework = item },
                                onEdit = { editingHomework = item },
                                onDownload = {
                                    if (isGuest) {
                                        showGuestGateDialog = true
                                    } else {
                                        if (item.isDownloaded) {
                                            openHomeworkFile(context, item.localPath, item.fileType)
                                        } else {
                                            viewModel.downloadHomework(context, item.id) { res ->
                                                if (res.isSuccess) {
                                                    Toast.makeText(context, "Homework downloaded successfully!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, res.exceptionOrNull()?.message ?: "Download failed", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                },
                                onDelete = {
                                    viewModel.deleteHomework(item.id) { res ->
                                        if (res.isSuccess) {
                                            Toast.makeText(context, "Homework deleted", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }

                        if (hasMore) {
                            item {
                                Button(
                                    onClick = { displayedLimit += 10 },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Text("Load More (${filteredList.size - displayedLimit} remaining)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Guest Mode Gate Dialog
    if (showGuestGateDialog) {
        AlertDialog(
            onDismissRequest = { showGuestGateDialog = false },
            icon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
            },
            title = {
                Text("Student Account Required", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text(
                    "Homework and assignments are exclusive to enrolled students. Please Sign Up or Log In with your student account to access homework tasks and assignments.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RealisticButton(
                        text = "Sign Up",
                        onClick = {
                            showGuestGateDialog = false
                            onNavigateToSignup()
                        },
                        gradientColors = listOf(PrimaryBlue, Color(0xFF1565C0))
                    )
                    RealisticOutlinedButton(
                        text = "Log In",
                        onClick = {
                            showGuestGateDialog = false
                            onNavigateToLogin()
                        },
                        borderColor = PrimaryBlue
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showGuestGateDialog = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

    // Direct Upload & Edit Dialog in HomeworkScreen (Cleanly extracted component)
    if (showUploadDialog || editingHomework != null) {
        HomeworkUploadDialog(
            editingHomework = editingHomework,
            viewModel = viewModel,
            onDismiss = {
                showUploadDialog = false
                editingHomework = null
            },
            onSuccess = { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                showUploadDialog = false
                editingHomework = null
                viewModel.syncHomework()
            }
        )
    }

    // Homework Preview & Details Dialog
    viewingHomework?.let { hw ->
        val rawFileType = (hw.fileType?.ifBlank { null } ?: "PDF").trim()
        val fileIcon = when (rawFileType.uppercase()) {
            "PDF" -> Icons.Default.PictureAsPdf
            "IMAGE" -> Icons.Default.Image
            "DOCX" -> Icons.Default.Description
            else -> Icons.AutoMirrored.Filled.Article
        }
        AlertDialog(
            onDismissRequest = { viewingHomework = null },
            icon = {
                Icon(
                    imageVector = fileIcon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(hw.title?.ifBlank { null } ?: "Homework Assignment", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryBlue.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${(hw.subject?.ifBlank { null } ?: "GENERAL").uppercase()} • ${hw.classLevel ?: "All"}",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (hw.isDownloaded) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Downloaded Offline", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (!hw.topic.isNullOrBlank() || !hw.batch.isNullOrBlank()) {
                        Text(
                            text = listOfNotNull(
                                hw.topic?.ifBlank { null }?.let { "Topic: $it" },
                                hw.batch?.ifBlank { null }?.let { "Batch: $it" }
                            ).joinToString(" • "),
                            fontSize = 12.sp,
                            color = SecondaryGold,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    val rawDue = hw.dueDate ?: ""
                    if (rawDue.isNotBlank() || hw.maxMarks > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (rawDue.isNotBlank()) {
                                Text("Due Date: $rawDue", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
                            }
                            if (hw.maxMarks > 0) {
                                Text("Max Marks: ${hw.maxMarks}", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text("Instructions / Assignment Task:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = if (!hw.description.isNullOrBlank()) hw.description else "No additional text instructions. Please download or open attachment to solve.",
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (hw.isDownloaded) {
                        Button(
                            onClick = {
                                openHomeworkFile(context, hw.localPath, rawFileType)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open File Offline")
                        }
                    } else {
                        Button(
                            onClick = {
                                if (isGuest) {
                                    showGuestGateDialog = true
                                } else {
                                    viewModel.downloadHomework(context, hw.id) { res ->
                                        if (res.isSuccess) {
                                            Toast.makeText(context, "Downloaded successfully!", Toast.LENGTH_SHORT).show()
                                            viewingHomework = hw.copy(isDownloaded = true)
                                        } else {
                                            Toast.makeText(context, res.exceptionOrNull()?.message ?: "Download failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewingHomework = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun HomeworkCard(
    item: HomeworkEntity,
    isAdmin: Boolean,
    onView: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    val rawSubject = (item.subject?.ifBlank { null } ?: "GENERAL").trim()
    val rawTitle = item.title?.ifBlank { null } ?: "Homework Assignment"
    val rawDesc = item.description ?: ""
    val rawDue = item.dueDate ?: ""
    val rawFileType = (item.fileType?.ifBlank { null } ?: "PDF").trim()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Subject Badge, Class & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryBlue.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = rawSubject.uppercase(),
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = SecurityUtils.formatIndianShortDate(item.createdAt),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = rawTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Topic / Batch pills
            if (!item.topic.isNullOrBlank() || !item.batch.isNullOrBlank()) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!item.topic.isNullOrBlank()) {
                        Text(
                            text = "Topic: ${item.topic}",
                            fontSize = 11.sp,
                            color = SecondaryGold,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (!item.batch.isNullOrBlank()) {
                        Text(
                            text = "• Batch: ${item.batch}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Description
            if (rawDesc.isNotBlank()) {
                Text(
                    text = rawDesc,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 6.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Due Date & Max Marks
            if (rawDue.isNotBlank() || item.maxMarks > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (rawDue.isNotBlank()) {
                        Text(
                            text = "Due: $rawDue",
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (item.maxMarks > 0) {
                        Text(
                            text = "Max Marks: ${item.maxMarks}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Bottom Actions: File indicator & Download/Delete/View
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val fileIcon = when (rawFileType.uppercase()) {
                        "PDF" -> Icons.Default.PictureAsPdf
                        "IMAGE" -> Icons.Default.Image
                        "DOCX" -> Icons.Default.Description
                        else -> Icons.AutoMirrored.Filled.Article
                    }
                    Icon(fileIcon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = rawFileType.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    if (item.isDownloaded) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.CheckCircle, contentDescription = "Saved Offline", tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                        Text("Saved", fontSize = 11.sp, color = Color(0xFF2E7D32), modifier = Modifier.padding(start = 2.dp))
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isAdmin) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryBlue)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                        }
                    }

                    OutlinedButton(
                        onClick = onView,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDownload,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.isDownloaded) Color(0xFF2E7D32) else PrimaryBlue
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isDownloaded) Icons.Default.FolderOpen else Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (item.isDownloaded) "Open File" else "Download", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

fun openHomeworkFile(context: Context, localPath: String?, fileType: String?) {
    if (localPath.isNullOrBlank()) {
        Toast.makeText(context, "File not downloaded yet. Please tap Download.", Toast.LENGTH_SHORT).show()
        return
    }
    val file = java.io.File(localPath)
    if (!file.exists()) {
        Toast.makeText(context, "Local file not found. Please re-download.", Toast.LENGTH_SHORT).show()
        return
    }
    val safeType = (fileType ?: "PDF").trim().uppercase()
    val mimeType = when (safeType) {
        "PDF" -> "application/pdf"
        "IMAGE", "JPG", "PNG", "JPEG" -> "image/*"
        "DOCX", "DOC" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        else -> "text/plain"
    }
    try {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "com.sparkingpathshala.app.fileprovider",
            file
        )
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val chooser = android.content.Intent.createChooser(intent, "Open Assignment with")
        chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open $safeType file: ${e.localizedMessage ?: "No viewer found"}", Toast.LENGTH_SHORT).show()
    }
}
