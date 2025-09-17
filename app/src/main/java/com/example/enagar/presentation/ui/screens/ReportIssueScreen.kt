
package com.example.enagar.presentation.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.enagar.domain.models.IssueType
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import kotlinx.coroutines.launch
import java.util.jar.Manifest
import kotlin.contracts.contract

@Preview
@Composable
private fun prehsfs() {
    val navController = rememberNavController()
    ReportIssuesScreen(navController)

}
// 2. Report Issue Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssuesScreen(navController: NavController) {

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var location by remember { mutableStateOf("") }
    var isLocatonAvailable by remember { mutableStateOf(false) }
    var isLocationSettingEnabled by remember { mutableStateOf(false) }


    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract= ActivityResultContracts.StartIntentSenderForResult()
    ) {
        result->
        Log.d("TAG", "ReportIssuesScreen: system location enable request")
        if (result.resultCode == android.app.Activity.RESULT_OK){
            isLocationSettingEnabled = true
            // now try to fetch location
            fetchLocation(context,fusedLocationClient){
                loc->
                location = loc
                isLocatonAvailable = loc.isNotBlank() && loc != "Location not available"
                Log.d("TAG", "ReportIssuesScreen: location after setting enabled")
            }
        }else{
            Log.d("TAG", "Location settings not enabled by user")
            Toast.makeText(context, "Location is required for this feature", Toast.LENGTH_LONG).show() }


    }
    // Debug logs
    fun logDebug(message: String) {
        Log.d("ReportIssuesScreen", message)
        println("ReportIssuesScreen: $message") // Console log for easier debugging
    }

    // camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success->
        if (success){
            selectedImageUri = tempUri
        }
        else{
            Toast.makeText(context,"Image capture failed", Toast.LENGTH_LONG).show()
        }
    }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
            uri : Uri? ->
        selectedImageUri = uri

    }
    // location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract  = ActivityResultContracts.RequestMultiplePermissions()
    ){permissions->
        locationPermissionGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationPermissionGranted ){


        }
        else{
            permissionDenied = true
        }
    }
    // camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){
        isGranted->
        cameraPermissionGranted = isGranted
        if (!isGranted){
            Toast.makeText(context,"Camera permission is required to capture images", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkAndRequestLocalionSettings(){
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, // priority
            1000L                            // interval in millis
            )
            .setMinUpdateIntervalMillis(500L)
            .setMaxUpdateDelayMillis(1000L)
            .build()

        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        val settingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener{
                Log.d("TAG", "checkAndRequestLocalionSettings: Location Setting already enabled")
                isLocationSettingEnabled = true
                fetchLocation(context,fusedLocationClient){loc->
                    location = loc
                    isLocatonAvailable = loc.isNotBlank() && loc != "Location not available"
                    Log.d("TAG", "checkAndRequestLocalionSettings: location fetched successfully ${loc}")

                }
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException){
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        locationSettingsLauncher.launch(intentSenderRequest)
                    }catch (e: IntentSender.SendIntentException){
                        Log.e("TAG", "Error showing location settings dialog", e)
                        Toast.makeText(context, "Unable to show location settings", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Log.e("TAG", "Location settings check failed", exception)
                    Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
                }
            }



    }

    fun chedkAndRequestLocationPermission(){
        Log.d("TAG", "chedkAndRequestLocationPermission: ab location permission milni chiye ")
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        Log.d("TAG", "chedkAndRequestLocationPermission: fineLocatin value -> ${fineLocationGranted} and coarseLocation value-> ${coarseLocationGranted}")
        if (fineLocationGranted || coarseLocationGranted){
            locationPermissionGranted = true
            checkAndRequestLocalionSettings()
        }
        else{
            Log.d("TAG", "Location permissions not granted, requesting permissions")
            // request location permission
            locationPermissionLauncher.launch(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Automatically request permission when this screen loads
    LaunchedEffect(Unit) {

        chedkAndRequestLocationPermission()
        // check camera permission
        cameraPermissionGranted = ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED


    }

    val issueTypes = listOf(
        IssueType("Pothole", Icons.Default.Warning),
        IssueType("Drainage", Icons.Default.Water),
        IssueType("Streetlight", Icons.Default.Lightbulb),
        IssueType("Traffic", Icons.Default.Traffic),
        IssueType("Garbage", Icons.Default.Delete),
        IssueType("Other", Icons.Default.MoreHoriz)
    )




    Scaffold(
        snackbarHost = {SnackbarHost(snackbarHostState)},
        topBar = {
            TopAppBar(
                title = { Text("Report an Issue") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Photo Upload Section
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f)
                        .background(Color(0xFFF5F5F5))
                    ,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent

                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    // show  image if available
                    if (selectedImageUri != null) {
                        // display captured image
                        selectedImageUri?.let { uri->
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Captured Image",
                                modifier = Modifier.fillMaxSize()
                                ,
                                contentScale = ContentScale.Fit
                            )
                        }

                    } else {
                        // Placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No Photo Selected",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }

                }
                Spacer(modifier = Modifier.height(4.dp))
                // camera Button
                Button(
                    onClick = {
                        Log.d("TAG", "Capture Photo clicked")
                        // check location permission and availability
                        if (!isLocatonAvailable){
                            chedkAndRequestLocationPermission()
                            return@Button
                        }
                        if (cameraPermissionGranted){
                            val uri = createImageUri(context)
                            if (uri!= null){
                                tempUri = uri
                                cameraLauncher.launch(uri)
                            }else{
                                Toast.makeText(context, "Failed to create image URI", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            // if permission is not granted then request for permission
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(),
//                    enabled = isLocatonAvailable

                ) {
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 3.dp, end = 3.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture Photo",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Capture Photo",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                //  Gallery Button
                Button(
                    onClick = {
                        if (!isLocatonAvailable){
                            chedkAndRequestLocationPermission()
                            return@Button
                        }
                        galleryLauncher.launch("image/*")

                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(),
//                    enabled = isLocatonAvailable

                ) {
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 3.dp, end = 3.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Upload Photo",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Upload Photo",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                if (permissionDenied){
                    LaunchedEffect(permissionDenied) {
                        snackbarHostState.showSnackbar("Location permission is required to auto-detect location.")
                        Toast.makeText(context,"Location permission is required to auto-detect location.", Toast.LENGTH_LONG).show()
                        permissionDenied = false
                    }

                }

            }


            Spacer(modifier = Modifier.height(24.dp))
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFffffff
                    )
                ),
                elevation = CardDefaults.elevatedCardElevation(5.dp),
                border = CardDefaults.outlinedCardBorder(true)) {
                // Location Section
                Column(modifier = Modifier
                    .padding(0.dp)
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment =Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Location",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(5.dp)
                    )


                    // Map Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFF4CAF50),)
                            .padding(0.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Current Location",
                                tint = Color.Red,
                                modifier = Modifier

                                    .size(32.dp)
                            )
                            if (location.isNotBlank()){
                                Text(
                                    text = "Location: $location",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }

                        }

                    }
                }

            }


            Spacer(modifier = Modifier.height(24.dp))

            // Problem Type Section
            Text(
                text = "Problem Type",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )



            LazyColumn(
                modifier = Modifier.height(240.dp)
            ) {
                items(issueTypes.chunked(3)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { issue ->
                            IssueTypeItem(issue)
                        }
                        // Fill remaining space if less than 3 items
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description Section
            Text(
                text = "Describe the Issue in Brief (Optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text("Enter description...") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(

                onClick = {
                    if (selectedImageUri != null && location.isNotBlank()){
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Issue Reported Successfully")
                            navController.navigate("report_submitted")
                        }
                    }
                    else{
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill all required details")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Submit Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun IssueTypeItem(issue: IssueType) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { }
    ) {
        Card(
            modifier = Modifier.size(60.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF73F0AB)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = issue.icon,
                    contentDescription = issue.name,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = issue.name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}


fun  createImageUri(context: Context) : Uri?{
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/eNagar")
    }
    return try{
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }catch (e: Exception){
        e.printStackTrace()
        null
    }

}
private fun fetchLocation(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocation: (String) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("TAG", "fetchLocation: Permissions granted, trying to get location")

        // First try to get last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
           if (location !=null){
               Log.d("TAG", "fetchLocation: Got last location: ${location.latitude}, ${location.longitude}")
               onLocation("${location.latitude}, ${location.longitude}")
           }
            else{
               Log.d("TAG", "fetchLocation: Last location is null, requesting current location")
               // If last location is null, request current location
               requestCurrentLocation(context, fusedLocationClient, onLocation)
           }
        }.addOnFailureListener {
            Log.e("TAG", "fetchLocation: Failed to get last location", it)
            requestCurrentLocation(context, fusedLocationClient, onLocation)
        }
    }
    else{
        onLocation("Location not available")
    }
}

// Added: Function to actively request current location with timeout handling
private fun requestCurrentLocation(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocation: (String) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        onLocation("Location not available")
        return
    }

    Log.d("TAG", "requestCurrentLocation: Requesting current location")

    try {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10 seconds
        ).apply {
            setMaxUpdates(1)
            setWaitForAccurateLocation(false)
        }.build()

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d("TAG", "requestCurrentLocation: Got current location: ${location.latitude}, ${location.longitude}")
                    onLocation("${location.latitude}, ${location.longitude}")
                    fusedLocationClient.removeLocationUpdates(this)
                } ?: run {
                    Log.d("TAG", "requestCurrentLocation: Current location result is null")
                    onLocation("Location not available")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        // Timeout after 15 seconds with fallback location
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Log.d("TAG", "requestCurrentLocation: Timeout - using fallback location")
            // UPDATED: Fallback to Delhi coordinates for testing
            onLocation("28.7041, 77.1025")
        }, 15000)

    } catch (e: Exception) {
        Log.e("TAG", "requestCurrentLocation: Error requesting location", e)
        onLocation("Location not available")
    }
}
