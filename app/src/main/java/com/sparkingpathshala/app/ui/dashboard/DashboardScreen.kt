package com.sparkingpathshala.app.ui.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
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
import com.sparkingpathshala.app.data.local.*
import com.sparkingpathshala.app.ui.common.LogoutConfirmationDialog
import com.sparkingpathshala.app.ui.dashboard.components.DashboardDrawerContent
import com.sparkingpathshala.app.ui.navigation.BottomBarScreen
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    currentTab: BottomBarScreen,
    onTabSelected: (BottomBarScreen) -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToOnDemand: () -> Unit,
    onNavigateToPremiumMaterial: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAdminRecords: (Int) -> Unit = { _ -> },
    onNavigateToAdminStudyMaterial: () -> Unit = {},
    onNavigateToAdminApprovals: () -> Unit = {},
    onNavigateToAdminStudents: () -> Unit = {},
    onNavigateToAdminEnquiries: () -> Unit = {},
    onNavigateToHomework: () -> Unit = {},
    onNavigateToAdminHomework: () -> Unit = {},
    onNavigateToAdminQuizzes: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAdminGuests: () -> Unit = {},
    onNavigateToDownloads: () -> Unit = {},
    content: @Composable (PaddingValues, onOpenDrawer: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user by viewModel.user.collectAsState()

    val context = LocalContext.current
    var wasOffline by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
        wasOffline = !(capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)

        val callback = object : android.net.ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                if (wasOffline) {
                    wasOffline = false
                    // Silent background sync when reconnecting - no intrusive banners or popups
                    viewModel.syncAll()
                }
            }

            override fun onLost(network: android.net.Network) {
                wasOffline = true
            }
        }

        val request = android.net.NetworkRequest.Builder()
            .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager?.registerNetworkCallback(request, callback)

        onDispose {
            try {
                connectivityManager?.unregisterNetworkCallback(callback)
            } catch (_: Exception) {}
        }
    }

    // 1. If navigation drawer is open, back press closes it
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    // 2. If user is on any secondary tab, back press returns to Home tab
    BackHandler(enabled = !drawerState.isOpen && currentTab != BottomBarScreen.HOME) {
        onTabSelected(BottomBarScreen.HOME)
    }

    // 3. Double-back press on Home tab prevents accidental app closures
    var lastBackPressTime by remember { mutableLongStateOf(0L) }
    BackHandler(enabled = !drawerState.isOpen && currentTab == BottomBarScreen.HOME) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < 2000L) {
            (context as? android.app.Activity)?.finish()
        } else {
            lastBackPressTime = currentTime
            android.widget.Toast.makeText(context, "Press back again to exit", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DashboardDrawerContent(
                user = user,
                currentTab = currentTab,
                onTabSelected = onTabSelected,
                onClose = { scope.launch { drawerState.close() } },
                onNavigateToAdminStudents = onNavigateToAdminStudents,
                onNavigateToAdminRecords = onNavigateToAdminRecords,
                onNavigateToAdminApprovals = onNavigateToAdminApprovals,
                onNavigateToAdminStudyMaterial = onNavigateToAdminStudyMaterial,
                onNavigateToAdminHomework = onNavigateToAdminHomework,
                onNavigateToAdminQuizzes = onNavigateToAdminQuizzes,
                onNavigateToAdminEnquiries = onNavigateToAdminEnquiries,
                onNavigateToAdminGuests = onNavigateToAdminGuests,
                onNavigateToHomework = onNavigateToHomework,
                onNavigateToAttendance = onNavigateToAttendance,
                onNavigateToFees = onNavigateToFees,
                onNavigateToResults = onNavigateToResults,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToDownloads = onNavigateToDownloads,
                onNavigateToPremiumMaterial = onNavigateToPremiumMaterial,
                onNavigateToOnDemand = onNavigateToOnDemand,
                onShowLogout = { showLogoutDialog = true }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                val visibleNavScreens = remember(user) {
                        when {
                            user.isAdmin() -> listOf(
                                BottomBarScreen.HOME,
                                BottomBarScreen.MATERIAL,
                                BottomBarScreen.HOMEWORK,
                                BottomBarScreen.ATTENDANCE,
                                BottomBarScreen.PROFILE
                            )
                            user.isGuest() -> listOf(
                                BottomBarScreen.HOME,
                                BottomBarScreen.QUIZ,
                                BottomBarScreen.MATERIAL,
                                BottomBarScreen.PROFILE
                            )
                            else -> listOf(
                                BottomBarScreen.HOME,
                                BottomBarScreen.MATERIAL,
                                BottomBarScreen.HOMEWORK,
                                BottomBarScreen.QUIZ,
                                BottomBarScreen.PROFILE
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            windowInsets = WindowInsets.navigationBars,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            visibleNavScreens.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    selected = currentTab == screen,
                                    onClick = { onTabSelected(screen) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
            }
        ) { paddingValues ->
            content(paddingValues) {
                scope.launch { drawerState.open() }
            }
        }
    }

    LogoutConfirmationDialog(
        show = showLogoutDialog,
        onDismiss = { showLogoutDialog = false },
        onConfirm = onLogout
    )
}
