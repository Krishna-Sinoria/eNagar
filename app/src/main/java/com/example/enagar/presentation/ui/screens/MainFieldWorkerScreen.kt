package com.example.enagar.presentation.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.enagar.presentation.navigation.Screen


/**
 * Main screen for the Field Worker with Scaffold and Bottom Navigation bar.
 */
@Composable
fun MainFieldWorkerScreen(mainNavController: NavHostController,
                          workerEmail: String) {
    val workerNavController = rememberNavController()

    Scaffold(
        bottomBar = { FieldWorkerBottomNavBar(navController = workerNavController) }
    ) { innerPadding ->
        NavHost(
            navController = workerNavController,
            startDestination = Screen.FieldWorkerDashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.FieldWorkerDashboard.route) {
                FieldWorkerDashboardScreen(navController = mainNavController)
            }
            composable(Screen.FieldWorkerNotifications.route) { FieldWorkerNotificationScreen(navController = mainNavController) }
            composable(Screen.FieldWorkerProfile.route) { FieldWorkerProfileScreen(navController = mainNavController, workerEmail = workerEmail) }
        }
    }
}

// Data class for bottom nav items
private data class WorkerBottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Bottom Navigation Bar styled with Material3 theme.
 */
@Composable
private fun FieldWorkerBottomNavBar(navController: NavController) {
    val items = listOf(
        WorkerBottomNavItem("Tasks", Icons.Default.Task, Screen.FieldWorkerDashboard.route),
        WorkerBottomNavItem("Notifications", Icons.Default.Notifications, Screen.FieldWorkerNotifications.route),
        WorkerBottomNavItem("Profile", Icons.Default.Person, Screen.FieldWorkerProfile.route)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.background,
                    selectedTextColor = MaterialTheme.colorScheme.background,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
