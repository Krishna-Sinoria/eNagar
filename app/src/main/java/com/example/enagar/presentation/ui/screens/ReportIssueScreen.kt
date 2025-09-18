
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.enagar.domain.models.IssueType
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.ui.components.ReportIssueDescription
import com.example.enagar.presentation.ui.components.ReportIssueTopBar
import com.example.enagar.presentation.ui.utility_functions.IssueTypeItem
import com.example.enagar.presentation.ui.utility_functions.createImageUri
import com.example.enagar.presentation.ui.utility_functions.fetchLocation
import com.example.enagar.presentation.viewModel.CitizenViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssuesScreen(navController: NavController) {
    val vm : CitizenViewModel = hiltViewModel()
    val report = vm.issueReport.value


    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var location by remember { mutableStateOf("") }
    var isLocatonAvailable by remember { mutableStateOf(false) }

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract= ActivityResultContracts.StartIntentSenderForResult()
    ) {
        result->
        Log.d("TAG", "ReportIssuesScreen: system location enable request")
        if (result.resultCode == android.app.Activity.RESULT_OK){
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
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        topBar = {ReportIssueTopBar(navController) },
        containerColor = colorScheme.background
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
                color = colorScheme.primary,
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
                        .background(color =  if (selectedImageUri != null) Color.Transparent else Color(0xFFD1CDCD))
                    ,
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedImageUri != null) Color.Transparent else Color(0xFFD1CDCD)

                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    // show  image if available
                    if (selectedImageUri != null) {

                        // display captured image
                        selectedImageUri?.let { uri->
                            vm.setImageUri(uri)
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
                                color = colorScheme.primary,
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
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Capture Photo",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))


                if (permissionDenied){
                    LaunchedEffect(permissionDenied) {
                        snackbarHostState.showSnackbar("Location permission is required to auto-detect location.")
                        Toast.makeText(context,"Location permission is required to auto-detect location.", Toast.LENGTH_LONG).show()
                        permissionDenied = false
                    }

                }

            }


            Spacer(modifier = Modifier.height(24.dp))

            // Location Section
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.background
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
                            .background(colorScheme.background,)
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
                                vm.setLocation(location)
                                Text(
                                    text = "Location: $location",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    fontSize = 14.sp,
                                    color = colorScheme.primary,
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
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally),
                color = colorScheme.primary
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
                            IssueTypeItem(issue){selectedIssue->
                                vm.setIssueType(selectedIssue.name)

                            }
                        }
                        // Fill remaining space if less than 3 items
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            ReportIssueDescription(vm)

            // Description Section

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(

                onClick = {
                    Log.d("TAG", "ReportIssuesScreen: submit button ${report.location} && ${report.imageUri}")
                    if (report.imageUri != null && report.location!!.isNotBlank()){
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Issue Reported Successfully")
                            navController.navigate(Screen.ReportSubmitted.route)
                            vm.generateReportId()
                            Log.d("TAG", "ReportIssuesScreen: ${vm.reportId}")
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
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
