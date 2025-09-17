package com.example.enagar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.enagar.presentation.navigation.Screen


data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(
            Screen.Home.route, Icons.Default.Home, "Home"),
        BottomNavItem(Screen.MyReports.route, Icons.Default.Report, "Reports"),
        BottomNavItem(Screen.Notifications.route, Icons.Default.Notifications, "Notifications"),
        BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    val colorScheme = MaterialTheme.colorScheme

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) colorScheme.secondary else colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                },
                label = {
                    Text(
                        item.label,
                        color = if (isSelected) colorScheme.secondary else colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            )
        }
    }
}
