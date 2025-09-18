package com.example.enagar.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
fun BottomNavBar1(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(Screen.Home.route, Icons.Default.Home, "Home"),
        BottomNavItem(Screen.MyReports.route, Icons.Default.Report, "Reports"),
        BottomNavItem(Screen.Notifications.route, Icons.Default.Notifications, "Notifications"),
        BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    val colorScheme = MaterialTheme.colorScheme

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true, // 👈 Always false (no selection effect)
                onClick = {
                    if (currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label, tint = colorScheme.onSurfaceVariant) },
                label = { Text(item.label, color = colorScheme.onSurfaceVariant) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorScheme.onSurfaceVariant,
                    selectedTextColor = colorScheme.onSurfaceVariant,
                    unselectedIconColor = colorScheme.onSurfaceVariant,
                    unselectedTextColor = colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent // no highlight bubble
                )
            )
        }
    }
}


// Reusable bottom nav
@Composable
fun BottomNavBar(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val items = listOf(
        Screen.Home.route to Icons.Default.Home,
        Screen.MyReports.route to Icons.Default.List,
        Screen.Notifications.route to Icons.Default.Notifications,
        Screen.Profile.route to Icons.Default.Person
    )
    var selectedItem by remember { mutableStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, (route, icon) ->
            val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
            NavigationBarItem(
                selected =  selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        icon,
                        contentDescription = route,
                        tint = if (selectedItem == index) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                },
                label = {
                    Text(
                        route.substringAfterLast('.'),
                        color = if (selectedItem == index) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
            )
        }
    }
}
