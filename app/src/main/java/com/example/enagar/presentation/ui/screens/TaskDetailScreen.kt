package com.example.enagar.presentation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.enagar.presentation.viewModel.UploadViewModel
import com.google.android.gms.location.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun TaskDetailScreen(
    navController: NavHostController,
    reportId:      String
) {
    val context     = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // ── All original state — UNTOUCHED ────────────────────────────────────────
    val uploadViewModel: UploadViewModel = hiltViewModel()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraImageUri   by remember { mutableStateOf<Uri?>(null) }
    var latitude         by remember { mutableStateOf("Fetching...") }
    var longitude        by remember { mutableStateOf("Fetching...") }
    var isUploading      by remember { mutableStateOf(false) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // ── Location permission — UNTOUCHED ───────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getCurrentLocation(fusedLocationClient) { lat, lng ->
                latitude  = lat
                longitude = lng
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Camera launcher — UNTOUCHED ───────────────────────────────────────────
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            selectedImageUri = cameraImageUri
            Toast.makeText(context, "Photo captured successfully ✓", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Camera permission — UNTOUCHED ─────────────────────────────────────────
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Auto fetch location — UNTOUCHED ───────────────────────────────────────
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation(fusedLocationClient) { lat, lng ->
                latitude  = lat
                longitude = lng
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val locationReady = latitude != "Fetching..." && longitude != "Fetching..."
    val allReady      = selectedImageUri != null && locationReady

    // ── Upload dialog ─────────────────────────────────────────────────────────
    if (isUploading) {
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
                        "Uploading Proof",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Sending photo and GPS location to server…",
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
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Upload Proof",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            color      = colorScheme.onPrimary
                        )
                        Text(
                            "Complete your assigned task",
                            fontSize = 11.sp,
                            color    = colorScheme.onPrimary.copy(alpha = 0.75f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Gradient header ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(colorScheme.primary, colorScheme.background),
                            startY = 0f, endY = 240f
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Text(
                        "Task Completion Proof",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        "Capture a live photo at the site to mark your task as complete.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Report ID chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Tag,
                                null,
                                tint     = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Report ID: $reportId",
                                fontSize   = 12.sp,
                                color      = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                // ── GPS Location card ─────────────────────────────────────────
                Spacer(modifier = Modifier.height(4.dp))
                ProofSectionLabel(
                    icon  = Icons.Default.LocationOn,
                    title = "GPS Location",
                    badge = if (locationReady) "Acquired ✓" else "Detecting…"
                )
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = if (locationReady)
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
                                    if (locationReady) Color(0xFF2E7D32).copy(alpha = 0.15f)
                                    else colorScheme.outline.copy(alpha = 0.12f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!locationReady) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color       = colorScheme.primary
                                )
                            } else {
                                Icon(
                                    Icons.Default.LocationOn,
                                    null,
                                    tint     = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text       = if (locationReady) "Location Acquired" else "Fetching GPS…",
                                style      = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color      = if (locationReady) Color(0xFF2E7D32)
                                else colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (locationReady) {
                                Text(
                                    "Lat: $latitude",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Lng: $longitude",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    "GPS coordinates will appear here",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }

                        if (!locationReady) {
                            IconButton(
                                onClick = {
                                    getCurrentLocation(fusedLocationClient) { lat, lng ->
                                        latitude  = lat
                                        longitude = lng
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

                // ── Live Camera Proof ─────────────────────────────────────────
                ProofSectionLabel(
                    icon  = Icons.Default.CameraAlt,
                    title = "Completion Photo",
                    badge = if (selectedImageUri != null) "Captured ✓" else "Required"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Live-only notice
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
                            null,
                            tint     = colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Live camera capture only — gallery uploads not allowed for task proof.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Photo card
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape     = RoundedCornerShape(18.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = if (selectedImageUri != null)
                            colorScheme.surface else colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border    = if (selectedImageUri == null)
                        androidx.compose.foundation.BorderStroke(
                            1.5.dp, colorScheme.outline.copy(alpha = 0.4f)
                        ) else null
                ) {
                    if (selectedImageUri != null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model              = selectedImageUri,
                                contentDescription = "Proof photo",
                                modifier           = Modifier.fillMaxSize(),
                                contentScale       = ContentScale.Crop
                            )
                            // Dark gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(0.55f))
                                        )
                                    )
                            )
                            // GPS stamp
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    null,
                                    tint     = Color(0xFF69F0AE),
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (locationReady) "$latitude, $longitude" else "GPS embedded",
                                    fontSize = 9.sp,
                                    color    = Color.White
                                )
                            }
                            // Retake button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Cameraswitch,
                                    "Retake",
                                    tint     = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier            = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier         = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (locationReady) colorScheme.primaryContainer
                                        else colorScheme.outline.copy(alpha = 0.12f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!locationReady) {
                                    CircularProgressIndicator(
                                        modifier    = Modifier.size(26.dp),
                                        strokeWidth = 2.5.dp,
                                        color       = colorScheme.primary
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        null,
                                        tint     = colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text       = if (locationReady) "Tap button below to capture" else "Waiting for GPS…",
                                style      = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color      = if (locationReady) colorScheme.onSurface
                                else colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text  = if (locationReady) "Live photo with GPS will be captured"
                                else "Camera unlocks once GPS is ready",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier  = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }

                // Camera button
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (!locationReady) {
                            Toast.makeText(context, "📍 Please wait — GPS not ready yet", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val uri = createImageUri(context)
                        cameraImageUri = uri
                        if (ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    enabled   = locationReady,
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
                    Icon(
                        imageVector = if (selectedImageUri != null) Icons.Default.Cameraswitch
                        else Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (selectedImageUri != null) "Retake Photo" else "Open Camera",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── Pre-submit checklist ──────────────────────────────────────
                if (!allReady) {
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
                                "Complete before uploading:",
                                style      = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color      = colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (!locationReady)          ProofCheckRow("GPS location must be acquired")
                            if (selectedImageUri == null) ProofCheckRow("Capture a live proof photo")
                        }
                    }
                }

                // ── Upload button — ORIGINAL LOGIC UNTOUCHED ─────────────────
                Button(
                    onClick = {
                        if (selectedImageUri == null) {
                            Toast.makeText(context, "📷 Please capture a photo first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!locationReady) {
                            Toast.makeText(context, "📍 Location not available yet", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isUploading = true

                        val compressedFile = compressImage(context, selectedImageUri!!)

                        uploadViewModel.uploadProof(
                            context  = context,
                            reportId = reportId,
                            file     = compressedFile,
                            lat      = latitude,
                            lng      = longitude,
                            onSuccess = {
                                isUploading = false
                                Toast.makeText(context, "✅ Completion proof uploaded successfully", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { message ->
                                isUploading = false
                                Toast.makeText(context, "❌ $message", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    enabled   = !isUploading && allReady,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = ButtonDefaults.buttonColors(
                        containerColor         = colorScheme.primary,
                        contentColor           = colorScheme.onPrimary,
                        disabledContainerColor = colorScheme.surfaceVariant,
                        disabledContentColor   = colorScheme.onSurfaceVariant
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Upload Completion Proof",
                        style    = MaterialTheme.typography.labelLarge,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────
@Composable
private fun ProofSectionLabel(
    icon:  androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    badge: String
) {
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

// ── Checklist row ─────────────────────────────────────────────────────────────
@Composable
private fun ProofCheckRow(text: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.RadioButtonUnchecked, null, tint = colorScheme.onSecondaryContainer, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSecondaryContainer)
    }
}

// ── Get current location — ORIGINAL UNTOUCHED ─────────────────────────────────
@SuppressLint("MissingPermission")
fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived:  (String, String) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            onLocationReceived(location.latitude.toString(), location.longitude.toString())
        } else {
            val locationRequest = LocationRequest.create().apply {
                priority        = Priority.PRIORITY_HIGH_ACCURACY
                interval        = 1000
                fastestInterval = 500
                numUpdates      = 1
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val freshLocation = result.lastLocation
                        if (freshLocation != null) {
                            onLocationReceived(
                                freshLocation.latitude.toString(),
                                freshLocation.longitude.toString()
                            )
                        }
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                },
                android.os.Looper.getMainLooper()
            )
        }
    }
}

// ── Compress image — ORIGINAL UNTOUCHED ───────────────────────────────────────
fun compressImage(context: Context, uri: Uri): File {
    val inputStream    = context.contentResolver.openInputStream(uri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    val maxWidth = 1080; val maxHeight = 1080
    var width    = originalBitmap.width
    var height   = originalBitmap.height
    val ratioBitmap = width.toFloat() / height.toFloat()
    val ratioMax    = maxWidth.toFloat() / maxHeight.toFloat()
    if (height > maxHeight || width > maxWidth) {
        if (ratioBitmap < ratioMax) {
            width = (maxHeight * ratioBitmap).toInt(); height = maxHeight
        } else if (ratioBitmap > ratioMax) {
            height = (maxWidth / ratioBitmap).toInt(); width = maxWidth
        } else { width = maxWidth; height = maxHeight }
    }
    val resizedBitmap = originalBitmap.scale(width, height)
    val file          = File.createTempFile("compressed_", ".jpg", context.cacheDir)
    val outputStream  = FileOutputStream(file)
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)
    outputStream.flush(); outputStream.close()
    originalBitmap.recycle(); resizedBitmap.recycle()
    return file
}

// ── Create camera URI — ORIGINAL UNTOUCHED ────────────────────────────────────
fun createImageUri(context: Context): Uri {
    val timeStamp  = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile  = File(context.cacheDir, "camera_$timeStamp.jpg")
    return FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
}