package com.example.enagar.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.enagar.domain.models.Report
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.viewModel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldWorkerNotificationScreen(

    navController: NavHostController

) {

    val viewModel: WorkerViewModel =
        hiltViewModel()

    val reports by viewModel.reports.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    val error by viewModel.error.collectAsState()

    val context = navController.context

    LaunchedEffect(Unit) {

        viewModel.fetchAssignedReports(context)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Box(

                        modifier = Modifier.fillMaxWidth(),

                        contentAlignment = Alignment.Center
                    ) {

                        Text(

                            text = "Notifications",

                            style =
                                MaterialTheme.typography.titleLarge,

                            color =
                                MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(

                        containerColor =
                            MaterialTheme.colorScheme.primary
                    )
            )
        }

    ) { padding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {

                isLoading -> {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.align(Alignment.Center)
                    )
                }

                error != null -> {

                    Text(

                        text = error ?: "Unknown Error",

                        color = Color.Red,

                        modifier =
                            Modifier.align(Alignment.Center)
                    )
                }

                reports.isEmpty() -> {

                    Text(

                        text = "No Notifications",

                        modifier =
                            Modifier.align(Alignment.Center)
                    )
                }

                else -> {

                    LazyColumn(

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)

                    ) {

                        items(reports) { report ->

                            NotificationItem(

                                report = report,

                                onClick = {

                                    navController.navigate(

                                        Screen.TaskDetail
                                            .createRoute(
                                                report._id
                                            )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(

    report: Report,

    onClick: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                onClick()
            },

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),

        shape =
            MaterialTheme.shapes.medium

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment =
                Alignment.Top
        ) {

            Box(

                modifier = Modifier
                    .size(40.dp)
                    .background(

                        MaterialTheme.colorScheme.primary,

                        CircleShape
                    ),

                contentAlignment =
                    Alignment.Center

            ) {

                Icon(

                    imageVector =
                        Icons.Default.NotificationsActive,

                    contentDescription = null,

                    tint = Color.White,

                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text =
                        "${report.problem_type} assigned to you",

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = report.description
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(

                    text = "Status : ${report.status}",

                    color =
                        MaterialTheme.colorScheme.primary,

                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}