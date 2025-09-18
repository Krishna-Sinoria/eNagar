package com.example.enagar.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.enagar.components.BottomNavBar
import com.example.enagar.presentation.ui.screens.*
import com.example.enagar.screens.MyReportsScreen
import com.example.enagar.screens.NotificationScreen
import com.example.enagar.screens.ProfileScreen
import com.example.enagar.screens.ReportSubmittedScreen
import com.example.enagar.screens.SignInScreen
import com.example.enagar.screens.SignUpScreen



@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.SignIn.route) {

        // ---------- AUTH SCREENS ----------
        composable(Screen.SignIn.route) { SignInScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }
        // Role USER
        // ---------- MAIN USER SCREENS ----------
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.MyReports.route) {MyReportsScreen(navController)}
        composable(Screen.Notifications.route) { NotificationScreen(navController)}
        composable(Screen.Profile.route) {   ProfileScreen(navController) }
        composable (Screen.AadhaarVerification.route){ AadhaarVerificationScreen(navController) }
        // ---------- REPORT FLOW ----------
        composable(Screen.ReportIssue.route) { ReportIssuesScreen(navController) }
        composable(Screen.ReportSubmitted.route) { ReportSubmittedScreen(navController) }

        // Role FIELD WORKER
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
