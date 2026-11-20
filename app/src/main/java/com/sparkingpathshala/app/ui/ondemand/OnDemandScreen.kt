package com.sparkingpathshala.app.ui.ondemand

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.sparkingpathshala.app.data.remote.DoubtResponse
import com.sparkingpathshala.app.data.remote.DoubtReplyResponse
import com.sparkingpathshala.app.data.local.isAdmin
import com.sparkingpathshala.app.data.local.isGuest
import com.sparkingpathshala.app.security.SecurityUtils
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.*

sealed interface DoubtView {
    object List : DoubtView
    object Create : DoubtView
    data class Detail(val doubt: DoubtResponse) : DoubtView
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnDemandScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToSignup: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser by viewModel.user.collectAsState()
    val isGuest = currentUser.isGuest()
    var showGuestDialog by remember { mutableStateOf(false) }
    
    var currentView by remember { mutableStateOf<DoubtView>(DoubtView.List) }

    if (showGuestDialog) {
        AlertDialog(
            onDismissRequest = { showGuestDialog = false },
            icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(32.dp)) },
            title = { Text("Sign Up or Log In Required", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryBlue) },
            text = {
                Text(
                    "Guest users cannot submit doubt tickets. Please create an account or log in to ask doubts directly to our expert faculty.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                RealisticButton(
                    text = "Sign Up",
                    onClick = {
                        showGuestDialog = false
                        onNavigateToSignup()
                    },
                    gradientColors = listOf(PrimaryBlue, Color(0xFF1565C0))
                )
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { showGuestDialog = false }) {
                        Text("Dismiss", color = Color.Gray)
                    }
                    RealisticOutlinedButton(
                        text = "Log In",
                        onClick = {
                            showGuestDialog = false
                            onNavigateToLogin()
                        },
                        borderColor = PrimaryBlue
                    )
                }
            }
        )
    }

    androidx.activity.compose.BackHandler(enabled = currentView != DoubtView.List) {
        currentView = DoubtView.List
    }

    var doubtsList by remember { mutableStateOf<List<DoubtResponse>>(emptyList()) }
    var repliesList by remember { mutableStateOf<List<DoubtReplyResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch lists dynamically
    val refreshData = {
        isLoading = true
        if (currentView is DoubtView.List) {
            if (currentUser.isAdmin()) {
                viewModel.getAdminDoubts { result ->
                    isLoading = false
                    result.onSuccess { doubtsList = it }
                    result.onFailure {
                        Toast.makeText(context, "Error fetching doubts: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                viewModel.getStudentDoubts { result ->
                    isLoading = false
                    result.onSuccess { doubtsList = it }
                    result.onFailure {
                        Toast.makeText(context, "Error fetching doubts: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (currentView is DoubtView.Detail) {
            val doubtId = (currentView as DoubtView.Detail).doubt.id
            viewModel.getDoubtReplies(doubtId) { result ->
                isLoading = false
                result.onSuccess { repliesList = it }
                result.onFailure {
                    Toast.makeText(context, "Error fetching replies: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(currentUser, currentView) {
        refreshData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentView) {
                            is DoubtView.List -> if (currentUser.isAdmin()) "Student Doubt Tickets" else "Ask Doubt Forum"
                            is DoubtView.Create -> "Ask a Doubt"
                            is DoubtView.Detail -> "Doubt Ticket Details"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            when (currentView) {
                                is DoubtView.List -> onBack()
                                else -> currentView = DoubtView.List
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentView is DoubtView.Detail) {
                        val selectedDoubt = (currentView as DoubtView.Detail).doubt
                        // Refresh button
                        IconButton(onClick = { refreshData() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh replies")
                        }
                        
                        // Admin options: Change status & Delete
                        if (currentUser.isAdmin()) {
                            var statusExpanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { statusExpanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Doubt Action Menu")
                                }
                                DropdownMenu(
                                    expanded = statusExpanded,
                                    onDismissRequest = { statusExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Mark Pending") },
                                        onClick = {
                                            statusExpanded = false
                                            isLoading = true
                                            viewModel.updateDoubtStatus(selectedDoubt.id, "pending") { res ->
                                                isLoading = false
                                                res.onSuccess {
                                                    Toast.makeText(context, "Status updated to Pending", Toast.LENGTH_SHORT).show()
                                                    currentView = DoubtView.List
                                                }
                                            }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Mark Replied") },
                                        onClick = {
                                            statusExpanded = false
                                            isLoading = true
                                            viewModel.updateDoubtStatus(selectedDoubt.id, "replied") { res ->
                                                isLoading = false
                                                res.onSuccess {
                                                    Toast.makeText(context, "Status updated to Replied", Toast.LENGTH_SHORT).show()
                                                    currentView = DoubtView.List
                                                }
                                            }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Mark Resolved") },
                                        onClick = {
                                            statusExpanded = false
                                            isLoading = true
                                            viewModel.updateDoubtStatus(selectedDoubt.id, "resolved") { res ->
                                                isLoading = false
                                                res.onSuccess {
                                                    Toast.makeText(context, "Status updated to Resolved", Toast.LENGTH_SHORT).show()
                                                    currentView = DoubtView.List
                                                }
                                            }
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Delete Ticket", color = Color.Red) },
                                        onClick = {
                                            statusExpanded = false
                                            isLoading = true
                                            viewModel.deleteDoubt(selectedDoubt.id) { res ->
                                                isLoading = false
                                                res.onSuccess {
                                                    Toast.makeText(context, "Ticket Deleted Successfully", Toast.LENGTH_SHORT).show()
                                                    currentView = DoubtView.List
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (currentView is DoubtView.List && currentUser?.role != "admin") {
                FloatingActionButton(
                    onClick = {
                        if (isGuest) {
                            showGuestDialog = true
                        } else {
                            currentView = DoubtView.Create
                        }
                    },
                    containerColor = SecondaryGold,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ask Doubt")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when (currentView) {
                is DoubtView.List -> {
                    DoubtListContent(
                        doubts = doubtsList,
                        isLoading = isLoading,
                        isAdmin = currentUser.isAdmin(),
                        onDoubtClick = { currentView = DoubtView.Detail(it) },
                        onRefresh = { refreshData() }
                    )
                }
                is DoubtView.Create -> {
                    AskDoubtForm(
                        viewModel = viewModel,
                        onSuccess = {
                            Toast.makeText(context, "Doubt Submitted Successfully!", Toast.LENGTH_LONG).show()
                            currentView = DoubtView.List
                        }
                    )
                }
                is DoubtView.Detail -> {
                    val activeDoubt = (currentView as DoubtView.Detail).doubt
                    DoubtDetailChat(
                        doubt = activeDoubt,
                        replies = repliesList,
                        viewModel = viewModel,
                        currentUserEmail = currentUser?.email ?: "",
                        isLoading = isLoading,
                        onReplySuccess = {
                            refreshData()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DoubtListContent(
    doubts: List<DoubtResponse>,
    isLoading: Boolean,
    isAdmin: Boolean,
    onDoubtClick: (DoubtResponse) -> Unit,
    onRefresh: () -> Unit
) {
    if (isLoading && doubts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SecondaryGold)
        }
    } else if (doubts.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No doubts found.",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAdmin) "All student doubt tickets are cleared!" else "Have a question? Tap the '+' button to ask a doubt.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Refresh")
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(doubts, key = { it.id }, contentType = { "doubt" }) { doubt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDoubtClick(doubt) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Subject bubble
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFBE9E7),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, SecondaryGold.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = doubt.subject.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            
                            // Status pill
                            val statusColor = when (doubt.status.lowercase()) {
                                "pending" -> Color(0xFFD84315) // orange-red
                                "replied" -> Color(0xFF1E88E5) // blue
                                "resolved" -> Color(0xFF2E7D32) // green
                                else -> Color.Gray
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = statusColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = doubt.status.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = doubt.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = doubt.message,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAdmin) "Student: ${doubt.studentName}" else "You",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Text(
                                text = formatShortDate(doubt.createdAt),
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        if (doubt.status.lowercase() == "replied") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE3F2FD),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubble,
                                        contentDescription = null,
                                        tint = Color(0xFF1E88E5),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAdmin) "Student replied. View thread." else "Admin replied. View thread.",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E88E5)
                                    )
                                }
                            }
                        } else if (doubt.status.lowercase() == "pending" && isAdmin) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFF3E0),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Awaiting your reply.",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFE65100)
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

@Composable
fun AskDoubtForm(
    viewModel: DashboardViewModel,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var subject by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var attachedUri by remember { mutableStateOf<Uri?>(null) }
    var attachedFileName by remember { mutableStateOf<String?>(null) }
    var attachedFileData by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isProcessingFile by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Launcher for file picker with automatic background image compression
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isProcessingFile = true
            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val result = compressImageUriToBase64(context, it)
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        isProcessingFile = false
                        if (result != null) {
                            attachedUri = it
                            attachedFileName = result.first
                            attachedFileData = result.second
                        } else {
                            Toast.makeText(context, "Error processing image file.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        isProcessingFile = false
                        Toast.makeText(context, "Error processing image file: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ask a Teacher",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Text(
            text = "Describe your academic doubt, attach an optional reference image, and our educators will reply shortly.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject (e.g. Physics, Chemistry, Maths)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Doubt Title (e.g. Optics Convex Lens Formula)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Describe your doubt in detail...") },
            minLines = 4,
            maxLines = 8,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image attachment container
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = Color.LightGray.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (attachedUri != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = SecondaryGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = attachedFileName ?: "Image Attached",
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = {
                            attachedUri = null
                            attachedFileName = null
                            attachedFileData = null
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Base64Image(
                        base64Str = attachedFileData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pickerLauncher.launch("image/*") }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Attach Reference Image (Max 20MB)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isSubmitting) {
            CircularProgressIndicator(color = SecondaryGold)
        } else {
            Button(
                onClick = {
                    if (!SecurityUtils.isNetworkAvailable(context)) {
                        Toast.makeText(context, "You are offline. Please connect to the internet to submit your doubt.", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (subject.isBlank() || title.isBlank() || message.isBlank()) {
                        Toast.makeText(context, "Please fill in all mandatory fields.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isSubmitting = true
                    viewModel.submitDoubt(title, subject, message, attachedFileName, attachedFileData) { res ->
                        isSubmitting = false
                        res.onSuccess { onSuccess() }.onFailure {
                            val errorMsg = if (!SecurityUtils.isNetworkAvailable(context) || it is java.io.IOException) {
                                "You are offline. Please connect to the internet to submit your doubt."
                            } else {
                                "Submission failed: ${it.localizedMessage}"
                            }
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Doubt", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DoubtDetailChat(
    doubt: DoubtResponse,
    replies: List<DoubtReplyResponse>,
    viewModel: DashboardViewModel,
    currentUserEmail: String,
    isLoading: Boolean,
    onReplySuccess: () -> Unit
) {
    val context = LocalContext.current
    var replyMessage by remember { mutableStateOf("") }
    var replyFileName by remember { mutableStateOf<String?>(null) }
    var replyFileData by remember { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }
    var isProcessingReplyFile by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Launcher for file picker with automatic background image compression
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isProcessingReplyFile = true
            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val result = compressImageUriToBase64(context, it)
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        isProcessingReplyFile = false
                        if (result != null) {
                            replyFileName = result.first
                            replyFileData = result.second
                        } else {
                            Toast.makeText(context, "Error processing reply image.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        isProcessingReplyFile = false
                        Toast.makeText(context, "Error reading image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat list container
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
            ) {
                // 1. Render Parent Doubt card as the first item
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryGold.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SUBJECT: ${doubt.subject.uppercase()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = formatShortDate(doubt.createdAt),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                text = doubt.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = PrimaryBlue
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = doubt.message,
                                fontSize = 14.sp,
                                color = Color(0xFF212121)
                            )
                            
                            if (doubt.fileData != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Attached image:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Base64Image(
                                    base64Str = doubt.fileData,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 220.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "By: ${doubt.studentName} (${doubt.studentEmail})",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Discussion Forum Thread",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                }

                // 2. Render Loading or List of replies
                if (isLoading && replies.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = SecondaryGold, modifier = Modifier.size(24.dp))
                        }
                    }
                } else {
                    items(replies, key = { it.id }, contentType = { "reply" }) { reply ->
                        val isMe = reply.senderEmail == currentUserEmail
                        
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            // Sender tags
                            Text(
                                text = "${reply.senderName} (${reply.senderRole.uppercase()})",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                            
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isMe) 12.dp else 0.dp,
                                    bottomEnd = if (isMe) 0.dp else 12.dp
                                ),
                                color = if (isMe) Color(0xFFFBE9E7) else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = reply.message,
                                        fontSize = 14.sp,
                                        color = Color(0xFF212121)
                                    )
                                    
                                    if (reply.fileData != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Base64Image(
                                            base64Str = reply.fileData,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 180.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = formatShortTime(reply.createdAt),
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Reply Input Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Preview attached reply image
            if (replyFileData != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = SecondaryGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reply Image Attached", fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(onClick = {
                        replyFileName = null
                        replyFileData = null
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Red)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Paperclip icon
                IconButton(onClick = { pickerLauncher.launch("image/*") }) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach image",
                        tint = PrimaryBlue
                    )
                }

                OutlinedTextField(
                    value = replyMessage,
                    onValueChange = { replyMessage = it },
                    placeholder = { Text("Write a reply...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    maxLines = 4,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF212121),
                        unfocusedTextColor = Color(0xFF212121),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = SecondaryGold,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = SecondaryGold)
                } else {
                    IconButton(
                        onClick = {
                            if (!SecurityUtils.isNetworkAvailable(context)) {
                                Toast.makeText(context, "You are offline. Please connect to the internet to send a reply.", Toast.LENGTH_LONG).show()
                                return@IconButton
                            }
                            if (replyMessage.isBlank() && replyFileData == null) return@IconButton
                            isSending = true
                            viewModel.submitDoubtReply(doubt.id, replyMessage, replyFileName, replyFileData) { res ->
                                isSending = false
                                res.onSuccess {
                                    replyMessage = ""
                                    replyFileName = null
                                    replyFileData = null
                                    onReplySuccess()
                                }.onFailure {
                                    val errorMsg = if (!SecurityUtils.isNetworkAvailable(context) || it is java.io.IOException) {
                                        "You are offline. Please connect to the internet to send a reply."
                                    } else {
                                        "Error sending reply: ${it.localizedMessage}"
                                    }
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .background(PrimaryBlue, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// Utility functions delegating to central robust utils
fun getUriFileName(context: Context, uri: Uri): String =
    com.sparkingpathshala.app.utils.FileUtils.getUriFileName(context, uri)

fun parseIsoStringToDate(isoString: String): java.util.Date? =
    com.sparkingpathshala.app.utils.DateUtils.parseIsoStringToDate(isoString)

fun formatShortDate(isoString: String): String =
    com.sparkingpathshala.app.utils.DateUtils.formatToUserLocalTime(isoString, "dd MMM, hh:mm a")

fun formatShortTime(isoString: String): String =
    com.sparkingpathshala.app.utils.DateUtils.formatToUserLocalTime(isoString, "hh:mm a")

@Composable
fun Base64Image(base64Str: String?, modifier: Modifier = Modifier) {
    AsyncBase64Image(base64Str, modifier)
}

suspend fun compressImageUriToBase64(context: Context, uri: Uri): Pair<String, String>? =
    com.sparkingpathshala.app.utils.FileUtils.compressImageUriToBase64(context, uri)

fun calculateInSampleSize(options: android.graphics.BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int =
    com.sparkingpathshala.app.utils.FileUtils.calculateInSampleSize(options, reqWidth, reqHeight)



