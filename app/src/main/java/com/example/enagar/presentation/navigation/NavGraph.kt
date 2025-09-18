package com.example.enagar.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.enagar.presentation.ui.screens.*
import com.example.enagar.screens.MyReportsScreen
import com.example.enagar.screens.NotificationScreen
import com.example.enagar.screens.ProfileScreen
import com.example.enagar.screens.ReportSubmittedScreen
import com.example.enagar.screens.SignInScreen
import com.example.enagar.screens.SignUpScreen

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
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.SignIn.route) {

        // ---------- AUTH SCREENS ----------
        composable(Screen.SignIn.route) { SignInScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }

        // ---------- REPORT FLOW ----------
        composable(Screen.ReportIssue.route) { ReportIssuesScreen(navController) }
        composable(Screen.ReportSubmitted.route) { ReportSubmittedScreen(navController) }

        // ---------- MAIN USER SCREENS ----------
        composable(Screen.Home.route) {
            Scaffold(bottomBar = { BottomNavBar(navController) }) { paddingValues ->
                HomeScreen(navController, Modifier.padding(paddingValues))
            }
        }
        composable(Screen.MyReports.route) {
            Scaffold(bottomBar = { BottomNavBar(navController) }) { paddingValues ->
                MyReportsScreen(navController, Modifier.padding(paddingValues))
            }
        }
        composable(Screen.Notifications.route) {
            Scaffold(bottomBar = { BottomNavBar(navController) }) { paddingValues ->
                NotificationScreen(navController, Modifier.padding(paddingValues))
            }
        }
        composable(Screen.Profile.route) {
            Scaffold(bottomBar = { BottomNavBar(navController) }) { paddingValues ->
                ProfileScreen(navController, Modifier.padding(paddingValues))
            }
        }

        // ---------- FIELD WORKER FLOW ----------
        composable(Screen.FieldWorkerMain.route) {
            // Main container with its own bottom nav
            MainFieldWorkerScreen(mainNavController = navController)
        }

        // Detail screens for tasks (without bottom nav)
        composable(Screen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            TaskDetailScreen(navController = navController, taskId = taskId)
        }
        composable(Screen.ResolveIssue.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            ResolveIssueScreen(navController = navController, taskId = taskId)
        }
    }
}
