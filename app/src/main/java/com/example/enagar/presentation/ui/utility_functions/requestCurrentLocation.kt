package com.example.enagar.presentation.ui.utility_functions

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat


// Added: Function to actively request current location with timeout handling
fun requestCurrentLocation(
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
