package com.example.enagar.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.enagar.presentation.ui.components.BottomNavBar
import com.example.enagar.presentation.ui.screens.HomeScreen
import com.example.enagar.presentation.ui.screens.MyReportsScreen
import com.example.enagar.presentation.ui.screens.NotificationScreen
import com.example.enagar.presentation.ui.screens.ProfileScreen
import com.example.enagar.presentation.ui.screens.ReportIssuesScreen
import com.example.enagar.presentation.ui.screens.ReportSubmittedScreen
import com.example.enagar.presentation.ui.screens.SignInScreen
import com.example.enagar.presentation.ui.screens.SignUpScreen

sealed class Screen(val route: String) {
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object ReportIssue : Screen("report_issue")
    object ReportSubmitted : Screen("report_submitted")
    object MyReports : Screen("my_reports")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")

    object FieldWorkerDashboard : Screen("field_worker_dashboard")
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.SignIn.route) {

        // Auth Screens (No BottomNavBar)
        composable(Screen.SignIn.route) { SignInScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }

        // Report Flow (No BottomNavBar)
        composable(Screen.ReportIssue.route) { ReportIssuesScreen(navController) }
        composable(Screen.ReportSubmitted.route) { ReportSubmittedScreen(navController) }

        // Main Screens (With BottomNavBar)
        composable(Screen.Home.route) {
            Scaffold(
                bottomBar = { BottomNavBar(navController) }
            ) { paddingValues ->
                HomeScreen(navController, Modifier.padding(paddingValues))
            }
        }

        composable(Screen.MyReports.route) {
            Scaffold(
                bottomBar = { BottomNavBar(navController) }
            ) { paddingValues ->
                MyReportsScreen(navController, Modifier.padding(paddingValues))
            }
        }

        composable(Screen.Notifications.route) {
            Scaffold(
                bottomBar = { BottomNavBar(navController) }
            ) { paddingValues ->
                NotificationScreen(navController, Modifier.padding(paddingValues))
            }
        }

        composable(Screen.Profile.route) {
            Scaffold(
                bottomBar = { BottomNavBar(navController) }
            ) { paddingValues ->
                ProfileScreen(navController, Modifier.padding(paddingValues))
            }

        }


        composable(Screen.FieldWorkerDashboard.route) {
            FieldWorkerDashboardScreen(navController)
        }
    }
}
