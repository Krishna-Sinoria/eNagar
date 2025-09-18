package com.example.enagar.presentation.ui.utility_functions

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat


fun fetchLocation(
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
