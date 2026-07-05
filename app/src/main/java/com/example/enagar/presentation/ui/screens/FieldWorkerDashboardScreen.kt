package com.example.enagar.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.enagar.domain.models.Report
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.viewModel.WorkerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Status color helper ───────────────────────────────────────────────────────
private fun statusColor(status: String?): Color = when (status) {
    "Assigned"             -> Color(0xFFE65100)
    "In Progress"          -> Color(0xFF1565C0)
    "Verification Pending" -> Color(0xFF6A1B9A)
    "Completed"            -> Color(0xFF2E7D32)
    "Rejected"             -> Color(0xFFB71C1C)
    else                   -> Color(0xFF607D8B)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldWorkerDashboardScreen(
    navController: NavHostController
) {
    // ── ViewModel + state — UNTOUCHED ─────────────────────────────────────────
    val viewModel: WorkerViewModel = hiltViewModel()
    val reports   by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error     by viewModel.error.collectAsState()
    val context   = navController.context

    // ── Pull-to-refresh state ─────────────────────────────────────────────────
    val pullState    = rememberPullToRefreshState()
    val scope        = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    // ── Auto-refresh every 30 seconds ─────────────────────────────────────────
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.fetchAssignedReports(context)
            delay(30_000L)
        }
    }

    // ── Refresh on screen resume (coming back from TaskDetail etc.) ───────────
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchAssignedReports(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val colorScheme  = MaterialTheme.colorScheme
    val listState    = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "My Assignments",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            color      = colorScheme.onPrimary
                        )
                        Text(
                            "${reports.size} task${if (reports.size != 1) "s" else ""} assigned",
                            fontSize = 11.sp,
                            color    = colorScheme.onPrimary.copy(alpha = 0.75f)
                        )
                    }
                },
                actions = {
                    // Manual refresh button
                    IconButton(
                        onClick = {
                            scope.launch {
                                isRefreshing = true
                                viewModel.fetchAssignedReports(context)
                                delay(800)
                                isRefreshing = false
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint               = colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary
                )
            )
        },
        containerColor = colorScheme.background
    ) { padding ->

        // ── Pull-to-refresh wrapper ───────────────────────────────────────────
        PullToRefreshBox(
            modifier     = Modifier
                .fillMaxSize()
                .padding(padding),
            state        = pullState,
            isRefreshing = isRefreshing || isLoading,
            onRefresh    = {
                scope.launch {
                    isRefreshing = true
                    viewModel.fetchAssignedReports(context)
                    delay(800)
                    isRefreshing = false
                }
            }
        ) {
            when {
                // ── Loading (first load) ──────────────────────────────────────
                isLoading && reports.isEmpty() -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Loading your tasks…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ── Error ─────────────────────────────────────────────────────
                error != null && reports.isEmpty() -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.padding(32.dp)
                        ) {
                            Box(
                                modifier         = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.errorContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.WifiOff,
                                    contentDescription = null,
                                    tint               = colorScheme.error,
                                    modifier           = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Couldn't load tasks",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color      = colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                error ?: "Unknown error",
                                style     = MaterialTheme.typography.bodySmall,
                                color     = colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { viewModel.fetchAssignedReports(context) },
                                shape   = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Try Again")
                            }
                        }
                    }
                }

                // ── Empty ─────────────────────────────────────────────────────
                reports.isEmpty() -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.padding(32.dp)
                        ) {
                            Box(
                                modifier         = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AssignmentTurnedIn,
                                    contentDescription = null,
                                    tint               = colorScheme.primary,
                                    modifier           = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No tasks assigned yet",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color      = colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Pull down to check for new assignments",
                                style     = MaterialTheme.typography.bodySmall,
                                color     = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ── Report list ───────────────────────────────────────────────
                else -> {
                    LazyColumn(
                        state               = listState,
                        modifier            = Modifier.fillMaxSize(),
                        contentPadding      = PaddingValues(
                            horizontal = 16.dp,
                            vertical   = 14.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = reports,
                            key   = { it._id }
                        ) { report ->
                            AnimatedVisibility(
                                visible = true,
                                enter   = fadeIn() + slideInVertically { 30 }
                            ) {
                                WorkerReportCard(
                                    report        = report,
                                    navController = navController,
                                    onStartWork   = {
                                        viewModel.startWork(context, report._id) {
                                            viewModel.fetchAssignedReports(context)
                                        }
                                    }
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

// ── Worker Report Card ────────────────────────────────────────────────────────
@Composable
fun WorkerReportCard(
    report:        Report,
    navController: NavHostController,
    onStartWork:   () -> Unit
) {
    var startClicked  by remember { mutableStateOf(false) }
    var uploadClicked by remember { mutableStateOf(false) }

    val isStarted             = report.status == "In Progress"
    val isVerificationPending = report.status == "Verification Pending"
    val isCompleted           = report.status == "Completed"
    val isRejected            = report.status == "Rejected"

    val sColor      = statusColor(report.status)
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {

            // ── Image header (if available) ───────────────────────────────────
            if (!report.image.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
                    AsyncImage(
                        model              = report.image,
                        contentDescription = null,
                        modifier           = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale       = ContentScale.Crop
                    )
                    // Dark gradient overlay at bottom of image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                                )
                            )
                    )
                    // Status badge on image
                    StatusBadge(
                        status = report.status,
                        color = sColor,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    )
                    // Problem type on image bottom
                    Text(
                        text       = report.problem_type.ifEmpty { "Unspecified Issue" },
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        modifier   = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                }
            } else {
                // No image — coloured top strip + status badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(sColor, sColor.copy(alpha = 0.6f))
                            ),
                            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = report.problem_type.ifEmpty { "Unspecified Issue" },
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White,
                            modifier   = Modifier.weight(1f),
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        StatusBadge(
                            status = report.status,
                            color = Color.White.copy(alpha = 0.22f),
                            textColor = Color.White
                        )
                    }
                }
            }

            // ── Card body ─────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(16.dp)) {

                // Problem type heading (only if image was shown)
                if (!report.image.isNullOrEmpty()) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = report.problem_type.ifEmpty { "Unspecified Issue" },
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = colorScheme.onSurface,
                            modifier   = Modifier.weight(1f),
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        StatusBadge(status = report.status, color = sColor)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Description
                if (report.description.isNotEmpty()) {
                    Text(
                        text     = report.description,
                        style    = MaterialTheme.typography.bodySmall,
                        color    = colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ── Rejection message ─────────────────────────────────────────
                if (isRejected && !report.rejectionMessage.isNullOrEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Row(
                            modifier          = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = null,
                                tint               = Color(0xFFB71C1C),
                                modifier           = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Rejection Reason",
                                    style      = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color      = Color(0xFFB71C1C)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    report.rejectionMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF7F1A1A)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ── Location row ──────────────────────────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint               = colorScheme.primary,
                            modifier           = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text     = "${report.lat}, ${report.lng}",
                        style    = MaterialTheme.typography.bodySmall,
                        color    = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.3f))

                Spacer(modifier = Modifier.height(14.dp))

                // ── Action buttons ────────────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    // Start Work button — ORIGINAL LOGIC UNTOUCHED
                    Button(
                        onClick  = {
                            startClicked = true
                            onStartWork()
                        },
                        enabled  = !startClicked && !isStarted &&
                                !isVerificationPending && !isCompleted,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = colorScheme.primary,
                            contentColor           = colorScheme.onPrimary,
                            disabledContainerColor = colorScheme.surfaceVariant,
                            disabledContentColor   = colorScheme.onSurfaceVariant
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                isCompleted || isVerificationPending -> Icons.Default.CheckCircle
                                isStarted -> Icons.Default.PlayCircleFilled
                                else -> Icons.Default.PlayArrow
                            },
                            contentDescription = null,
                            modifier           = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text  = when {
                                isCompleted           -> "Completed"
                                isVerificationPending -> "Verifying"
                                isStarted             -> "Started"
                                else                  -> "Start Work"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    // Upload Proof button — ORIGINAL LOGIC UNTOUCHED
                    OutlinedButton(
                        onClick  = {
                            uploadClicked = true
                            navController.navigate(
                                Screen.TaskDetail.createRoute(report._id)
                            )
                        },
                        enabled  = !uploadClicked && !isVerificationPending && !isCompleted,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(
                            contentColor         = colorScheme.primary,
                            disabledContentColor = colorScheme.onSurfaceVariant
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (!uploadClicked && !isVerificationPending && !isCompleted)
                                colorScheme.primary
                            else
                                colorScheme.outline.copy(alpha = 0.4f)
                        )
                    ) {
                        Icon(
                            imageVector = when {
                                isCompleted           -> Icons.Default.CheckCircle
                                isVerificationPending -> Icons.Default.HourglassBottom
                                else                  -> Icons.Default.Upload
                            },
                            contentDescription = null,
                            modifier           = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text  = when {
                                isCompleted           -> "Uploaded"
                                isVerificationPending -> "Proof Sent"
                                else                  -> "Upload Proof"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

// ── Status badge pill ─────────────────────────────────────────────────────────
@Composable
private fun StatusBadge(
    status: String?,
    color: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White
) {
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(20.dp),
        color    = color.copy(alpha = if (textColor == Color.White) 1f else 0.12f)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            if (status != null) {
                Text(
                    text       = status,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (textColor == Color.White) Color.White else color
                )
            }
        }
    }
}