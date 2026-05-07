package com.example.enagar.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.enagar.presentation.ui.screens.*
import com.example.enagar.screens.*

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route
    ) {

        // 🔐 AUTH
        composable(Screen.SignIn.route) {
            SignInScreen(navController)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }


        // 👤 USER FLOW
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.MyReports.route) {
            MyReportsScreen(navController)
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.AadhaarVerification.route) {
            AadhaarVerificationScreen(navController)
        }


        // 📝 REPORT FLOW
        composable(Screen.ReportIssue.route) {
            ReportIssuesScreen(navController)
        }

        composable(
            route = Screen.ReportSubmitted.route
        ) {
            ReportSubmittedScreen(navController)
        }


        // 👷 FIELD WORKER DASHBOARD
        composable(Screen.FieldWorkerMain.route) {

            MainFieldWorkerScreen(
                mainNavController = navController
            )
        }


        // 📋 TASK DETAILS
        composable(Screen.TaskDetail.route) { backStackEntry ->

            val reportId =
                backStackEntry.arguments?.getString("taskId")
                    ?: ""

            TaskDetailScreen(
                navController = navController,
                reportId = reportId
            )
        }


        // ✅ OPTIONAL
        composable(Screen.ResolveIssue.route) { backStackEntry ->

            val reportId =
                backStackEntry.arguments?.getString("taskId")
                    ?: ""

            ResolveIssueScreen(
                navController = navController,
                taskId = reportId
            )
        }
    }
}