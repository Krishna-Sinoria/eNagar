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
//fun MyReportsScreen(navController: NavController, padding1: Modifier) {
//    Scaffold(
//        topBar = { TopAppBar(title = { Text("My Reports") }) }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp)
//        ) {
//            Text("Here you will see all your submitted reports.")
//        }
//    }
//}
//
//

package com.example.enagar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// local / screen-specific data class to avoid conflict with other files
data class MyReport(
    val id: Int,
    val title: String,
    val status: String, // "Pending", "In Progress", "Resolved"
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsScreen(navController: NavController, padding1: Modifier) {
    // Dummy data
    val allReports = remember {
        listOf(
            MyReport(201, "Pothole on Main Street near Bus Stop", "Pending", "2025-09-12"),
            MyReport(202, "Streetlight Outage on Elm Avenue", "In Progress", "2025-09-11"),
            MyReport(203, "Broken Park Bench (north side)", "Resolved", "2025-09-10"),
            MyReport(204, "Graffiti on Building wall", "Pending", "2025-09-09"),
            MyReport(205, "Overflowing Trash Can outside mall", "Resolved", "2025-09-08"),
            MyReport(206, "Damaged Road Sign by Junction", "In Progress", "2025-09-07")
        )
    }

    // Tabs
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Pending", "In Progress", "Resolved")

    // Filtering
    val filteredReports = remember(selectedTabIndex) {
        when (tabs[selectedTabIndex]) {
            "Pending" -> allReports.filter { it.status == "Pending" }
            "In Progress" -> allReports.filter { it.status == "In Progress" }
            "Resolved" -> allReports.filter { it.status == "Resolved" }
            else -> allReports
        }
    }

    // Theme-aware background colors (match previous screens)
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color(0xFF121212) else Color(0xFFF6F7F9)
    val cardBgColor = if (isDark) Color(0xFF1F1F1F) else Color.White
    val surfaceVariant = if (isDark) Color(0xFF242424) else Color(0xFFF0F2F5)

    Scaffold(
        topBar = {
            // Gradient top bar consistent with sign-in/sign-up
            CenterAlignedTopAppBar(
                title = { Text("My Reports", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(listOf(Color(0xFF2E7D32), Color(0xFF81C784)))
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Tabs — use surface color for tab row to feel connected to cards
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = surfaceVariant,
                indicator = { tabPositions ->
                    // custom indicator color
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFF2E7D32),
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, t ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = t,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 14.sp,
                                color = if (selectedTabIndex == index) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredReports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reports in this category.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredReports) { report ->
                        MyReportCard(
                            report = report,
                            cardBg = cardBgColor,
                            onClick = {
                                // navigate to detail if you have route (example)
                                navController.navigate("report_detail/${report.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyReportCard(report: MyReport, cardBg: Color, onClick: () -> Unit) {
    val statusColor = when (report.status) {
        "Pending" -> Color(0xFF1976D2)      // blue
        "In Progress" -> Color(0xFFD28F18)  // orange
        "Resolved" -> Color(0xFF2E7D32)     // green
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // main content (title + meta)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ID and date on one column (left)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ID: ${report.id}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = report.date,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // status chip (keeps on one line and won't wrap)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = report.status,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
