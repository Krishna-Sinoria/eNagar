package com.example.enagar.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.enagar.R
import com.example.enagar.components.BottomNavBar
import com.example.enagar.presentation.navigation.Screen

// Data class for reports
data class HomeReport(
    val id: Int,
    val title: String,
    val status: String,
    val icon: ImageVector,
    val iconBgColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val colorScheme = MaterialTheme.colorScheme

    // Dummy data
    val recentReports = listOf(
        HomeReport(1, "Pothole on Main Street", "Reported", Icons.Default.ReportProblem, colorScheme.error),
        HomeReport(2, "Streetlight Outage", "In Progress", Icons.Default.Lightbulb, colorScheme.secondary),
        HomeReport(3, "Broken Park Bench", "Resolved", Icons.Default.Done, colorScheme.primary)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Citizen",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = "Language",
                            tint = colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomNavBar(navController as NavHostController)
        },
        containerColor = colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Welcome header
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Welcome HackSmiths!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
                Text(
                    text = "Report issues in your community",
                    fontSize = 16.sp,
                    color = colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            // Map + Search
            item { MapSearchSection() }

            // Report button
            item {
                Button(
                    onClick = { navController.navigate(Screen.ReportIssue.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Report", modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report an Issue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Recent reports header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Recent Reports",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "View All Reports",
                        tint = colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Report cards
            items(recentReports) { report ->
                ReportCard(report)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun MapSearchSection() {
    var searchQuery by remember { mutableStateOf("") }
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.mapup),
                contentDescription = "Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search location") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape = CircleShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface.copy(alpha = 0.95f),
                    unfocusedContainerColor = colorScheme.surface.copy(alpha = 0.95f),
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface,
                    cursorColor = colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun ReportCard(report: HomeReport) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(report.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = report.icon,
                    contentDescription = report.title,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(report.title, fontWeight = FontWeight.Bold, color = colorScheme.onBackground, fontSize = 16.sp)
                Text(report.status, color = colorScheme.onBackground.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        }
    }
}
