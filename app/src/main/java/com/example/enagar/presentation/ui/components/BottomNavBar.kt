package com.example.enagar.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
fun BottomNavBar(
    navController: NavHostController
) {

    val items = listOf(
        BottomNavItem(
            Screen.Home.route,
            Icons.Default.Home,
            "Home"
        ),
        BottomNavItem(
            Screen.MyReports.route,
            Icons.Default.Report,
            "Reports"
        ),
        BottomNavItem(
            Screen.Notifications.route,
            Icons.Default.Notifications,
            "Alerts"
        ),
        BottomNavItem(
            Screen.Profile.route,
            Icons.Default.Person,
            "Profile"
        )
    )

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentDestination: NavDestination? =
        navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor =
            MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {

        items.forEach { item ->

            val selected =
                currentDestination?.hierarchy?.any {
                    it.route == item.route
                } == true

            NavigationBarItem(

                selected = selected,

                onClick = {

                    navController.navigate(item.route) {

                        popUpTo(
                            navController.graph.startDestinationId
                        ) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },

                label = {
                    Text(
                        text = item.label
                    )
                },

                alwaysShowLabel = true,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}