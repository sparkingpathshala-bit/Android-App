package com.sparkingpathshala.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Dashboard : Screen("dashboard")
    
    // Sub-screens under dashboard navigation
    object Fees : Screen("fees")
    object Results : Screen("results")
    object Attendance : Screen("attendance")
    object OnDemand : Screen("ondemand")
    object Homework : Screen("homework")
    object AdminHomework : Screen("admin_homework")
    object Notifications : Screen("notifications")
    object AdminGuests : Screen("admin_guests")
    object AdminRecords : Screen("admin_records/{tabIndex}") {
        fun createRoute(tabIndex: Int) = "admin_records/$tabIndex"
    }
    object AdminStudyMaterial : Screen("admin_study_material")
    object AdminApprovals : Screen("admin_approvals")
    object AdminQuizzes : Screen("admin_quizzes")
    object AdminStudents : Screen("admin_students")
    object QuizList : Screen("quiz_list")
    object QuizPlay : Screen("quiz_play/{quizId}") {
        fun createRoute(quizId: String) = "quiz_play/$quizId"
    }
    object AdminEnquiries : Screen("admin_enquiries")
    object PremiumMaterial : Screen("premium_material")
    object Downloads : Screen("downloads")
}

enum class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Default.Home),
    MATERIAL("material", "Library", Icons.Default.Book),
    HOMEWORK("homework_tab", "Homework", Icons.AutoMirrored.Filled.Assignment),
    QUIZ("quiz", "Quiz", Icons.Default.Extension),
    ATTENDANCE("attendance_tab", "Attendance", Icons.Default.CheckCircle),
    PROFILE("profile", "Profile", Icons.Default.Person)
}

