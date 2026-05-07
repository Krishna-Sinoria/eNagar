package com.example.enagar.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.enagar.domain.models.Report
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.viewModel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldWorkerDashboardScreen(
    navController: NavHostController
) {

    val viewModel: WorkerViewModel = hiltViewModel()

    val reports by viewModel.reports.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    val error by viewModel.error.collectAsState()

    val context = navController.context

    val brown = Color(0xFF6D4C41)

    // 🔥 Auto Fetch Reports
    LaunchedEffect(Unit) {

        viewModel.fetchAssignedReports(context)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Field Worker Dashboard",
                        color = Color.White
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = brown
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

                // 🔄 Loading
                isLoading -> {

                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // ❌ Error
                error != null -> {

                    Text(

                        text = error ?: "Unknown Error",

                        color = Color.Red,

                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // 📭 Empty
                reports.isEmpty() -> {

                    Text(

                        text = "No Assigned Reports",

                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // ✅ Reports List
                else -> {

                    LazyColumn(

                        modifier = Modifier.fillMaxSize(),

                        contentPadding = PaddingValues(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(14.dp)

                    ) {

                        items(reports) { report ->

                            ReportCard(

                                report = report,

                                brown = brown,

                                navController = navController,

                                onStartWork = {

                                    viewModel.startWork(

                                        context,

                                        report._id

                                    ) {

                                        // 🔄 Refresh
                                        viewModel.fetchAssignedReports(
                                            context
                                        )
                                    }
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
fun ReportCard(

    report: Report,

    brown: Color,

    navController: NavHostController,

    onStartWork: () -> Unit

) {

    // 🔥 Button States
    var startClicked by remember {

        mutableStateOf(false)
    }

    var uploadClicked by remember {

        mutableStateOf(false)
    }

    // 🔥 Status Checks
    val isStarted =
        report.status == "In Progress"

    val isVerificationPending =
        report.status == "Verification Pending"

    val isCompleted =
        report.status == "Completed"

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )

    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // 🏷 Problem Type
            Text(

                text = report.problem_type.ifEmpty {
                    "No Problem Type"
                },

                style = MaterialTheme.typography.titleLarge,

                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 📝 Description
            Text(

                text = report.description.ifEmpty {
                    "No Description"
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🖼 Report Image
            if (!report.image.isNullOrEmpty()) {

                AsyncImage(

                    model = report.image,

                    contentDescription = null,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),

                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // 📌 Status
            Text(

                text = "Status : ${report.status}",

                color = brown,

                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 📍 Location
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = brown
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${report.lat}, ${report.lng}"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ▶️ START WORK BUTTON
            Button(

                onClick = {

                    startClicked = true

                    onStartWork()
                },

                enabled = !startClicked &&
                        !isStarted &&
                        !isVerificationPending &&
                        !isCompleted,

                colors = ButtonDefaults.buttonColors(
                    containerColor = brown
                )

            ) {

                Text(

                    when {

                        isCompleted ->
                            "Completed"

                        isVerificationPending ->
                            "Verification Pending"

                        isStarted ->
                            "Work Started"

                        else ->
                            "Start Work"
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 📤 Upload Completion Proof
            Text(

                text = when {

                    isCompleted ->
                        "Completed"

                    isVerificationPending ->
                        "Proof Uploaded"

                    else ->
                        "Upload Completion Proof"
                },

                color = if (
                    uploadClicked ||
                    isVerificationPending
                )
                    Color.Gray
                else
                    brown,

                fontWeight = FontWeight.Bold,

                modifier = Modifier.clickable(

                    enabled = !uploadClicked &&
                            !isVerificationPending &&
                            !isCompleted

                ) {

                    uploadClicked = true

                    navController.navigate(

                        Screen.TaskDetail.createRoute(
                            report._id
                        )
                    )
                }
            )
        }
    }
}