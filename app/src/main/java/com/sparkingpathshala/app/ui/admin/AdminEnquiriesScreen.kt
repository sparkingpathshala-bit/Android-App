package com.sparkingpathshala.app.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
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
import com.sparkingpathshala.app.data.remote.EnquiryInfo
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEnquiriesScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var enquiriesList by remember { mutableStateOf<List<EnquiryInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedEnquiry by remember { mutableStateOf<EnquiryInfo?>(null) }

    fun loadEnquiries(silent: Boolean = false) {
        if (!silent && enquiriesList.isEmpty()) {
            isLoading = true
        } else {
            isRefreshing = true
        }
        viewModel.getAdminEnquiries { result ->
            isLoading = false
            isRefreshing = false
            result.onSuccess { list ->
                enquiriesList = list
            }.onFailure { exception ->
                Toast.makeText(context, "Failed to load enquiries: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        // Initial load
        loadEnquiries(silent = enquiriesList.isNotEmpty())
        
        // Connect real-time Server-Sent Events stream
        val call = viewModel.listenToEnquiriesStream {
            // Trigger silent reload on backend updates
            loadEnquiries(silent = true)
        }
        
        try {
            kotlinx.coroutines.awaitCancellation()
        } finally {
            call?.cancel()
        }
    }

    var displayedLimit by remember { mutableIntStateOf(15) }

    LaunchedEffect(searchQuery, selectedStatusFilter) {
        displayedLimit = 15
    }

    val filteredEnquiries = remember(enquiriesList, searchQuery, selectedStatusFilter) {
        var result = enquiriesList
        if (searchQuery.isNotBlank()) {
            result = result.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.mobile.contains(searchQuery)
            }
        }
        if (selectedStatusFilter != "All") {
            result = result.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
        }
        result
    }

    val paginatedEnquiries = remember(filteredEnquiries, displayedLimit) {
        filteredEnquiries.take(displayedLimit)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Enquiries", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { loadEnquiries(silent = false) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Enquiries", tint = Color.White)
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
            Column(modifier = Modifier.fillMaxSize()) {
                if (isRefreshing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, email, or mobile...") },
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
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Status filter tabs
                val filterOptions = listOf("All", "Pending", "Contacted", "Closed", "Ignored")
                PrimaryScrollableTabRow(
                    selectedTabIndex = filterOptions.indexOf(selectedStatusFilter).coerceAtLeast(0),
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryBlue
                ) {
                    filterOptions.forEach { status ->
                        Tab(
                            selected = selectedStatusFilter == status,
                            onClick = { selectedStatusFilter = status },
                            text = { Text(status, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else if (filteredEnquiries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No enquiries found.", color = Color.Gray)
                    }
                } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(paginatedEnquiries, key = { it.id }, contentType = { "enquiry" }) { enq ->
                                EnquiryItemCard(
                                    enquiry = enq,
                                    onClick = {
                                        selectedEnquiry = enq
                                        showEditDialog = true
                                    },
                                    onDelete = {
                                        viewModel.deleteAdminEnquiry(enq.id) { result ->
                                            if (result.isSuccess) {
                                                Toast.makeText(context, "Enquiry deleted successfully.", Toast.LENGTH_SHORT).show()
                                                loadEnquiries()
                                            } else {
                                                Toast.makeText(context, "Delete failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                )
                            }

                            if (filteredEnquiries.size > displayedLimit) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        OutlinedButton(
                                            onClick = { displayedLimit += 15 },
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Load More (${paginatedEnquiries.size} of ${filteredEnquiries.size})", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    val currentEnquiry = selectedEnquiry
    if (showEditDialog && currentEnquiry != null) {
        EnquiryEditDialog(
            enquiry = currentEnquiry,
            onDismiss = {
                showEditDialog = false
                selectedEnquiry = null
            },
            onSubmit = { status, remarks ->
                showEditDialog = false
                val payload = mapOf(
                    "status" to status,
                    "remarks" to remarks
                )
                viewModel.updateAdminEnquiry(currentEnquiry.id, payload) { result ->
                    selectedEnquiry = null
                    if (result.isSuccess) {
                        Toast.makeText(context, "Enquiry updated successfully.", Toast.LENGTH_SHORT).show()
                        loadEnquiries()
                    } else {
                        Toast.makeText(context, "Update failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

@Composable
fun EnquiryItemCard(
    enquiry: EnquiryInfo,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (enquiry.status.uppercase()) {
        "PENDING", "NEW" -> Color(0xFF8E8E93)
        "CONTACTED", "ADMITTED" -> Color(0xFF1C1C1E)
        "CLOSED" -> Color(0xFF007AFF)
        "IGNORED", "REJECTED" -> Color(0xFFFFFFFF)
        else -> Color.Gray
    }

    val statusBg = when (enquiry.status.uppercase()) {
        "PENDING", "NEW" -> Color(0xFFF2F2F7)
        "CONTACTED", "ADMITTED" -> Color(0xFFE5E5EA)
        "CLOSED" -> Color(0xFFE5F0FF)
        "IGNORED", "REJECTED" -> Color(0xFF000000)
        else -> Color(0xFFE5E5EA)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row for Name, Date, and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = enquiry.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = enquiry.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitles: Email & Mobile
            Text(
                text = "${enquiry.email} | ${enquiry.mobile}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tags for Class and Subject
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = enquiry.classSelection,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (!enquiry.subject.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Text(
                            text = enquiry.subject,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            // Message Body
            Text(
                text = enquiry.message,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (!enquiry.remarks.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Remarks: ${enquiry.remarks}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onClick) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Update", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnquiryEditDialog(
    enquiry: EnquiryInfo,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var status by remember { mutableStateOf(enquiry.status) }
    var remarks by remember { mutableStateOf(enquiry.remarks ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update Enquiry Status",
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
                // Info Section
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Student: ${enquiry.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Enquiry Message: ${enquiry.message}", fontSize = 12.sp, color = Color.DarkGray)
                    }
                }

                // Status Selection RadioButtons
                Text("Select Action/Status:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                val statusOptions = listOf("Pending", "Contacted", "Closed", "Ignored")
                statusOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { status = option }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = status == option,
                            onClick = { status = option }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Remarks TextField
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Follow-up Remarks / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(status, remarks) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
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
