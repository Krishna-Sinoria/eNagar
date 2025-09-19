package com.example.enagar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.enagar.components.BottomNavBar

data class MyReport(
    val id: Int,
    val title: String,
    val status: String, // "Pending", "In Progress", "Resolved"
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsScreen(navController: NavController) {
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

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Reports",
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colorScheme.primary)
            )
        },
        bottomBar = { BottomNavBar(navController as NavHostController) },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Scrollable tabs to show full text
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = colorScheme.surface,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = colorScheme.primary,
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
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedTabIndex == index)
                                    colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.7f)
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
                        color = colorScheme.onSurfaceVariant,
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
                            cardBg = colorScheme.surface,
                            onClick = {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
