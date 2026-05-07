package com.example.enagar.presentation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.enagar.presentation.viewModel.UploadViewModel
import com.google.android.gms.location.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun TaskDetailScreen(
    navController: NavHostController,
    reportId: String
) {

    val context = LocalContext.current

    val uploadViewModel: UploadViewModel = hiltViewModel()

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var latitude by remember {
        mutableStateOf("Fetching...")
    }

    var longitude by remember {
        mutableStateOf("Fetching...")
    }

    var isUploading by remember {
        mutableStateOf(false)
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // 🔥 Permission Launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                getCurrentLocation(
                    fusedLocationClient
                ) { lat, lng ->

                    latitude = lat
                    longitude = lng
                }

            } else {

                Toast.makeText(
                    context,
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // 🔥 Auto Fetch Location
    LaunchedEffect(Unit) {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            getCurrentLocation(
                fusedLocationClient
            ) { lat, lng ->

                latitude = lat
                longitude = lng
            }

        } else {

            permissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    // 📸 Image Picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        selectedImageUri = uri
    }

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Task Details")
                }
            )
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Text(
                text = "Report ID: $reportId",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 📍 Location Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Current Location",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Latitude : $latitude")

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(text = "Longitude : $longitude")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 📷 Choose Image
            Button(
                onClick = {
                    launcher.launch("image/*")
                }
            ) {

                Text("Choose Completion Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🖼 Preview Image
            selectedImageUri?.let { uri ->

                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),

                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 🚀 Upload Button
            Button(

                onClick = {

                    if (selectedImageUri == null) {

                        Toast.makeText(
                            context,
                            "Please select image",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    if (
                        latitude == "Fetching..." ||
                        longitude == "Fetching..."
                    ) {

                        Toast.makeText(
                            context,
                            "Location not available yet",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    isUploading = true

                    val compressedFile =
                        compressImage(
                            context,
                            selectedImageUri!!
                        )

                    uploadViewModel.uploadProof(
                        context = context,
                        reportId = reportId,
                        file = compressedFile,
                        lat = latitude,
                        lng = longitude
                    )

                    Toast.makeText(
                        context,
                        "Uploading...",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 🔥 Delay navigation slightly
                    android.os.Handler(
                        Looper.getMainLooper()
                    ).postDelayed({

                        isUploading = false
                        navController.popBackStack()

                    }, 3000)
                },

                modifier = Modifier.fillMaxWidth(),

                enabled = !isUploading
            ) {

                if (isUploading) {

                    CircularProgressIndicator()

                } else {

                    Text("Upload Completion Proof")
                }
            }
        }
    }
}


// 🔥 Get Current Location
@SuppressLint("MissingPermission")
fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (String, String) -> Unit
) {

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->

            if (location != null) {

                onLocationReceived(
                    location.latitude.toString(),
                    location.longitude.toString()
                )

            } else {

                val locationRequest =
                    LocationRequest.create().apply {

                        priority =
                            Priority.PRIORITY_HIGH_ACCURACY

                        interval = 1000
                        fastestInterval = 500
                        numUpdates = 1
                    }

                fusedLocationClient.requestLocationUpdates(

                    locationRequest,

                    object : LocationCallback() {

                        override fun onLocationResult(
                            result: LocationResult
                        ) {

                            val freshLocation =
                                result.lastLocation

                            if (freshLocation != null) {

                                onLocationReceived(
                                    freshLocation.latitude.toString(),
                                    freshLocation.longitude.toString()
                                )
                            }

                            fusedLocationClient
                                .removeLocationUpdates(this)
                        }
                    },

                    Looper.getMainLooper()
                )
            }
        }
}


// 🔥 Compress Image
fun compressImage(
    context: Context,
    uri: Uri
): File {

    val inputStream =
        context.contentResolver.openInputStream(uri)

    val bitmap =
        BitmapFactory.decodeStream(inputStream)

    val file = File.createTempFile(
        "compressed_",
        ".jpg",
        context.cacheDir
    )

    val outputStream =
        FileOutputStream(file)

    // ✅ Compress to 70%
    bitmap.compress(
        Bitmap.CompressFormat.JPEG,
        70,
        outputStream
    )

    outputStream.flush()
    outputStream.close()

    return file
}