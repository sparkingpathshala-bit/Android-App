package com.sparkingpathshala.app.ui.homework
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.local.HomeworkEntity
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.ondemand.getUriFileName
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold

val CLASS_OPTIONS = com.sparkingpathshala.app.utils.AppConstants.CLASS_OPTIONS_WITH_ALL
val SUBJECT_OPTIONS = com.sparkingpathshala.app.utils.AppConstants.ALL_SUBJECTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeworkScreen(
    viewModel: DashboardViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val homeworkList by viewModel.homeworkList.collectAsState()
    val isSyncing by viewModel.isHomeworkSyncing.collectAsState()

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(Unit) {
        viewModel.syncHomework()
    }

    var showUploadDialog by remember { mutableStateOf(false) }
    var editingHomework by remember { mutableStateOf<HomeworkEntity?>(null) }
    var selectedClassFilter by remember { mutableStateOf("All") }

    val filteredList = remember(homeworkList, selectedClassFilter) {
        if (selectedClassFilter == "All") homeworkList
        else homeworkList.filter { (it.classLevel ?: "").equals(selectedClassFilter, ignoreCase = true) || (it.classLevel ?: "").equals("All", ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin: Homework Hub", fontWeight = FontWeight.Bold) },
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
                    IconButton(onClick = { showUploadDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Upload Homework", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showUploadDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.UploadFile, contentDescription = null) },
                text = { Text("New Homework") }
            )
        }
    ) { padding ->
        var showPurgeNotice by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimaryBlue,
                    trackColor = PrimaryBlue.copy(alpha = 0.15f)
                )
            }

            // Dismissible Storage Purge Banner
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

            // Class Filter Chips
            SecondaryScrollableTabRow(
                selectedTabIndex = CLASS_OPTIONS.indexOf(selectedClassFilter).coerceAtLeast(0),
                edgePadding = 16.dp,
                divider = {}
            ) {
                CLASS_OPTIONS.forEach { cls ->
                    Tab(
                        selected = selectedClassFilter == cls,
                        onClick = { selectedClassFilter = cls },
                        text = { Text(cls, fontSize = 12.sp) }
                    )
                }
            }

            // List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No homework/assignment uploaded for $selectedClassFilter", color = Color.Gray)
                }
            } else {
                val uniqueFilteredList = remember(filteredList) {
                    filteredList.distinctBy { it.id }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uniqueFilteredList, key = { "admin_hw_${it.id}" }) { item ->
                        val rawTitle = item.title ?: ""
                        val rawDesc = item.description ?: ""
                        val isAssignment = rawTitle.startsWith("[Assignment]", ignoreCase = true) || rawDesc.contains("Assignment", ignoreCase = true)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isAssignment) SecondaryGold.copy(alpha = 0.15f) else PrimaryBlue.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = if (isAssignment) "Assignment" else "Homework",
                                                color = if (isAssignment) SecondaryGold else PrimaryBlue,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color.LightGray.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "${item.classLevel} • ${item.subject}",
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        if (!item.topic.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(item.topic, fontSize = 11.sp, color = SecondaryGold, fontWeight = FontWeight.Medium)
                                        }
                                    }

                                    Row {
                                        IconButton(onClick = { editingHomework = item }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(onClick = {
                                            viewModel.deleteHomework(item.id) { res ->
                                                if (res.isSuccess) {
                                                    Toast.makeText(context, "Deleted homework", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                val cleanTitle = rawTitle.removePrefix("[Assignment]").removePrefix("[Homework]").trim().ifBlank { "Homework Task" }
                                Text(cleanTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp))
                                if (!rawDesc.isNullOrBlank()) {
                                    Text(rawDesc, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp), maxLines = 2)
                                }
                                val rawDue = item.dueDate ?: ""
                                if (rawDue.isNotBlank()) {
                                    Text("Due: $rawDue", fontSize = 11.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Upload & Edit Dialog (Extracted component to eliminate duplication)
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
}
