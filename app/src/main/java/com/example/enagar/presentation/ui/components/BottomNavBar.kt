//package com.example.enagar.components
//
//
//
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Report
//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.navigation.NavDestination
//import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.example.enagar.navigation.Screen
//
//data class BottomNavItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)
//
//@Composable
//fun BottomNavBar(navController: NavHostController) {
//    val items = listOf(
//        BottomNavItem(Screen.Home.route, Icons.Default.Home, "Home"),
//        BottomNavItem(Screen.MyReports.route, Icons.Default.Report, "Reports"),
//        BottomNavItem(Screen.Notifications.route, Icons.Default.Notifications, "Notifications"),
//        BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profile")
//    )
//
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination: NavDestination? = navBackStackEntry?.destination
//
//    NavigationBar {
//        items.forEach { item ->
//            NavigationBarItem(
//                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
//                onClick = {
//                    if (currentDestination?.route != item.route) {
//                        navController.navigate(item.route) {
//                            popUpTo(Screen.Home.route) { saveState = true }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
//                },
//                icon = { Icon(item.icon, contentDescription = item.label) },
//                label = { Text(item.label) }
//            )
//        }
//    }
//}
//

package com.example.enagar.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.enagar.presentation.navigation.Screen

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(Screen.Home.route, Icons.Default.Home, "Home"),
        BottomNavItem(Screen.MyReports.route, Icons.Default.Report, "Reports"),
        BottomNavItem(Screen.Notifications.route, Icons.Default.Notifications, "Notifications"),
        BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    val isDarkTheme = isSystemInDarkTheme()

    // Gradient background for bottom bar
    val gradientBrush = Brush.horizontalGradient(
        colors = if (isDarkTheme) {
            listOf(Color(0xFF1B5E20), Color(0xFF388E3C)) // darker gradient
        } else {
            listOf(Color(0xFF2E7D32), Color(0xFF81C784)) // lighter gradient
        }
    )

    val selectedColor = Color(0xFF81C784) // Light green
    val unselectedColor = if (isDarkTheme) Color.LightGray else Color.Black

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = gradientBrush,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ),
        containerColor = Color.Transparent,
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
                        tint = if (isSelected) selectedColor else unselectedColor
                    )
                },
                label = {
                    Text(
                        item.label,
                        color = if (isSelected) selectedColor else unselectedColor
                    )
                }
            )
        }
    }
}

