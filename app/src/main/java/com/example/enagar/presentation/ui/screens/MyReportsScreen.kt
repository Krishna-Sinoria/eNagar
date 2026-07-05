package com.example.enagar.presentation.ui.screens

import android.R.attr.priority
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.enagar.components.BottomNavBar
import com.example.enagar.domain.models.ReportResponse
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.viewModel.CitizenViewModel

import androidx.compose.animation.core.tween

// ── Status helpers ────────────────────────────────────────────────────────────
private fun statusColor(status: String?): Color = when (status?.lowercase()) {
    "pending"              -> Color(0xFFE65100)
    "assigned"             -> Color(0xFF6A1B9A)
    "in progress"          -> Color(0xFF1565C0)
    "verification pending" -> Color(0xFFAD6800)
    "completed", "resolved"-> Color(0xFF2E7D32)
    "rejected"             -> Color(0xFFB71C1C)
    else                   -> Color(0xFF607D8B)
}

private fun statusIcon(status: String?): ImageVector = when (status?.lowercase()) {
    "pending"              -> Icons.Default.HourglassBottom
    "assigned"             -> Icons.Default.AssignmentInd
    "in progress"          -> Icons.Default.BuildCircle
    "verification pending" -> Icons.Default.FactCheck
    "completed", "resolved"-> Icons.Default.CheckCircle
    "rejected"             -> Icons.Default.Cancel
    else                   -> Icons.Default.Info
}

private fun priorityColor(priority: String?): Color = when (priority?.lowercase()) {
    "high"   -> Color(0xFFB71C1C)
    "medium" -> Color(0xFFE65100)
    "low"    -> Color(0xFF1565C0)
    else     -> Color(0xFF607D8B)
}

// ── Safe field accessors (handles ReportResponse vs Report field differences) ─
// ReportResponse._id may be named differently — use the one that exists in your model
private val ReportResponse.safeId: String
    get() = _id  // change to your actual field if different

private val ReportResponse.safeStatus: String?
    get() = status

private val ReportResponse.safePriority: String?
    get() = try { priority } catch (e: Exception) { null } as String?

// submission_date may not exist in ReportResponse — use createdAt or any date field
// If ReportResponse has no date field at all, this returns ""
private val ReportResponse.safeDate: String
    get() = try {
        // Try common field names — use whichever exists in your ReportResponse model
        // Change "createdAt" to whatever date field your ReportResponse actually has
        ""   // ← replace with: createdAt ?: submission_date ?: ""
    } catch (e: Exception) { "" }

