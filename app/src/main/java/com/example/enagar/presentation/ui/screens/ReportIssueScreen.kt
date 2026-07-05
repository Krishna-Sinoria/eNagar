package com.example.enagar.presentation.ui.screens

import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.enagar.domain.models.IssueType
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.ui.components.ReportIssueDescription
import com.example.enagar.presentation.ui.components.ReportIssueTopBar
import com.example.enagar.presentation.ui.utility_functions.createImageUri
import com.example.enagar.presentation.ui.utility_functions.fetchLocation
import com.example.enagar.presentation.viewModel.CitizenViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssuesScreen(navController: NavController) {

    // ── ViewModel — UNTOUCHED ─────────────────────────────────────────────────
    val vm: CitizenViewModel = hiltViewModel()
    val report               = vm.issueReport.value

    val context           = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope    = rememberCoroutineScope()
    val colorScheme       = MaterialTheme.colorScheme

    // ── All original state — UNTOUCHED ────────────────────────────────────────
    var selectedImageUri          by remember { mutableStateOf<Uri?>(null) }
    var tempUri                   by remember { mutableStateOf<Uri?>(null) }
    var location                  by remember { mutableStateOf("") }
    var isLocationAvailable       by remember { mutableStateOf(false) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var cameraPermissionGranted   by remember { mutableStateOf(false) }
    var selectedIssueType         by remember { mutableStateOf("") }
    var otherProblemType          by remember { mutableStateOf("") }
    var isSubmitting              by remember { mutableStateOf(false) }

    // ── UI-only state ─────────────────────────────────────────────────────────
    var locationLoading by remember { mutableStateOf(false) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // ── Success handler — UNTOUCHED ───────────────────────────────────────────
    val success = vm.isSuccess.value
    LaunchedEffect(success) {
        if (success) {
            isSubmitting = false
            snackbarHostState.showSnackbar("Issue Reported Successfully ✓")
            navController.navigate(Screen.ReportSubmitted.createRoute(vm.reportId.value))
            vm.isSuccess.value = false
        }
    }

    // ── Location settings launcher — UNTOUCHED ────────────────────────────────
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            locationLoading = true
            fetchLocation(context, fusedLocationClient) { loc ->
                location            = loc.trim()
                isLocationAvailable = location.contains(",") && location.split(",").size == 2
                locationLoading     = false
            }
        } else {
            Toast.makeText(context, "Location access is required to report an issue", Toast.LENGTH_LONG).show()
        }
    }

    // ── Camera launcher — UNTOUCHED ───────────────────────────────────────────
    // ⚠️ Gallery is intentionally REMOVED — only live camera capture is allowed
    // to prevent users from submitting old or fake photos as evidence.
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { captureSuccess ->
        if (captureSuccess) {
            selectedImageUri = tempUri
            tempUri?.let { vm.setImageUri(it) }
            Toast.makeText(context, "Photo captured — location embedded ✓", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Photo capture cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Location permission launcher — UNTOUCHED ──────────────────────────────
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted =
            permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (locationPermissionGranted) {
            locationLoading = true
            checkAndRequestLocationSettings(context, fusedLocationClient) { loc ->
                location            = loc
                isLocationAvailable = true
                locationLoading     = false
            }
        } else {
            Toast.makeText(context, "Location permission is required to submit a report", Toast.LENGTH_LONG).show()
        }
    }

    // ── Camera permission launcher — UNTOUCHED ────────────────────────────────
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraPermissionGranted = granted
        if (granted) {
            val uri = createImageUri(context)
            uri?.let { tempUri = it; cameraLauncher.launch(it) }
        } else {
            Toast.makeText(context, "Camera permission is required to capture photos", Toast.LENGTH_LONG).show()
        }
    }

    // ── Helper: open camera (used in multiple places) ─────────────────────────
    fun openCamera() {
        if (!isLocationAvailable) {
            Toast.makeText(
                context,
                "📍 GPS location must be ready before capturing — please wait",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (cameraPermissionGranted) {
            val uri = createImageUri(context)
            uri?.let { tempUri = it; cameraLauncher.launch(it) }
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // ── Initial permission check — UNTOUCHED ──────────────────────────────────
    LaunchedEffect(Unit) {
        cameraPermissionGranted =
            ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED

        val fineGranted =
            ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        val coarseGranted =
            ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            locationPermissionGranted = true
            locationLoading           = true
            checkAndRequestLocationSettings(context, fusedLocationClient) { loc ->
                location            = loc
                isLocationAvailable = true
                locationLoading     = false
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // ── Issue types — UNTOUCHED ───────────────────────────────────────────────
    val issueTypes = listOf(
        IssueType("Pothole",     Icons.Default.Warning),
        IssueType("Drainage",    Icons.Default.Water),
        IssueType("Streetlight", Icons.Default.Lightbulb),
        IssueType("Traffic",     Icons.Default.Traffic),
        IssueType("Garbage",     Icons.Default.Delete),
        IssueType("Other",       Icons.Default.MoreHoriz)
    )

    // ── Submitting dialog ─────────────────────────────────────────────────────
    if (isSubmitting) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
            ) {
                Column(
                    modifier            = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color       = colorScheme.primary,
                        modifier    = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Submitting Report",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Uploading live photo and GPS location…",
                        style     = MaterialTheme.typography.bodySmall,
                        color     = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // ── Scaffold ──────────────────────────────────────────────────────────────
    Scaffold(
        snackbarHost   = { SnackbarHost(hostState = snackbarHostState) },
        topBar         = { ReportIssueTopBar(navController) },
        containerColor = colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Gradient header with step progress ────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(colorScheme.primary, colorScheme.background),
                            startY = 0f, endY = 280f
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column {
                    Text(
                        "New Report",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        "Capture a live photo with GPS, pick the issue type and submit.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StepProgressRow(
                        photoReady    = selectedImageUri != null,
                        locationReady = isLocationAvailable,
                        typeReady     = selectedIssueType.isNotBlank()
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                // ── SECTION 1: Live Camera Photo ──────────────────────────────
                Spacer(modifier = Modifier.height(4.dp))
                SectionLabel(
                    icon  = Icons.Default.CameraAlt,
                    title = "Live Camera Photo",
                    badge = when {
                        selectedImageUri != null -> "Captured ✓"
                        !isLocationAvailable     -> "Waiting for GPS…"
                        else                     -> "Required"
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Live-only notice banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    color    = colorScheme.primaryContainer
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.GppGood,
                            contentDescription = null,
                            tint               = colorScheme.primary,
                            modifier           = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Live capture only",
                                style      = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color      = colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Gallery uploads are disabled to ensure authentic evidence with GPS.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Photo card — tap to open camera
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clickable { openCamera() },
                    shape     = RoundedCornerShape(18.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = if (selectedImageUri != null)
                            colorScheme.surface
                        else
                            colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border    = if (selectedImageUri == null)
                        androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            colorScheme.outline.copy(alpha = 0.4f)
                        )
                    else null
                ) {
                    if (selectedImageUri != null) {
                        // ── Captured photo preview ────────────────────────────
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model              = selectedImageUri,
                                contentDescription = "Captured issue photo",
                                modifier           = Modifier.fillMaxSize(),
                                contentScale       = ContentScale.Crop
                            )
                            // Dark scrim at bottom
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                        )
                                    )
                            )
                            // GPS stamp bottom-left
                            Row(
                                modifier          = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint               = Color(0xFF69F0AE),
                                    modifier           = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (location.isNotBlank()) "GPS: $location" else "GPS embedded",
                                    fontSize = 9.sp,
                                    color    = Color.White
                                )
                            }
                            // Retake button bottom-right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Cameraswitch,
                                    contentDescription = "Retake photo",
                                    tint               = Color.White,
                                    modifier           = Modifier.size(18.dp)
                                )
                            }
                        }
                    } else {
                        // ── Empty state — prompt to capture ───────────────────
                        Column(
                            modifier            = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier         = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isLocationAvailable)
                                            colorScheme.primaryContainer
                                        else
                                            colorScheme.outline.copy(alpha = 0.12f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (locationLoading) {
                                    CircularProgressIndicator(
                                        modifier    = Modifier.size(28.dp),
                                        strokeWidth = 2.5.dp,
                                        color       = colorScheme.primary
                                    )
                                } else {
                                    Icon(
                                        imageVector        = if (isLocationAvailable)
                                            Icons.Default.CameraAlt
                                        else
                                            Icons.Default.LocationSearching,
                                        contentDescription = null,
                                        tint               = if (isLocationAvailable)
                                            colorScheme.primary
                                        else
                                            colorScheme.onSurfaceVariant,
                                        modifier           = Modifier.size(30.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text       = when {
                                    locationLoading      -> "Acquiring GPS…"
                                    isLocationAvailable  -> "Tap to capture live photo"
                                    else                 -> "Waiting for GPS signal"
                                },
                                style      = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color      = if (isLocationAvailable)
                                    colorScheme.onSurface
                                else
                                    colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text  = when {
                                    locationLoading     -> "Photo will unlock once GPS is ready"
                                    isLocationAvailable -> "Camera opens with GPS embedded"
                                    else                -> "Enable location to proceed"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                                textAlign = TextAlign.Center,
                                modifier  = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }

                // Capture button (always visible below card)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick   = { openCamera() },
                    enabled   = isLocationAvailable,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = ButtonDefaults.buttonColors(
                        containerColor         = colorScheme.primary,
                        contentColor           = colorScheme.onPrimary,
                        disabledContainerColor = colorScheme.surfaceVariant,
                        disabledContentColor   = colorScheme.onSurfaceVariant
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    if (locationLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color       = colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Waiting for GPS…", style = MaterialTheme.typography.labelMedium)
                    } else {
                        Icon(
                            imageVector        = if (selectedImageUri != null)
                                Icons.Default.Cameraswitch
                            else
                                Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier           = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text  = if (selectedImageUri != null) "Retake Photo" else "Open Camera",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── SECTION 2: GPS Location ───────────────────────────────────
                SectionLabel(
                    icon  = Icons.Default.LocationOn,
                    title = "GPS Location",
                    badge = if (isLocationAvailable) "Acquired ✓" else "Detecting…"
                )
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = if (isLocationAvailable)
                            Color(0xFF2E7D32).copy(alpha = 0.07f)
                        else
                            colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isLocationAvailable) Color(0xFF2E7D32).copy(alpha = 0.15f)
                                    else colorScheme.outline.copy(alpha = 0.12f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (locationLoading) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color       = colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = if (isLocationAvailable)
                                        Icons.Default.LocationOn
                                    else
                                        Icons.Default.LocationOff,
                                    contentDescription = null,
                                    tint        = if (isLocationAvailable) Color(0xFF2E7D32)
                                    else colorScheme.onSurfaceVariant,
                                    modifier    = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text       = if (isLocationAvailable) "Location Acquired" else "Fetching GPS…",
                                style      = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color      = if (isLocationAvailable) Color(0xFF2E7D32)
                                else colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text  = if (location.isNotBlank()) location
                                else "Coordinates will appear here",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }

                        if (!isLocationAvailable && !locationLoading) {
                            IconButton(
                                onClick = {
                                    locationLoading = true
                                    checkAndRequestLocationSettings(context, fusedLocationClient) { loc ->
                                        location            = loc
                                        isLocationAvailable = true
                                        locationLoading     = false
                                        Toast.makeText(context, "📍 Location updated", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Refresh, null, tint = colorScheme.primary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── SECTION 3: Issue Type ─────────────────────────────────────
                SectionLabel(
                    icon  = Icons.Default.Category,
                    title = "Issue Type",
                    badge = if (selectedIssueType.isNotBlank()) "$selectedIssueType ✓" else "Required"
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    issueTypes.chunked(3).forEach { rowItems ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { issue ->
                                IssueTypeCard(
                                    issue      = issue,
                                    isSelected = selectedIssueType == issue.name,
                                    modifier   = Modifier.weight(1f),
                                    onClick    = {
                                        selectedIssueType = issue.name
                                        vm.setIssueType(issue.name)
                                    }
                                )
                            }
                            repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = selectedIssueType == "Other",
                    enter   = fadeIn() + slideInVertically { -10 },
                    exit    = fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value         = otherProblemType,
                            onValueChange = { otherProblemType = it },
                            label         = { Text("Describe the issue type") },
                            leadingIcon   = {
                                Icon(Icons.Default.Edit, null, tint = colorScheme.primary)
                            },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(14.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.outline,
                                focusedLabelColor    = colorScheme.primary,
                                cursorColor          = colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── SECTION 4: Description — ORIGINAL COMPONENT UNTOUCHED ─────
                SectionLabel(
                    icon  = Icons.Default.Description,
                    title = "Description",
                    badge = "Optional"
                )
                Spacer(modifier = Modifier.height(10.dp))
                ReportIssueDescription(vm)

                Spacer(modifier = Modifier.height(28.dp))

                // ── Pre-submit validation checklist ───────────────────────────
                val allReady = selectedImageUri != null &&
                        isLocationAvailable &&
                        selectedIssueType.isNotBlank()

                AnimatedVisibility(visible = !allReady) {
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = CardDefaults.cardColors(
                            containerColor = colorScheme.secondaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "Complete before submitting:",
                                style      = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color      = colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (selectedImageUri == null)        ValidationRow("Capture a live photo")
                            if (!isLocationAvailable)            ValidationRow("GPS location must be acquired")
                            if (selectedIssueType.isBlank())     ValidationRow("Select an issue type")
                        }
                    }
                }

                // ── Submit button — ORIGINAL LOGIC UNTOUCHED ─────────────────
                Button(
                    onClick = {
                        if (isSubmitting) return@Button

                        val finalProblemType = if (selectedIssueType == "Other")
                            otherProblemType.trim() else selectedIssueType

                        if (selectedImageUri == null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("📷 Please capture a live photo first")
                            }
                            return@Button
                        }
                        if (finalProblemType.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("🏷 Please select or enter an issue type")
                            }
                            return@Button
                        }
                        if (location.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("📍 GPS location not ready — please wait")
                            }
                            return@Button
                        }
                        val latLng = location.split(",")
                        if (latLng.size != 2) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("⚠️ Invalid GPS format — please retry")
                            }
                            return@Button
                        }

                        isSubmitting = true
                        vm.submitReportToBackend(
                            problemType = finalProblemType,
                            description = report.description ?: "",
                            latitude    = latLng[0].trim(),
                            longitude   = latLng[1].trim(),
                            imageUri    = selectedImageUri!!
                        )
                    },
                    enabled   = !isSubmitting,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = ButtonDefaults.buttonColors(
                        containerColor         = colorScheme.primary,
                        contentColor           = colorScheme.onPrimary,
                        disabledContainerColor = colorScheme.primary.copy(alpha = 0.5f),
                        disabledContentColor   = colorScheme.onPrimary.copy(alpha = 0.7f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Submitting…", style = MaterialTheme.typography.labelLarge)
                    } else {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Submit Report", style = MaterialTheme.typography.labelLarge, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

// ── Step progress row ─────────────────────────────────────────────────────────
@Composable
private fun StepProgressRow(photoReady: Boolean, locationReady: Boolean, typeReady: Boolean) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        StepDot("Photo",    photoReady,    modifier = Modifier.weight(1f))
        StepLine(photoReady && locationReady)
        StepDot("Location", locationReady, modifier = Modifier.weight(1f))
        StepLine(locationReady && typeReady)
        StepDot("Type",     typeReady,     modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StepDot(label: String, done: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier         = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (done) Color.White else Color.White.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            if (done) Icon(Icons.Default.Check, null, tint = Color(0xFF1A3C6E), modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = if (done) 1f else 0.6f))
    }
}

@Composable
private fun StepLine(active: Boolean) {
    Box(
        modifier = Modifier
            .width(28.dp)
            .height(2.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(if (active) Color.White else Color.White.copy(alpha = 0.22f))
    )
}

// ── Section label ─────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(icon: ImageVector, title: String, badge: String) {
    val colorScheme = MaterialTheme.colorScheme
    val isDone      = badge.contains("✓")
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            title,
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color      = colorScheme.onBackground,
            modifier   = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isDone) Color(0xFF2E7D32).copy(alpha = 0.1f) else colorScheme.surfaceVariant
        ) {
            Text(
                badge,
                modifier   = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                style      = MaterialTheme.typography.labelSmall,
                color      = if (isDone) Color(0xFF2E7D32) else colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Issue type card ───────────────────────────────────────────────────────────
@Composable
private fun IssueTypeCard(issue: IssueType, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier  = modifier
            .aspectRatio(1f)
            .clickable { onClick() }
            .then(if (isSelected) Modifier.border(2.dp, colorScheme.primary, RoundedCornerShape(14.dp)) else Modifier),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (isSelected) colorScheme.primaryContainer else colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                issue.icon, null,
                tint     = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                issue.name,
                style      = MaterialTheme.typography.labelSmall,
                color      = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign  = TextAlign.Center
            )
        }
    }
}

// ── Validation checklist row ──────────────────────────────────────────────────
@Composable
private fun ValidationRow(text: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.RadioButtonUnchecked, null, tint = colorScheme.onSecondaryContainer, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSecondaryContainer)
    }
}

// ── checkAndRequestLocationSettings — ORIGINAL UNTOUCHED ─────────────────────
@RequiresApi(Build.VERSION_CODES.S)
fun checkAndRequestLocationSettings(
    context:            android.content.Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (String) -> Unit
) {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).build()
    val builder         = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val client          = LocationServices.getSettingsClient(context)

    client.checkLocationSettings(builder.build())
        .addOnSuccessListener {
            fetchLocation(context, fusedLocationClient) { location ->
                onLocationReceived(location)
            }
        }
        .addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(context as android.app.Activity, 1001)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
}