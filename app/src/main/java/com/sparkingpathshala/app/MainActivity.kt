package com.sparkingpathshala.app

import android.os.Build
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sparkingpathshala.app.ui.ViewModelFactory
import com.sparkingpathshala.app.data.local.*
import com.sparkingpathshala.app.ui.auth.AuthViewModel
import com.sparkingpathshala.app.ui.auth.LoginScreen
import com.sparkingpathshala.app.ui.auth.SignupScreen
import com.sparkingpathshala.app.ui.dashboard.DashboardScreen
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.dashboard.components.HomeTabContent
import com.sparkingpathshala.app.ui.downloads.DownloadsScreen
import com.sparkingpathshala.app.ui.fees.FeesScreen
import com.sparkingpathshala.app.ui.attendance.AttendanceScreen
import com.sparkingpathshala.app.ui.material.StudyMaterialScreen
import com.sparkingpathshala.app.ui.navigation.BottomBarScreen
import com.sparkingpathshala.app.ui.navigation.Screen
import com.sparkingpathshala.app.ui.ondemand.OnDemandScreen
import com.sparkingpathshala.app.ui.results.ResultsScreen
import com.sparkingpathshala.app.ui.theme.SparkingPathshalaTheme
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.profile.ProfileScreen
import com.sparkingpathshala.app.ui.admin.AdminRecordsScreen
import com.sparkingpathshala.app.ui.admin.AdminStudyMaterialScreen
import com.sparkingpathshala.app.ui.admin.AdminApprovalsScreen
import com.sparkingpathshala.app.ui.admin.AdminQuizzesScreen
import com.sparkingpathshala.app.ui.admin.AdminStudentsScreen
import com.sparkingpathshala.app.ui.admin.AdminEnquiriesScreen
import com.sparkingpathshala.app.ui.quiz.QuizListScreen
import com.sparkingpathshala.app.ui.quiz.QuizPlayScreen
import com.sparkingpathshala.app.ui.homework.HomeworkScreen
import com.sparkingpathshala.app.ui.homework.AdminHomeworkScreen
import com.sparkingpathshala.app.ui.notifications.NotificationCenterScreen
import com.sparkingpathshala.app.ui.guests.AdminGuestUsersScreen

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Modern Android 15+ edge-to-edge configuration
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= 35) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        if (com.sparkingpathshala.app.security.SecurityUtils.detectSecurityThreats(this)) {
            android.util.Log.e("SecurityCheck", "Active debugger or debug build tampering detected. Closing application.")
            android.widget.Toast.makeText(this, "Security alert: Debugging tools detected. Please disable them to run Sparking Pathshala.", android.widget.Toast.LENGTH_LONG).show()
            finishAffinity()
            return
        }
        
        val app = application as SparkingPathshalaApplication
        val factory = ViewModelFactory(app.repository)
        
        setContent {
            SparkingPathshalaTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Screenshot / screen capture prevention for sensitive data screens
                    val context = androidx.compose.ui.platform.LocalContext.current
                    DisposableEffect(navController) {
                        val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, destination, _ ->
                            val activity = context as? android.app.Activity
                            val window = activity?.window
                            if (window != null) {
                                val sensitiveRoutes = listOf(
                                    Screen.Fees.route,
                                    Screen.AdminApprovals.route,
                                    Screen.AdminStudents.route,
                                    Screen.AdminRecords.route,
                                    Screen.AdminQuizzes.route,
                                    Screen.AdminStudyMaterial.route,
                                    Screen.AdminEnquiries.route
                                )
                                val isSensitive = sensitiveRoutes.any { route -> 
                                    destination.route?.contains(route) == true 
                                }
                                if (isSensitive) {
                                    window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
                                } else {
                                    window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
                                }
                            }
                        }
                        navController.addOnDestinationChangedListener(listener)
                        onDispose {
                            navController.removeOnDestinationChangedListener(listener)
                        }
                    }
                    
                    val authViewModel: AuthViewModel = viewModel(factory = factory)
                    val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
                    
                    val isUserLoading by authViewModel.isUserLoading.collectAsState()
                    val currentUser by authViewModel.currentUser.collectAsState()

                    DisposableEffect(isUserLoading) {
                        splashScreen.setKeepOnScreenCondition { isUserLoading }
                        onDispose {}
                    }

                    if (isUserLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(96.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Sparking Pathshala",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Text(
                                    text = "Igniting Minds, Empowering Future",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(28.dp))
                                CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                            }
                        }
                    } else {
                        // Route state checker: redirect authenticated users away from Login/Signup and handle logout
                        LaunchedEffect(currentUser) {
                            if (currentUser != null) {
                                val currentRoute = navController.currentDestination?.route
                                if (currentRoute == Screen.Login.route || currentRoute == Screen.Signup.route) {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            } else {
                                val currentRoute = navController.currentDestination?.route
                                if (currentRoute != null && currentRoute != Screen.Login.route && currentRoute != Screen.Signup.route) {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        // Centralized 401 Session Expiration listener
                        LaunchedEffect(Unit) {
                            com.sparkingpathshala.app.core.AuthSessionManager.sessionExpiredEvent.collect {
                                val currentRoute = navController.currentDestination?.route
                                if (currentRoute != Screen.Login.route && currentRoute != Screen.Signup.route) {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }
                        
                        NavHost(
                            navController = navController,
                            startDestination = if (currentUser == null) Screen.Login.route else Screen.Dashboard.route,
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                            popExitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                                onLoginSuccess = {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Signup.route) {
                            SignupScreen(
                                viewModel = authViewModel,
                                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                                onSignupSuccess = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Signup.route) { inclusive = true }
                                    }
                                },
                                onGuestLoginSuccess = {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Dashboard.route) {
                            var currentTab by remember { mutableStateOf(BottomBarScreen.HOME) }
                            
                            LaunchedEffect(Unit) {
                                dashboardViewModel.checkAppVersion(com.sparkingpathshala.app.BuildConfig.VERSION_CODE)
                            }

                            DashboardScreen(
                                viewModel = dashboardViewModel,
                                currentTab = currentTab,
                                onTabSelected = { currentTab = it },
                                onNavigateToFees = { navController.navigate(Screen.Fees.route) },
                                onNavigateToResults = { navController.navigate(Screen.Results.route) },
                                onNavigateToAttendance = { navController.navigate(Screen.Attendance.route) },
                                onNavigateToOnDemand = { navController.navigate(Screen.OnDemand.route) },
                                onNavigateToPremiumMaterial = { navController.navigate(Screen.PremiumMaterial.route) },
                                onNavigateToHomework = { currentTab = BottomBarScreen.HOMEWORK },
                                onNavigateToAdminHomework = { navController.navigate(Screen.AdminHomework.route) },
                                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                                onNavigateToAdminGuests = { navController.navigate(Screen.AdminGuests.route) },
                                onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) },
                                onLogout = { authViewModel.logout() },
                                onNavigateToAdminRecords = { tabIndex -> navController.navigate(Screen.AdminRecords.createRoute(tabIndex)) },
                                onNavigateToAdminStudyMaterial = { navController.navigate(Screen.AdminStudyMaterial.route) },
                                onNavigateToAdminApprovals = { navController.navigate(Screen.AdminApprovals.route) },
                                onNavigateToAdminStudents = { navController.navigate(Screen.AdminStudents.route) },
                                onNavigateToAdminEnquiries = { navController.navigate(Screen.AdminEnquiries.route) }
                            ) { paddingValues, onOpenDrawer ->
                                AnimatedContent(
                                    targetState = currentTab,
                                    transitionSpec = {
                                        (fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                                         slideInHorizontally(animationSpec = tween(220, easing = FastOutSlowInEasing)) { width -> if (targetState.ordinal > initialState.ordinal) width / 5 else -width / 5 }
                                        ).togetherWith(
                                         fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing))
                                        )
                                    },
                                    label = "TabSwitchTransition"
                                ) { activeTab ->
                                    when (activeTab) {
                                        BottomBarScreen.HOME -> HomeTabContent(
                                            viewModel = dashboardViewModel,
                                            paddingValues = paddingValues,
                                            onOpenDrawer = onOpenDrawer,
                                            onNavigateToFees = { navController.navigate(Screen.Fees.route) },
                                            onNavigateToResults = { navController.navigate(Screen.Results.route) },
                                            onNavigateToAttendance = { navController.navigate(Screen.Attendance.route) },
                                            onNavigateToOnDemand = { navController.navigate(Screen.OnDemand.route) },
                                            onNavigateToPremiumMaterial = { navController.navigate(Screen.PremiumMaterial.route) },
                                            onNavigateToHomework = { currentTab = BottomBarScreen.HOMEWORK },
                                            onNavigateToAdminHomework = { navController.navigate(Screen.AdminHomework.route) },
                                            onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                                            onNavigateToAdminGuests = { navController.navigate(Screen.AdminGuests.route) },
                                            onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) },
                                            onLogout = { authViewModel.logout() },
                                            onDeleteAccount = {
                                                authViewModel.deleteAccount { res ->
                                                    res.onFailure { err ->
                                                        android.widget.Toast.makeText(
                                                            context,
                                                            "Account deletion failed: ${err.localizedMessage ?: "Please check internet connection"}",
                                                            android.widget.Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                            },
                                            onNavigateToAdminRecords = { tabIndex -> navController.navigate(Screen.AdminRecords.createRoute(tabIndex)) },
                                            onNavigateToAdminStudyMaterial = { navController.navigate(Screen.AdminStudyMaterial.route) },
                                            onNavigateToAdminApprovals = { navController.navigate(Screen.AdminApprovals.route) },
                                            onNavigateToQuiz = { navController.navigate(Screen.QuizList.route) },
                                            onNavigateToAdminQuizzes = { navController.navigate(Screen.AdminQuizzes.route) },
                                            onNavigateToAdminStudents = { navController.navigate(Screen.AdminStudents.route) },
                                            onNavigateToAdminEnquiries = { navController.navigate(Screen.AdminEnquiries.route) }
                                        )
                                        BottomBarScreen.HOMEWORK -> HomeworkScreen(
                                            viewModel = dashboardViewModel,
                                            paddingValues = paddingValues,
                                            onNavigateBack = { currentTab = BottomBarScreen.HOME },
                                            onNavigateToLogin = {
                                                authViewModel.logout {
                                                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                                                }
                                            },
                                            onNavigateToSignup = {
                                                authViewModel.logout {
                                                    navController.navigate(Screen.Signup.route) { popUpTo(0) { inclusive = true } }
                                                }
                                            },
                                            onNavigateToAdminUpload = { navController.navigate(Screen.AdminHomework.route) }
                                        )
                                        BottomBarScreen.MATERIAL -> StudyMaterialScreen(
                                            viewModel = dashboardViewModel,
                                            paddingValues = paddingValues,
                                            onOpenDrawer = onOpenDrawer,
                                            onNavigateToUpload = { navController.navigate(Screen.AdminStudyMaterial.route) },
                                            onNavigateToLogin = {
                                                authViewModel.logout {
                                                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                                                }
                                            },
                                            onNavigateToSignup = {
                                                authViewModel.logout {
                                                    navController.navigate(Screen.Signup.route) { popUpTo(0) { inclusive = true } }
                                                }
                                            }
                                        )
                                        BottomBarScreen.QUIZ -> QuizListScreen(
                                            viewModel = dashboardViewModel,
                                            paddingValues = paddingValues,
                                            onNavigateToPlayQuiz = { quizId -> navController.navigate(Screen.QuizPlay.createRoute(quizId)) },
                                            onNavigateToAdminQuizzes = { navController.navigate(Screen.AdminQuizzes.route) },
                                            onBack = { currentTab = BottomBarScreen.HOME }
                                        )
                                        BottomBarScreen.ATTENDANCE -> {
                                            if (currentUser.isAdmin()) {
                                                AdminRecordsScreen(
                                                    viewModel = dashboardViewModel,
                                                    initialTab = 0,
                                                    outerPadding = paddingValues,
                                                    onBack = { currentTab = BottomBarScreen.HOME }
                                                )
                                            } else {
                                                AttendanceScreen(
                                                    viewModel = dashboardViewModel,
                                                    paddingValues = paddingValues,
                                                    onBack = { currentTab = BottomBarScreen.HOME }
                                                )
                                            }
                                        }
                                        BottomBarScreen.PROFILE -> ProfileScreen(
                                            viewModel = dashboardViewModel,
                                            paddingValues = paddingValues,
                                            onOpenDrawer = onOpenDrawer,
                                            onNavigateToLogin = {
                                                authViewModel.logout {
                                                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                                                }
                                            },
                                            onNavigateToSignup = {
                                                authViewModel.logout {
                                                    navController.navigate(Screen.Signup.route) { popUpTo(0) { inclusive = true } }
                                                }
                                            },
                                            onPasswordChangedAndRequireLogin = {
                                                authViewModel.logout {
                                                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                                                }
                                            },
                                            onDeleteAccount = {
                                                authViewModel.deleteAccount { res ->
                                                    res.onFailure { err ->
                                                        android.widget.Toast.makeText(
                                                            context,
                                                            "Account deletion failed: ${err.localizedMessage ?: "Please check internet connection"}",
                                                            android.widget.Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        composable(Screen.Fees.route) {
                            FeesScreen(
                                viewModel = dashboardViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Results.route) {
                            ResultsScreen(
                                viewModel = dashboardViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Attendance.route) {
                            AttendanceScreen(
                                viewModel = dashboardViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.OnDemand.route) {
                            OnDemandScreen(
                                viewModel = dashboardViewModel,
                                onBack = { navController.popBackStack() },
                                onNavigateToLogin = {
                                    authViewModel.logout {
                                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                                    }
                                },
                                onNavigateToSignup = {
                                    authViewModel.logout {
                                        navController.navigate(Screen.Signup.route) { popUpTo(0) { inclusive = true } }
                                    }
                                }
                            )
                        }

                        composable(Screen.AdminRecords.route) { backStackEntry ->
                            if (currentUser.isAdmin()) {
                                val tabIndex = backStackEntry.arguments?.getString("tabIndex")?.toIntOrNull() ?: 0
                                AdminRecordsScreen(
                                    viewModel = dashboardViewModel,
                                    initialTab = tabIndex,
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable(Screen.AdminStudents.route) {
                            if (currentUser.isAdmin()) {
                                AdminStudentsScreen(
                                    viewModel = dashboardViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable(Screen.AdminStudyMaterial.route) {
                            if (currentUser.isAdmin()) {
                                AdminStudyMaterialScreen(
                                    viewModel = dashboardViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }
                        composable(Screen.AdminApprovals.route) {
                            if (currentUser.isAdmin()) {
                                AdminApprovalsScreen(
                                    viewModel = dashboardViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable(Screen.QuizList.route) {
                            if (currentUser.isStudent() || currentUser.isGuest()) {
                                QuizListScreen(
                                    viewModel = dashboardViewModel,
                                    onNavigateToPlayQuiz = { quizId ->
                                        navController.navigate(Screen.QuizPlay.createRoute(quizId))
                                    },
                                    onNavigateToAdminQuizzes = { navController.navigate(Screen.AdminQuizzes.route) },
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.AdminQuizzes.route) {
                                        popUpTo(Screen.Dashboard.route)
                                    }
                                }
                            }
                        }

                        composable(Screen.QuizPlay.route) { backStackEntry ->
                            if (currentUser.isStudent() || currentUser.isGuest()) {
                                val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
                                QuizPlayScreen(
                                    viewModel = dashboardViewModel,
                                    quizId = quizId,
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable(Screen.PremiumMaterial.route) {
                            if (currentUser.isStudent() || currentUser.isGuest()) {
                                StudyMaterialScreen(
                                    viewModel = dashboardViewModel,
                                    paddingValues = PaddingValues(0.dp),
                                    onOpenDrawer = { navController.popBackStack() },
                                    onNavigateToUpload = { navController.navigate(Screen.AdminStudyMaterial.route) },
                                    isPremiumOnly = true
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable(Screen.AdminQuizzes.route) {
                            if (currentUser.isAdmin()) {
                                AdminQuizzesScreen(
                                    viewModel = dashboardViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable(Screen.AdminEnquiries.route) {
                            if (currentUser.isAdmin()) {
                                AdminEnquiriesScreen(
                                    viewModel = dashboardViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable(Screen.Homework.route) {
                            HomeworkScreen(
                                viewModel = dashboardViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToLogin = {
                                    authViewModel.logout {
                                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                                    }
                                },
                                onNavigateToSignup = {
                                    authViewModel.logout {
                                        navController.navigate(Screen.Signup.route) { popUpTo(0) { inclusive = true } }
                                    }
                                },
                                onNavigateToAdminUpload = { navController.navigate(Screen.AdminHomework.route) }
                            )
                        }

                        composable(Screen.AdminHomework.route) {
                            if (currentUser.isAdmin()) {
                                AdminHomeworkScreen(
                                    viewModel = dashboardViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) { popUpTo(0) { inclusive = true } }
                                }
                            }
                        }

                        composable(Screen.Notifications.route) {
                            NotificationCenterScreen(
                                viewModel = dashboardViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToRoute = { route ->
                                    try {
                                        navController.navigate(route)
                                    } catch (e: Exception) {
                                        // Ignore or fallback if dynamic notification route is unrecognized
                                    }
                                }
                            )
                        }

                        composable(Screen.AdminGuests.route) {
                            if (currentUser.isAdmin()) {
                                AdminGuestUsersScreen(
                                    viewModel = dashboardViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate(Screen.Dashboard.route) { popUpTo(0) { inclusive = true } }
                                }
                            }
                        }

                        composable(Screen.Downloads.route) {
                            DownloadsScreen(
                                viewModel = dashboardViewModel,
                                onBack = { navController.popBackStack() },
                                onNavigateToStudyMaterial = {
                                    navController.popBackStack()
                                },
                                onNavigateToHomework = {
                                    navController.navigate(Screen.Homework.route)
                                },
                                onNavigateToLogin = {
                                    authViewModel.logout {
                                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
}

