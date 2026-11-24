package com.sparkingpathshala.app.ui.dashboard.components

import android.widget.Toast
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.sparkingpathshala.app.data.local.*
import com.sparkingpathshala.app.ui.common.DeleteAccountConfirmationDialog
import com.sparkingpathshala.app.ui.common.LogoutConfirmationDialog
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.dashboard.SyncState
import com.sparkingpathshala.app.ui.theme.*

@Composable
fun HomeTabContent(
    viewModel: DashboardViewModel,
    paddingValues: PaddingValues = PaddingValues(),
    onOpenDrawer: () -> Unit = {},
    onNavigateToFees: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToOnDemand: () -> Unit,
    onNavigateToPremiumMaterial: () -> Unit = {},
    onNavigateToHomework: () -> Unit = {},
    onNavigateToAdminHomework: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAdminGuests: () -> Unit = {},
    onNavigateToDownloads: () -> Unit = {},
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onNavigateToAdminRecords: (Int) -> Unit = { _ -> },
    onNavigateToAdminStudyMaterial: () -> Unit = {},
    onNavigateToAdminApprovals: () -> Unit = {},
    onNavigateToQuiz: () -> Unit = {},
    onNavigateToAdminQuizzes: () -> Unit = {},
    onNavigateToAdminStudents: () -> Unit = {},
    onNavigateToAdminEnquiries: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val unreadNotifCount by viewModel.unreadNotificationCount.collectAsState()
    val versionInfo by viewModel.versionInfo.collectAsState()
    val context = LocalContext.current
    val isBillingAdmin = user.isAdmin()

    var totalStudentsCount by remember { mutableIntStateOf(0) }
    var pendingApprovalsCount by remember { mutableIntStateOf(0) }
    var totalDoubtTicketsCount by remember { mutableIntStateOf(0) }
    var activeQuizzesCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(user?.id, user?.role) {
        if (user.isAdmin()) {
            viewModel.getAdminStudents { res ->
                res.onSuccess { totalStudentsCount = it.size }
            }
            viewModel.getPendingStudents { res ->
                res.onSuccess { pendingApprovalsCount = it.count { s -> s.status == "pending_approval" } }
            }
            viewModel.getAdminDoubts { res ->
                res.onSuccess { totalDoubtTicketsCount = it.count { d -> d.status.lowercase() != "resolved" } }
            }
            viewModel.getAdminQuizzes { res ->
                res.onSuccess { activeQuizzesCount = it.size }
            }
        }
    }

    val fees by viewModel.fees.collectAsState()
    val results by viewModel.results.collectAsState()
    val attendance by viewModel.attendance.collectAsState()
    val downloadedCount by viewModel.downloadedCount.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    val pendingFees = remember(fees) {
        fees.filter { !it.isPaid }.sumOf { it.amount }
    }
    val attendancePercentage = remember(attendance) {
        val totalAttendanceDays = attendance.size
        val presentDays = attendance.count { it.isPresent }
        if (totalAttendanceDays > 0) {
            (presentDays.toFloat() / totalAttendanceDays * 100).toInt()
        } else 100
    }

    val averageScore = remember(results) {
        if (results.isNotEmpty()) {
            (results.sumOf { it.marksObtained.toFloat() / it.maxMarks } / results.size * 100).toInt()
        } else 0
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val prefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    var noticeTitle by remember { mutableStateOf(prefs.getString("notice_title", "Admissions Open 2027: CBSE Classes 9–12") ?: "Admissions Open 2027: CBSE Classes 9–12") }
    var noticeBody by remember { mutableStateOf(prefs.getString("notice_body", "Science & Maths batches under Er. Sajjan Kumar Mallik (B.Tech Gold Medalist). Batch cap strictly 20 students.") ?: "Science & Maths batches under Er. Sajjan Kumar Mallik (B.Tech Gold Medalist). Batch cap strictly 20 students.") }
    var noticeNote by remember { mutableStateOf(prefs.getString("notice_note", "📍 Campus: Dheeraj Nagar, Atmadpur, Faridabad • Call: +91 99994 70824") ?: "📍 Campus: Dheeraj Nagar, Atmadpur, Faridabad • Call: +91 99994 70824") }
    var showNoticeBanner by remember { mutableStateOf(!prefs.getBoolean("notice_dismissed_key", false)) }
    var showEditNoticeDialog by remember { mutableStateOf(false) }

    if (showEditNoticeDialog) {
        FacultyNoticeEditDialog(
            initialTitle = noticeTitle,
            initialBody = noticeBody,
            initialNote = noticeNote,
            onDismiss = { showEditNoticeDialog = false },
            onPublish = { editTitle, editBody, editNote ->
                noticeTitle = editTitle
                noticeBody = editBody
                noticeNote = editNote
                showNoticeBanner = true
                prefs.edit {
                    putString("notice_title", editTitle)
                    putString("notice_body", editBody)
                    putString("notice_note", editNote)
                    putBoolean("notice_dismissed_key", false)
                }
                viewModel.sendNoticeAnnouncement(editTitle, editBody, editNote)
                showEditNoticeDialog = false
                Toast.makeText(context, "Notice published to all students!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    DeleteAccountConfirmationDialog(
        show = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onConfirm = onDeleteAccount
    )

    LogoutConfirmationDialog(
        show = showLogoutDialog,
        onDismiss = { showLogoutDialog = false },
        onConfirm = onLogout
    )

    val homeScrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(homeScrollState)
        ) {
            // Modern 2026/2027 Hero Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = BrandGradient,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding()
                    .padding(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White)
                                    .border(BorderStroke(1.5.dp, ModernAmber), RoundedCornerShape(14.dp))
                                    .clickable(onClickLabel = "Open Navigation Menu", onClick = onOpenDrawer),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.sparkingpathshala.app.R.drawable.ic_launcher),
                                    contentDescription = "Sparking Pathshala Logo - Tap to open menu",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                val timeZone = java.util.TimeZone.getTimeZone("Asia/Kolkata")
                                val calendar = java.util.Calendar.getInstance(timeZone)
                                val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                                val greeting = when (hour) {
                                    in 0..11 -> "Good Morning"
                                    in 12..16 -> "Good Afternoon"
                                    else -> "Good Evening"
                                }
                                Text(
                                    text = "$greeting,",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = user?.name ?: "Student",
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val subText = if (user.isAdmin()) "Administrator Portal" else "${user?.classLevel ?: "Class 10"} • CBSE / State"
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.18f),
                                    modifier = Modifier.padding(top = 3.dp)
                                ) {
                                    Text(
                                        text = subText,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.syncAll() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = Color.White, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = onNavigateToNotifications,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotifCount > 0) {
                                            Badge(containerColor = ModernRose, contentColor = Color.White) {
                                                Text("$unreadNotifCount", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Daily Study Streak & Goal Tracker Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.Black.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, ModernAmber.copy(alpha = 0.40f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔥", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "5-Day Learning Streak!",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = ModernAmber
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("⚡", fontSize = 10.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "350 XP",
                                            color = Color(0xFF0F172A),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Today's goal: 2 of 3 tasks completed",
                                    color = Color.White.copy(alpha = 0.90f),
                                    fontSize = 11.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                    modifier = Modifier.clickable { onNavigateToQuiz() }
                                ) {
                                    Text(
                                        text = "Continue →",
                                        color = BrandCrimson,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sync Error Banner
            if (syncState is SyncState.Error) {
                Surface(
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = ModernRose, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = (syncState as SyncState.Error).message,
                                fontSize = 11.sp,
                                color = Color(0xFF991B1B)
                            )
                        }
                        TextButton(
                            onClick = { viewModel.syncAll() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ModernRose)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Urgent Faculty Notice Banner
            if (showNoticeBanner) {
                FacultyNoticeBanner(
                    title = noticeTitle,
                    body = noticeBody,
                    note = noticeNote,
                    isAdmin = user.isAdmin(),
                    onDismiss = {
                        showNoticeBanner = false
                        prefs.edit { putBoolean("notice_dismissed_key", true) }
                    },
                    onEdit = { showEditNoticeDialog = true }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Version Update Grace Period Banner
            if (versionInfo.status == DashboardViewModel.ForceUpdateStatus.GRACE_PERIOD_ACTIVE) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = ModernAmber)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("New Version Available (${versionInfo.latestVersionName})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF92400E))
                            Text("Update within ${versionInfo.daysRemainingInGrace} days.", fontSize = 11.sp, color = Color(0xFF78350F))
                        }
                        Button(
                            onClick = {
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, "market://details?id=com.sparkingpathshala.app".toUri())
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.sparkingpathshala.app".toUri())
                                    context.startActivity(webIntent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ModernAmber),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Update", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Today's Live Class / Batch Highlight Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFEFF6FF), CircleShape)
                                .border(1.dp, Color(0xFFBFDBFE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔴", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("LIVE CLASS TODAY", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ModernRose)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("• 4:30 PM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                            }
                            Text(
                                text = "Electrochemistry: Nernst Equation",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text("By Prof. Verma • Chemistry", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                        }
                    }
                    Button(
                        onClick = onNavigateToOnDemand,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Join", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Official Canva 2027 Admissions Showcase Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(0.5.dp, Color(0xFFF59E0B))
                        ) {
                            Text(
                                text = "🎓 ADMISSIONS OPEN 2027",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryBlue.copy(alpha = 0.08f)
                        ) {
                            Text(
                                text = "Batch Cap: 20 Students",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.sparkingpathshala.app.R.drawable.banner_admissions_canva),
                                contentDescription = "Sparking Pathshala Admissions Flyer",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CBSE Classes 9–12 • Science & Maths",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Under Er. Sajjan Kumar Mallik (B.Tech Gold Medalist)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Campus: Dheeraj Nagar, Atmadpur, Faridabad",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                        data = "tel:+919999470824".toUri()
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            border = BorderStroke(1.dp, PrimaryBlue)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Campus", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }

                        Button(
                            onClick = onNavigateToAdminEnquiries,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("Enquire Now →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Actions Hub
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (user.isAdmin()) "Admin Controls" else "Learning Hub",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "All Tools →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable { onOpenDrawer() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Pills Grid
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (user.isAdmin()) {
                        val adminShortcuts = remember(isBillingAdmin) {
                            buildList {
                                add(Triple("Attendance", Icons.Default.CheckCircle, Color(0xFFECFDF5) to ModernEmerald to { onNavigateToAdminRecords(0) }))
                                add(Triple("Test Results", Icons.AutoMirrored.Filled.FactCheck, Color(0xFFFEF3C7) to ModernAmber to { onNavigateToAdminRecords(1) }))
                                if (isBillingAdmin) {
                                    add(Triple("Fee Ledger", Icons.Default.Payments, Color(0xFFEFF6FF) to PrimaryBlue to { onNavigateToAdminRecords(2) }))
                                }
                                add(Triple("Students", Icons.Default.People, Color(0xFFEEF2FF) to ModernViolet to onNavigateToAdminStudents))
                                add(Triple("Upload Notes", Icons.Default.CloudUpload, Color(0xFFF0FDF4) to Color(0xFF16A34A) to onNavigateToAdminStudyMaterial))
                                add(Triple("Quiz Builder", Icons.AutoMirrored.Filled.Assignment, Color(0xFFFFF7ED) to Color(0xFFEA580C) to onNavigateToAdminQuizzes))
                            }
                        }
                        adminShortcuts.chunked(3).forEachIndexed { idx, row ->
                            if (idx > 0) Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                row.forEach { item ->
                                    val (title, icon, colorsAndAction) = item
                                    val (colors, action) = colorsAndAction
                                    ActionShortcutItem(title, icon, colors.first, colors.second, action)
                                }
                                repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ActionShortcutItem("Speed Quiz", Icons.Default.Extension, Color(0xFFFEF3C7), ModernAmber, onNavigateToQuiz)
                            ActionShortcutItem("Homework", Icons.AutoMirrored.Filled.Assignment, Color(0xFFEFF6FF), PrimaryBlue, onNavigateToHomework)
                            ActionShortcutItem("NCERT Notes", Icons.Default.Book, Color(0xFFECFDF5), ModernEmerald, onNavigateToDownloads)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ActionShortcutItem("Ask Doubt", Icons.AutoMirrored.Filled.LiveHelp, Color(0xFFFDF2F8), Color(0xFFDB2777), onNavigateToOnDemand)
                            ActionShortcutItem("Report Card", Icons.AutoMirrored.Filled.FactCheck, Color(0xFFF5F3FF), ModernViolet, onNavigateToResults)
                            ActionShortcutItem("Fee Receipts", Icons.Default.Payments, Color(0xFFF0FDF4), Color(0xFF16A34A), onNavigateToFees)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Student Overview (2x2 Cards)
            Text(
                text = if (user.isAdmin()) "Batch Health Overview" else "Academic Progress Vitals",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!user.isAdmin()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Attendance",
                            value = "$attendancePercentage%",
                            icon = Icons.Default.CalendarToday,
                            iconColor = ModernEmerald,
                            onClick = onNavigateToAttendance,
                            modifier = Modifier.weight(1f)
                        ) {
                            LinearProgressIndicator(
                                progress = { attendancePercentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ModernEmerald,
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }

                        val isDue = pendingFees > 0
                        StatCard(
                            title = "Pending Fees",
                            value = "₹$pendingFees",
                            icon = Icons.Default.CurrencyRupee,
                            iconColor = if (isDue) ModernRose else ModernEmerald,
                            onClick = onNavigateToFees,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDue) Color(0xFFFEE2E2) else Color(0xFFECFDF5),
                                border = BorderStroke(1.dp, if (isDue) Color(0xFFFCA5A5) else Color(0xFFA7F3D0))
                            ) {
                                Text(
                                    text = if (isDue) "PAY DUE" else "PAID IN FULL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDue) ModernRose else ModernEmerald,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Average Mark",
                            value = "$averageScore%",
                            icon = Icons.Default.Assessment,
                            iconColor = PrimaryBlue,
                            onClick = onNavigateToResults,
                            modifier = Modifier.weight(1f)
                        ) {
                            LinearProgressIndicator(
                                progress = { averageScore / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PrimaryBlue,
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }

                        StatCard(
                            title = "Offline Notes",
                            value = "$downloadedCount Files",
                            icon = Icons.Default.FileDownload,
                            iconColor = ModernViolet,
                            onClick = onNavigateToDownloads,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEEF2FF),
                                border = BorderStroke(1.dp, Color(0xFFC7D2FE))
                            ) {
                                Text(
                                    text = "OFFLINE READY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ModernViolet,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Admin Health Overview
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Total Enrolled",
                            value = "$totalStudentsCount",
                            icon = Icons.Default.People,
                            iconColor = PrimaryBlue,
                            onClick = onNavigateToAdminStudents,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Active Batches", fontSize = 11.sp, color = Color.Gray)
                        }

                        val hasPending = pendingApprovalsCount > 0
                        StatCard(
                            title = "Pending Approvals",
                            value = "$pendingApprovalsCount",
                            icon = Icons.Default.HowToReg,
                            iconColor = if (hasPending) ModernRose else ModernEmerald,
                            onClick = onNavigateToAdminApprovals,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (hasPending) "Review Required" else "All Cleared", fontSize = 11.sp, color = if (hasPending) ModernRose else ModernEmerald, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Open Doubts",
                            value = "$totalDoubtTicketsCount",
                            icon = Icons.AutoMirrored.Filled.LiveHelp,
                            iconColor = ModernAmber,
                            onClick = onNavigateToOnDemand,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Awaiting Faculty", fontSize = 11.sp, color = Color.Gray)
                        }

                        StatCard(
                            title = "Active Quizzes",
                            value = "$activeQuizzesCount",
                            icon = Icons.Default.Extension,
                            iconColor = ModernEmerald,
                            onClick = onNavigateToAdminQuizzes,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Running Live", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subject Curriculum Cards (Horizontal Swipeable Row)
            Text(
                text = "Academic Subjects & Syllabus",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mathematics Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .width(170.dp)
                        .clickable { onNavigateToQuiz() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("📐", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Mathematics", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Quadratic Eqns", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { 0.75f },
                            color = PrimaryBlue,
                            trackColor = Color(0xFFEFF6FF),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("9/12 Chapters • 75%", fontSize = 10.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                    }
                }

                // Science / Physics Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .width(170.dp)
                        .clickable { onNavigateToQuiz() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("⚡", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Physics", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Electricity & Optics", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { 0.60f },
                            color = ModernAmber,
                            trackColor = Color(0xFFFEF3C7),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("6/10 Chapters • 60%", fontSize = 10.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                    }
                }

                // Chemistry Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .width(170.dp)
                        .clickable { onNavigateToQuiz() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🧪", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Chemistry", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Carbon Compounds", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { 0.85f },
                            color = ModernEmerald,
                            trackColor = Color(0xFFECFDF5),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("11/13 Chapters • 85%", fontSize = 10.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Question of the Day / Daily Practice Problem (DPP)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DAILY PRACTICE PROBLEM", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Text("+50 Streak XP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "If a wire of resistance R is melted and recast to half of its length, what is the new resistance of the wire?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onNavigateToQuiz,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Solve Challenge in Practice Quiz →", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Pinned Status Bar Scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(BrandGradient)
                .zIndex(10f)
        )
    }
}
