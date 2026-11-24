package com.sparkingpathshala.app.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.local.*
import com.sparkingpathshala.app.ui.navigation.BottomBarScreen
import com.sparkingpathshala.app.ui.theme.BrandGradient
import com.sparkingpathshala.app.ui.theme.ModernAmber
import androidx.compose.ui.platform.LocalUriHandler

@Composable
fun DashboardDrawerContent(
    user: UserEntity?,
    currentTab: BottomBarScreen,
    onTabSelected: (BottomBarScreen) -> Unit,
    onClose: () -> Unit,
    onNavigateToAdminStudents: () -> Unit,
    onNavigateToAdminRecords: (Int) -> Unit,
    onNavigateToAdminApprovals: () -> Unit,
    onNavigateToAdminStudyMaterial: () -> Unit,
    onNavigateToAdminHomework: () -> Unit,
    onNavigateToAdminQuizzes: () -> Unit,
    onNavigateToAdminEnquiries: () -> Unit,
    onNavigateToAdminGuests: () -> Unit,
    onNavigateToHomework: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToPremiumMaterial: () -> Unit,
    onNavigateToOnDemand: () -> Unit,
    onShowLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(310.dp)
            .fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandGradient)
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sparking Pathshala",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user?.name ?: "Student",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (user.isAdmin()) {
                        Text(
                            text = if (user?.role == "billing_admin") "Role: Billing Administrator" else "Role: Administrator",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = if (user.isGuest()) "Class: All Classes" else "Class: ${user?.classLevel ?: "Class 10"}",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Menu",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val uriHandler = LocalUriHandler.current
        val drawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        )

        // Scrollable Menu Items Container for all screen sizes & Admin items
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            DrawerSectionHeader("NAVIGATION")

            NavigationDrawerItem(
                label = { Text("Home / Dashboard", fontWeight = FontWeight.Medium) },
                selected = currentTab == BottomBarScreen.HOME,
                onClick = {
                    onTabSelected(BottomBarScreen.HOME)
                    onClose()
                },
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                colors = drawerItemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            if (user.isAdmin()) {
                DrawerSectionHeader("ADMINISTRATION")

                NavigationDrawerItem(
                    label = { Text("Manage Students", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminStudents()
                    },
                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Manage Attendance", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminRecords(0)
                    },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Student Approvals", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminApprovals()
                    },
                    icon = { Icon(Icons.Default.HowToReg, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Upload Study Material", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminStudyMaterial()
                    },
                    icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Homework Hub", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminHomework()
                    },
                    icon = { Icon(Icons.Default.UploadFile, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Manage Quizzes", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminQuizzes()
                    },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Fee Records", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminRecords(1)
                    },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Test Results", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminRecords(2)
                    },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Student Enquiries", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminEnquiries()
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.ContactSupport, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Guest Users Monitor", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAdminGuests()
                    },
                    icon = { Icon(Icons.Default.PersonSearch, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }

            if (user.isStudent()) {
                DrawerSectionHeader("ACADEMIC HUB")

                NavigationDrawerItem(
                    label = { Text("Homework & Tasks", fontWeight = FontWeight.Medium) },
                    selected = currentTab == BottomBarScreen.HOMEWORK,
                    onClick = {
                        onTabSelected(BottomBarScreen.HOMEWORK)
                        onClose()
                        onNavigateToHomework()
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Attendance Status", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToAttendance()
                    },
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Fees Status", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToFees()
                    },
                    icon = { Icon(Icons.Default.Payments, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Test Results", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToResults()
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }

            DrawerSectionHeader("LEARNING & RESOURCES")

            NavigationDrawerItem(
                label = { Text("Notifications", fontWeight = FontWeight.Medium) },
                selected = false,
                onClick = {
                    onClose()
                    onNavigateToNotifications()
                },
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                colors = drawerItemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { Text("Study Material", fontWeight = FontWeight.Medium) },
                selected = currentTab == BottomBarScreen.MATERIAL,
                onClick = {
                    onTabSelected(BottomBarScreen.MATERIAL)
                    onClose()
                },
                icon = { Icon(Icons.Default.Book, contentDescription = null) },
                colors = drawerItemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { Text("Offline Downloads", fontWeight = FontWeight.Medium) },
                selected = false,
                onClick = {
                    onClose()
                    onNavigateToDownloads()
                },
                icon = { Icon(Icons.Default.CloudDownload, contentDescription = null) },
                colors = drawerItemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            if (user?.role == "student") {
                NavigationDrawerItem(
                    label = { Text("Premium Material", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        onNavigateToPremiumMaterial()
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = null, tint = ModernAmber) },
                    colors = drawerItemColors,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }

            NavigationDrawerItem(
                label = { Text("Ask Doubt", fontWeight = FontWeight.Medium) },
                selected = false,
                onClick = {
                    onClose()
                    onNavigateToOnDemand()
                },
                icon = { Icon(Icons.AutoMirrored.Filled.LiveHelp, contentDescription = null) },
                colors = drawerItemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            DrawerSectionHeader("ACCOUNT & SETTINGS")

            NavigationDrawerItem(
                label = { Text("My Profile", fontWeight = FontWeight.Medium) },
                selected = currentTab == BottomBarScreen.PROFILE,
                onClick = {
                    onTabSelected(BottomBarScreen.PROFILE)
                    onClose()
                },
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                colors = drawerItemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { Text("Privacy Policy", fontWeight = FontWeight.Medium) },
                selected = false,
                onClick = {
                    onClose()
                    try {
                        uriHandler.openUri("https://sparkingpathshala.in/privacy-policy")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                icon = { Icon(Icons.Default.PrivacyTip, contentDescription = null) },
                colors = drawerItemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                label = { Text("Logout", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
                selected = false,
                onClick = {
                    onClose()
                    onShowLogout()
                },
                icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                colors = drawerItemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 28.dp, top = 14.dp, bottom = 6.dp)
    )
}
