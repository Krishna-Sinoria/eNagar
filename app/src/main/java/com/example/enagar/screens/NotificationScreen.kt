//package com.example.enagar.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NotificationScreen(navController: NavController, padding1: Modifier) {
//    Scaffold(
//        topBar = { TopAppBar(title = { Text("Notifications") }) }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp)
//        ) {
//            Text("No notifications yet.")
//        }
//    }
//}

package com.example.enagar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Dummy notification model
data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val type: String // Resolved, Comment, Update, Assigned, Received
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController, padding1: Modifier) {
    val notifications = remember {
        listOf(
            NotificationItem(
                1,
                "Report Resolved",
                "Your report regarding 'Pothole on Main St' has been marked as resolved by the city council.",
                "2 hours ago",
                "Resolved"
            ),
            NotificationItem(
                2,
                "New Comment",
                "A new comment has been added to your report: \"We are looking into this.\"",
                "Yesterday",
                "Comment"
            ),
            NotificationItem(
                3,
                "Report Update",
                "Your report 'Broken Streetlight' has been updated with new information.",
                "3 days ago",
                "Update"
            ),
            NotificationItem(
                4,
                "Report Assigned",
                "Your report 'Graffiti on Park Bench' has been assigned to a city official.",
                "1 week ago",
                "Assigned"
            ),
            NotificationItem(
                5,
                "Report Received",
                "Your report 'Overflowing Trash Can' has been received and is under review.",
                "2 weeks ago",
                "Received"
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2E7D32), Color(0xFF81C784))
                    )
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(notifications) { notification ->
                NotificationCard(notification)
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle icon placeholder (colored by type)
            val circleColor = when (notification.type) {
                "Resolved" -> Color(0xFF2E7D32) // green
                "Comment" -> Color(0xFF1976D2)  // blue
                "Update" -> Color(0xFFD28F18)   // orange
                "Assigned" -> Color(0xFF6A1B9A) // purple
                "Received" -> Color(0xFF455A64) // grey
                else -> MaterialTheme.colorScheme.primary
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(circleColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(circleColor, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title + message + time
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = notification.time,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

