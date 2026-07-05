package com.example.enagar.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.enagar.components.BottomNavBar
import com.example.enagar.presentation.navigation.Screen
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.util.*

// ── Data Models — UNTOUCHED ───────────────────────────────────────────────────
data class HomeReport(
    val id:          Int,
    val title:       String,
    val status:      String,
    val icon:        ImageVector,
    val statusColor: Color
)

data class QuickAction(
    val label: String,
    val icon:  ImageVector,
    val color: Color,
    val route: String
)

// ── HomeScreen ────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val colorScheme = MaterialTheme.colorScheme
    val context     = LocalContext.current

    // Greeting based on time of day
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else      -> "Good Evening"
        }
    }

    // ── Dummy data — UNTOUCHED ────────────────────────────────────────────────
    val recentReports = listOf(
        HomeReport(1, "Pothole on Main Street",  "Pending",     Icons.Default.ReportProblem, Color(0xFFE65100)),
        HomeReport(2, "Streetlight Outage",      "In Progress", Icons.Default.Lightbulb,    Color(0xFF1565C0)),
        HomeReport(3, "Broken Park Bench",       "Resolved",    Icons.Default.Done,          Color(0xFF2E7D32))
    )

    val quickActions = listOf(
        QuickAction("Report\nIssue",  Icons.Default.AddCircle,    colorScheme.primary,  Screen.ReportIssue.route),
        QuickAction("My\nReports",    Icons.Default.Assignment,   Color(0xFF1565C0),    Screen.MyReports.route),
        QuickAction("Alerts",         Icons.Default.Notifications, Color(0xFF6A1B9A),   Screen.Notifications.route),
        QuickAction("Profile",        Icons.Default.Person,        Color(0xFFE8A020),   Screen.Profile.route)
    )

    // ── Location permission — UNTOUCHED ───────────────────────────────────────
    var locationGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        locationGranted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!locationGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // ── Scaffold ──────────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.LocationCity,
                                contentDescription = null,
                                tint     = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "eNagar",
                                fontWeight    = FontWeight.Bold,
                                fontSize      = 17.sp,
                                color         = colorScheme.onPrimary,
                                letterSpacing = 0.3.sp
                            )
                            Text(
                                "Civic Issue Resolution",
                                fontSize = 10.sp,
                                color    = colorScheme.onPrimary.copy(alpha = 0.72f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        BadgedBox(badge = {
                            Badge(containerColor = colorScheme.secondary) {
                                Text("3", fontSize = 8.sp)
                            }
                        }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint               = colorScheme.onPrimary
                            )
                        }
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint               = colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary)
            )
        },
        bottomBar    = { BottomNavBar(navController as NavHostController) },
        containerColor = colorScheme.background
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // ── Hero banner ───────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(colorScheme.primary, colorScheme.background),
                                startY = 0f, endY = 380f
                            )
                        )
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp)
                ) {
                    Column {
                        // Greeting row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "$greeting! 👋",
                                fontSize = 14.sp,
                                color    = colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            // Live indicator pill
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.18f)
                            ) {
                                Row(
                                    modifier          = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF69F0AE))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        "Live",
                                        fontSize   = 11.sp,
                                        color      = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            "Report Civic Issues,\nBuild Better Cities.",
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color      = colorScheme.onPrimary,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // CTA button
                        Button(
                            onClick   = { navController.navigate(Screen.ReportIssue.route) },
                            modifier  = Modifier.fillMaxWidth().height(50.dp),
                            shape     = RoundedCornerShape(14.dp),
                            colors    = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.secondary,
                                contentColor   = colorScheme.onSecondary
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Report an Issue",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // ── Stats row ─────────────────────────────────────────────────────
            item {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(Modifier.weight(1f), "24", "Total",    colorScheme.primary)
                    StatCard(Modifier.weight(1f), "6",  "Active",   Color(0xFF1565C0))
                    StatCard(Modifier.weight(1f), "18", "Resolved", Color(0xFF2E7D32))
                }
            }

            // ── Nearby Issues + Map ───────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Section header
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Nearby Issues",
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color      = colorScheme.onBackground
                            )
                            Text(
                                "Issues reported around you",
                                fontSize = 12.sp,
                                color    = colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier          = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    "GPS Live",
                                    fontSize   = 11.sp,
                                    color      = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Map card
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .shadow(6.dp, RoundedCornerShape(20.dp), clip = false),
                        shape     = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                        colors    = CardDefaults.cardColors(containerColor = colorScheme.surface)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {

                            // OSM Map — UNTOUCHED logic
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory  = { ctx ->
                                    Configuration.getInstance().apply {
                                        userAgentValue = ctx.packageName
                                        load(ctx, ctx.getSharedPreferences("osmdroid", 0))
                                    }
                                    MapView(ctx).apply {
                                        setTileSource(TileSourceFactory.MAPNIK)
                                        setMultiTouchControls(true)
                                        isTilesScaledToDpi = true
                                        controller.setZoom(15.0)
                                        controller.setCenter(GeoPoint(26.8467, 80.9462))

                                        if (locationGranted) {
                                            val locationOverlay = MyLocationNewOverlay(
                                                GpsMyLocationProvider(ctx), this
                                            )
                                            locationOverlay.enableMyLocation()
                                            locationOverlay.enableFollowLocation()
                                            overlays.add(locationOverlay)
                                        }

                                        listOf(
                                            Triple(26.8480, 80.9475, "Pothole"),
                                            Triple(26.8455, 80.9445, "Broken Light"),
                                            Triple(26.8470, 80.9490, "Drainage Block")
                                        ).forEach { (lat, lng, title) ->
                                            overlays.add(Marker(this).apply {
                                                position = GeoPoint(lat, lng)
                                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                this.title = title
                                            })
                                        }
                                    }
                                },
                                update = { mapView ->
                                    if (locationGranted &&
                                        mapView.overlays.none { it is MyLocationNewOverlay }
                                    ) {
                                        val overlay = MyLocationNewOverlay(
                                            GpsMyLocationProvider(mapView.context), mapView
                                        )
                                        overlay.enableMyLocation()
                                        overlay.enableFollowLocation()
                                        mapView.overlays.add(overlay)
                                        mapView.invalidate()
                                    }
                                }
                            )

                            // Search bar over map
                            var searchQuery by remember { mutableStateOf("") }
                            TextField(
                                value         = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder   = {
                                    Text(
                                        "Search location…",
                                        fontSize = 13.sp,
                                        color    = colorScheme.onSurfaceVariant
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        null,
                                        tint     = colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                modifier   = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .align(Alignment.TopCenter)
                                    .shadow(8.dp, RoundedCornerShape(14.dp)),
                                shape  = RoundedCornerShape(14.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor   = colorScheme.surface,
                                    unfocusedContainerColor = colorScheme.surface,
                                    focusedTextColor        = colorScheme.onSurface,
                                    unfocusedTextColor      = colorScheme.onSurface,
                                    cursorColor             = colorScheme.primary,
                                    focusedIndicatorColor   = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            // No location nudge
                            if (!locationGranted) {
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = colorScheme.errorContainer
                                ) {
                                    Row(
                                        modifier          = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOff,
                                            null,
                                            tint     = colorScheme.onErrorContainer,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Enable location for nearby results",
                                            fontSize = 12.sp,
                                            color    = colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Map legend chips
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf(
                            Triple("Pothole",      Color(0xFFE65100), Icons.Default.Warning),
                            Triple("Broken Light", Color(0xFF1565C0), Icons.Default.Lightbulb),
                            Triple("Drainage",     Color(0xFF6A1B9A), Icons.Default.Water)
                        )) { (label, color, icon) ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = color.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier          = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(icon, null, tint = color, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            // ── Quick Actions ─────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Quick Actions",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        quickActions.forEach { action ->
                            QuickActionCard(
                                modifier = Modifier.weight(1f),
                                action   = action,
                                onClick  = { navController.navigate(action.route) }
                            )
                        }
                    }
                }
            }

            // ── Recent Reports header ─────────────────────────────────────────
            item {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 4.dp, top = 20.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Recent Reports",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = colorScheme.onBackground
                        )
                        Text(
                            "Your latest submissions",
                            fontSize = 12.sp,
                            color    = colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { navController.navigate(Screen.MyReports.route) }) {
                        Text(
                            "See all →",
                            fontSize   = 13.sp,
                            color      = colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Report cards ──────────────────────────────────────────────────
            items(recentReports) { report ->
                AnimatedVisibility(
                    visible = true,
                    enter   = fadeIn(tween(300)) + slideInVertically(tween(300)) { 20 }
                ) {
                    ReportCard(
                        report   = report,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// ── Stat Card ─────────────────────────────────────────────────────────────────
@Composable
fun StatCard(modifier: Modifier = Modifier, value: String, label: String, color: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                label,
                fontSize  = 11.sp,
                color     = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Quick Action Card ─────────────────────────────────────────────────────────
@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    action:   QuickAction,
    onClick:  () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = action.color.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick   = onClick
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                action.color,
                                action.color.copy(alpha = 0.75f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = action.icon,
                    contentDescription = action.label,
                    tint               = Color.White,
                    modifier           = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = action.label,
                fontSize   = 11.sp,
                color      = colorScheme.onBackground,
                textAlign  = TextAlign.Center,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Report Card ───────────────────────────────────────────────────────────────
@Composable
fun ReportCard(report: HomeReport, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left status bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(report.statusColor)
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Icon box
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(report.statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = report.icon,
                    contentDescription = report.title,
                    tint               = report.statusColor,
                    modifier           = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = report.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = colorScheme.onSurface,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = report.statusColor.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(report.statusColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text       = report.status,
                            fontSize   = 11.sp,
                            color      = report.statusColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(18.dp)
            )
        }
    }
}