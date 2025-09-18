package com.example.enagar.presentation.navigation

sealed class Screen(val route: String) {
    // Auth
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")

    // Main User Screens
    object Home : Screen("home")
    object MyReports : Screen("my_reports")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")

    // Report Flow
    object ReportIssue : Screen("report_issue")
    object ReportSubmitted : Screen("report_submitted")

    // Field Worker Screens
    object FieldWorkerMain : Screen("field_worker_main") // BottomNav container
    object FieldWorkerDashboard : Screen("field_worker_dashboard")
    object FieldWorkerNotifications : Screen("field_worker_notifications")
    object FieldWorkerProfile : Screen("field_worker_profile")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    object ResolveIssue : Screen("resolve_issue/{taskId}") {
        fun createRoute(taskId: String) = "resolve_issue/$taskId"
    }

    object AadhaarVerification : Screen("aadhaar_verification")
}