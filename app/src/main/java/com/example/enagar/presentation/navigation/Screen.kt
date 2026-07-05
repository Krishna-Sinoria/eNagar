package com.example.enagar.presentation.navigation

sealed class Screen(val route: String) {

    // 🔐 AUTH
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")


    // 👤 USER
    object Home : Screen("home")
    object MyReports : Screen("my_reports")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")


    // 📝 REPORT FLOW
    object ReportIssue : Screen("report_issue")
    object ReportSubmitted : Screen("report_submitted/{reportId}") {

        fun createRoute(reportId: String): String {
            return "report_submitted/$reportId"
        }
    }
    object AadhaarVerification : Screen("aadhaar_verification")


    // 👷 FIELD WORKER
    object FieldWorkerMain :
        Screen("field_worker_main/{workerEmail}") {

        fun createRoute(workerEmail: String): String {

            return "field_worker_main/$workerEmail"
        }
    }

    object FieldWorkerDashboard :
        Screen("field_worker_dashboard")

    object FieldWorkerNotifications :
        Screen("field_worker_notifications")

    object FieldWorkerProfile :
        Screen("field_worker_profile")


    // 📋 TASK DETAIL
    object TaskDetail :
        Screen("task_detail/{taskId}") {

        fun createRoute(taskId: String): String {
            return "task_detail/$taskId"
        }
    }


    // ✅ RESOLVE ISSUE
    object ResolveIssue :
        Screen("resolve_issue/{taskId}") {

        fun createRoute(taskId: String): String {
            return "resolve_issue/$taskId"
        }
    }
}