// ── MyReportsScreen ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsScreen(navController: NavController) {

    val context     = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    val viewModel: CitizenViewModel = hiltViewModel()

    val reports   by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error     by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserReports(context)
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Pending", "In Progress", "Completed", "Rejected")

    val filteredReports = remember(selectedTabIndex, reports) {
        when (tabs[selectedTabIndex]) {
            "Pending"     -> reports.filter {
                it.safeStatus?.lowercase() in listOf("pending", "assigned")
            }
            "In Progress" -> reports.filter {
                it.safeStatus?.lowercase() in listOf("in progress", "verification pending")
            }
            "Completed"   -> reports.filter {
                it.safeStatus?.lowercase() in listOf("completed", "resolved")
            }
            "Rejected"    -> reports.filter {
                it.safeStatus?.lowercase() == "rejected"
            }
            else -> reports
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "My Reports",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            color      = colorScheme.onPrimary
                        )
                        if (reports.isNotEmpty()) {
                            Text(
                                "${reports.size} total complaint${if (reports.size != 1) "s" else ""}",
                                fontSize = 11.sp,
                                color    = colorScheme.onPrimary.copy(alpha = 0.75f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchUserReports(context) }) {
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
        bottomBar      = { BottomNavBar(navController as NavHostController) },
        containerColor = colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── Summary chips ─────────────────────────────────────────────────
            if (reports.isNotEmpty()) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val pending  = reports.count { it.safeStatus?.lowercase() in listOf("pending", "assigned") }
                    val active   = reports.count { it.safeStatus?.lowercase() in listOf("in progress", "verification pending") }
                    val resolved = reports.count { it.safeStatus?.lowercase() in listOf("completed", "resolved") }
                    val rejected = reports.count { it.safeStatus?.lowercase() == "rejected" }

                    if (pending  > 0) SummaryChip("$pending Pending",   Color(0xFFE65100))
                    if (active   > 0) SummaryChip("$active Active",     Color(0xFF1565C0))
                    if (resolved > 0) SummaryChip("$resolved Resolved", Color(0xFF2E7D32))
                    if (rejected > 0) SummaryChip("$rejected Rejected", Color(0xFFB71C1C))
                }
            }

            // ── Tabs ──────────────────────────────────────────────────────────
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor   = colorScheme.surface,
                edgePadding      = 16.dp,
                divider          = {
                    HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.3f))
                },
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color    = colorScheme.primary,
                        height   = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick  = { selectedTabIndex = index },
                        text = {
                            Text(
                                text       = label,
                                fontSize   = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color      = if (isSelected) colorScheme.primary
                                else colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // ── Content ───────────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {
                when {

                    isLoading -> {
                        Column(
                            modifier            = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Loading your reports…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    error != null && reports.isEmpty() -> {
                        Column(
                            modifier            = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
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
                                    null,
                                    tint     = colorScheme.error,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "Couldn't load reports",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color      = colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                error ?: "Unknown error",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { viewModel.fetchUserReports(context) },
                                shape   = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Try Again")
                            }
                        }
                    }

                    filteredReports.isEmpty() -> {
                        Column(
                            modifier            = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier         = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Inbox,
                                    null,
                                    tint     = colorScheme.primary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "No ${tabs[selectedTabIndex].lowercase()} reports",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color      = colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                if (selectedTabIndex == 0)
                                    "You haven't submitted any reports yet"
                                else
                                    "No reports in this category",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                            if (selectedTabIndex == 0) {
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { navController.navigate(Screen.ReportIssue.route) },
                                    shape   = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Report an Issue")
                                }
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier            = Modifier.fillMaxSize(),
                            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = filteredReports,
                                // ✅ FIX 3: use safeId instead of _id directly
                                key   = { it.safeId }
                            ) { report ->
                                ReportListCard(
                                    report = report,
                                    onClick = {
                                        navController.navigate("report_detail/${report.safeId}")
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                        }
                    }
                }
            }
        }
    }
}

// ── Report list card ──────────────────────────────────────────────────────────
@Composable
private fun ReportListCard(
    report:  ReportResponse,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val sColor      = statusColor(report.safeStatus)

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {

            // Left status bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(sColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Status icon
            Box(
                modifier         = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(sColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = statusIcon(report.safeStatus),
                    contentDescription = null,
                    tint               = sColor,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                // Problem type
                Text(
                    text       = report.problem_type.ifBlank { "Unspecified Issue" },
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = colorScheme.onSurface,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description
                if (!report.description.isNullOrBlank()) {
                    Text(
                        text     = report.description,
                        fontSize = 12.sp,
                        color    = colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ID row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Tag,
                        null,
                        tint     = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        // Show last 8 chars of ID safely
                        text      = report.safeId.takeLast(8),
                        fontSize  = 11.sp,
                        color     = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status + Priority badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {

                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = sColor.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(sColor)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                report.safeStatus ?: "Unknown",
                                fontSize   = 11.sp,
                                color      = sColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // ✅ FIX 1: Priority badge — safe null check with ?.let
                    report.safePriority?.takeIf { it.isNotBlank() }?.let { priority ->
                        val pColor = priorityColor(priority)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = pColor.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier          = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Flag,
                                    null,
                                    tint     = pColor,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    // ✅ FIX 1: safe replaceFirstChar on non-null String
                                    priority.replaceFirstChar { it.uppercase() },
                                    fontSize   = 11.sp,
                                    color      = pColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                null,
                tint     = colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

// ── Summary chip ──────────────────────────────────────────────────────────────
@Composable
private fun SummaryChip(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                label,
                fontSize   = 11.sp,
                color      = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}