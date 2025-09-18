package com.example.enagar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.enagar.presentation.navigation.Screen

// --- Dummy Data Models and Source ---
data class Task(
    val id: String,
    val title: String,
    val location: String,
    val assignedTime: String,
    val priority: Priority
)

enum class Priority { High, Medium, Low }

val assignedTasks = listOf(
    Task("E12345", "Street Light Not Working", "Plot 123, Sector 17, Gandhinagar", "10:45 AM, 26 Jul 2024", Priority.High),
    Task("E12346", "Power Outage in Area", "Main Road, Infocity, Gandhinagar", "09:15 AM, 26 Jul 2024", Priority.High),
    Task("E12347", "Street Light Malfunction", "Near GH-4 Circle, Gandhinagar", "08:30 AM, 25 Jul 2024", Priority.Medium)
)

val completedTasks = listOf(
    Task("E12340", "Broken Pavement", "Kudasan, Gandhinagar", "04:30 PM, 24 Jul 2024", Priority.Medium),
    Task("E12341", "Water Pipe Leakage", "Sector 21, Gandhinagar", "11:00 AM, 24 Jul 2024", Priority.Low)
)
// --- End of Dummy Data ---


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldWorkerDashboardScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Assigned Tasks", "Completed Tasks")

    val brown = Color(0xFF6D4C41)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "HackSmith!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Electricity Dept.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Navigate to notifications */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = brown,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = brown,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = brown,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = if (selectedTab == index) brown else Color.Gray
                            )
                        },
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            val tasksToShow = if (selectedTab == 0) assignedTasks else completedTasks
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasksToShow) { task ->
                    TaskCard(task = task, navController = navController, brown = brown)
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, navController: NavHostController, brown: Color) {
    val priorityColor = when (task.priority) {
        Priority.High -> Color(0xFFFFEBEE)
        Priority.Medium -> Color(0xFFFFF3E0)
        Priority.Low -> Color(0xFFE8F5E9)
    }
    val priorityTextColor = when (task.priority) {
        Priority.High -> Color(0xFFD32F2F)
        Priority.Medium -> Color(0xFFF57C00)
        Priority.Low -> Color(0xFF388E3C)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.TaskDetail.createRoute(task.id)) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Assigned: ${task.assignedTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(priorityColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = task.priority.name,
                        color = priorityTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Task ID: #${task.id}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = brown
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = task.location, style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.navigate(Screen.TaskDetail.createRoute(task.id)) }
                ) {
                    Text(
                        text = "View Details",
                        color = brown,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = brown
                    )
                }
            }
        }
    }
}
