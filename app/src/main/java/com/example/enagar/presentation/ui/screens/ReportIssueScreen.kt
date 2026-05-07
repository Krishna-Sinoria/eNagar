package com.example.enagar.presentation.ui.screens

import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
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

    val vm: CitizenViewModel = hiltViewModel()
    val report = vm.issueReport.value

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    var location by remember { mutableStateOf("") }
    var isLocationAvailable by remember { mutableStateOf(false) }

    var locationPermissionGranted by remember { mutableStateOf(false) }
    var cameraPermissionGranted by remember { mutableStateOf(false) }

    var selectedIssueType by remember { mutableStateOf("") }
    var otherProblemType by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }

    val fusedLocationClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    val success = vm.isSuccess.value

    LaunchedEffect(success) {

        if (success) {

            isSubmitting = false

            snackbarHostState.showSnackbar(
                "Issue Reported Successfully"
            )

            navController.navigate(
                Screen.ReportSubmitted.createRoute(
                    vm.reportId.value
                )
            )
            vm.isSuccess.value = false
        }
    }

    // ---------------- LOCATION SETTINGS ----------------

    val locationSettingsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->

            if (result.resultCode == android.app.Activity.RESULT_OK) {

                fetchLocation(context, fusedLocationClient) { loc ->

                    location = loc.trim()

                    isLocationAvailable =
                        location.contains(",") && location.split(",").size == 2
                }
            } else {
                Toast.makeText(
                    context,
                    "Location is required",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // ---------------- CAMERA ----------------

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->

            if (success) {
                selectedImageUri = tempUri
                tempUri?.let {
                    vm.setImageUri(it)
                }
            } else {
                Toast.makeText(
                    context,
                    "Image capture failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // ---------------- LOCATION PERMISSION ----------------

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            locationPermissionGranted =
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (locationPermissionGranted) {
                checkAndRequestLocationSettings(
                    context = context,
                    fusedLocationClient = fusedLocationClient
                ) { loc ->
                    location = loc
                    isLocationAvailable = true
                }
            }
        }

    // ---------------- CAMERA PERMISSION ----------------

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            cameraPermissionGranted = granted

            if (!granted) {
                Toast.makeText(
                    context,
                    "Camera permission required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // ---------------- INITIAL CHECK ----------------

    LaunchedEffect(Unit) {

        cameraPermissionGranted =
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        val fineLocationGranted =
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {

            locationPermissionGranted = true

            checkAndRequestLocationSettings(
                context = context,
                fusedLocationClient = fusedLocationClient
            ) { loc ->
                location = loc
                isLocationAvailable = true
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

    // ---------------- ISSUE TYPES ----------------

    val issueTypes = listOf(
        IssueType("Pothole", Icons.Default.Warning),
        IssueType("Drainage", Icons.Default.Water),
        IssueType("Streetlight", Icons.Default.Lightbulb),
        IssueType("Traffic", Icons.Default.Traffic),
        IssueType("Garbage", Icons.Default.Delete),
        IssueType("Other", Icons.Default.MoreHoriz)
    )

    // ---------------- LOADING DIALOG ----------------

    if (isSubmitting) {

        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = {
                Text("Submitting Report")
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    CircularProgressIndicator()

                    Spacer(modifier = Modifier.width(12.dp))

                    Text("Please wait...")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            ReportIssueTopBar(navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "Upload a photo, select type and provide details.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------- IMAGE CARD ----------------

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(12.dp)
            ) {

                if (selectedImageUri != null) {

                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text("No Photo Selected")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---------------- CAMERA BUTTON ----------------

            Button(
                onClick = {

                    if (!isLocationAvailable) {

                        Toast.makeText(
                            context,
                            "Location not available",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    if (cameraPermissionGranted) {

                        val uri = createImageUri(context)

                        uri?.let {
                            tempUri = it
                            cameraLauncher.launch(it)
                        }

                    } else {

                        cameraPermissionLauncher.launch(
                            android.Manifest.permission.CAMERA
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(Icons.Default.CameraAlt, null)

                Spacer(modifier = Modifier.width(8.dp))

                Text("Capture Photo")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---------------- LOCATION ----------------

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Location",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint = Color.Red
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = location.ifBlank { "Fetching location..." },
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---------------- PROBLEM TYPE ----------------

            Text(
                text = "Problem Type",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.height(240.dp)
            ) {

                items(issueTypes.chunked(3)) { rowItems ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        rowItems.forEach { issue ->

                            val isSelected =
                                selectedIssueType == issue.name

                            Card(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(100.dp)
                                    .clickable {

                                        selectedIssueType = issue.name

                                        vm.setIssueType(issue.name)
                                    }
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        if (isSelected)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surface
                                )
                            ) {

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Icon(
                                        imageVector = issue.icon,
                                        contentDescription = null
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(issue.name)
                                }
                            }
                        }
                    }
                }
            }

            // ---------------- OTHER FIELD ----------------

            if (selectedIssueType == "Other") {

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = otherProblemType,
                    onValueChange = {
                        otherProblemType = it
                    },
                    label = {
                        Text("Enter Problem Type")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------- DESCRIPTION ----------------

            ReportIssueDescription(vm)

            Spacer(modifier = Modifier.height(30.dp))

            // ---------------- SUBMIT BUTTON ----------------

            Button(
                onClick = {

                    if (isSubmitting) return@Button

                    val finalProblemType =
                        if (selectedIssueType == "Other")
                            otherProblemType.trim()
                        else
                            selectedIssueType

                    if (selectedImageUri == null) {

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "Please capture image"
                            )
                        }

                        return@Button
                    }

                    if (finalProblemType.isBlank()) {

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "Please select problem type"
                            )
                        }

                        return@Button
                    }

                    if (location.isBlank()) {

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "Location not available"
                            )
                        }

                        return@Button
                    }

                    val latLng = location.split(",")

                    if (latLng.size != 2) {

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "Invalid location"
                            )
                        }

                        return@Button
                    }

                    isSubmitting = true

                    vm.submitReportToBackend(
                        problemType = finalProblemType,
                        description = report.description ?: "",
                        latitude = latLng[0].trim(),
                        longitude = latLng[1].trim(),
                        imageUri = selectedImageUri!!
                    )
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {

                if (isSubmitting) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text("Submitting...")

                } else {

                    Text("Submit Report")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun checkAndRequestLocationSettings(
    context: android.content.Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (String) -> Unit
) {

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1000L
    ).build()

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)

    val client = LocationServices.getSettingsClient(context)

    client.checkLocationSettings(builder.build())
        .addOnSuccessListener {

            fetchLocation(
                context,
                fusedLocationClient
            ) { location ->

                onLocationReceived(location)
            }
        }
        .addOnFailureListener { exception ->

            if (exception is ResolvableApiException) {

                try {

                    exception.startResolutionForResult(
                        context as android.app.Activity,
                        1001
                    )

                } catch (sendEx: IntentSender.SendIntentException) {

                    sendEx.printStackTrace()
                }
            }
        }
}