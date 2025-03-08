package com.depromeet.team6.presentation.util.context

import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun Context.getUserLocation(): Pair<Double, Double>? {
    return try {
        suspendCancellableCoroutine { continuation ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d(
                            "User_Location",
                            "Lat: ${location.latitude}, Lon: ${location.longitude}"
                        )
                        continuation.resume(Pair(location.latitude, location.longitude))
                    } else {
                        Log.d("User_Location", "Failed to get location")
                        continuation.resume(null) // 위치 정보를 가져오지 못한 경우
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("User_Location", "Error fetching location", exception)
                    continuation.resumeWithException(exception)
                }
        }
    } catch (e: SecurityException) {
        Log.e("User_Location", "Location permission not granted", e)
        null
    }
}
