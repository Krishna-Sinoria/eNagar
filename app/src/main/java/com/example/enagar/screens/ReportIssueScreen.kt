package com.example.enagar.screens

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.enagar.navigation.Screen
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

data class CategoryItem(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssuesScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tree") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            }
        }
    }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { _ -> }

    val categories = listOf(
        CategoryItem(Icons.Default.Forest, "Tree"),
        CategoryItem(Icons.Default.Delete, "Garbage"),
        CategoryItem(Icons.Default.Lightbulb, "Streetlight"),
        CategoryItem(Icons.Default.Traffic, "Traffic"),
        CategoryItem(Icons.Default.Construction, "Pothole"),
        CategoryItem(Icons.Default.Help, "Other")
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                    }
                },
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF2E7D32), Color(0xFF81C784))
                    )
                )
            )
        },
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description *") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
            )

            var locationError by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = location,
                onValueChange = { input ->
                    val regex = Regex("^[a-zA-Z0-9 ,.-]*$")
                    if (regex.matches(input)) {
                        location = input
                        locationError = input.isBlank()
                    }
                },
                label = { Text("Location(Auto Detect) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = locationError,
            )
            if (locationError) {
                Text(
                    text = "Please enter a valid address (letters & numbers only)",
                    color = Color.Red,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }

            // Category Dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                    Spacer(Modifier.width(8.dp))
                                    Text(item.label)
                                }
                            },
                            onClick = {
                                selectedCategory = item.label
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Upload Picture
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Upload Image")
                Spacer(Modifier.width(8.dp))
                Text("Upload Picture")
            }

            // Camera
            Button(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                Spacer(Modifier.width(8.dp))
                Text("Take a Photo")
            }

            // Preview image
            selectedImageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && location.isNotBlank()) {
                        navController.navigate(Screen.ReportSubmitted.route)
                    } else {
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Submit Issue")
            }
        }
    }
}
