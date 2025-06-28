package com.depromeet.team6.presentation.util.context

import android.content.Context
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LAT
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LNG
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun Context.getUserLocation(): LatLng {
    return try {
        suspendCancellableCoroutine { continuation ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        Timber.d("User_Location Lat: $latitude, Lon: $longitude")

                        // 위도 또는 경도가 비정상일 경우 기본 위치로 설정
                        if (latitude <= 0 || longitude <= 0) {
                            Timber.d("위도, 경도가 0보다 작음")
                            continuation.resume(LatLng(DEFAULT_LAT, DEFAULT_LNG))
                        } else {
                            continuation.resume(LatLng(latitude, longitude))
                        }
                    } else {
                        Timber.d("User_Location Failed to get location")
                        continuation.resume(LatLng(DEFAULT_LAT, DEFAULT_LNG)) // 위치 정보를 가져오지 못한 경우
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.e("User_Location Error fetching location", exception)
                    continuation.resumeWithException(exception)
                }
        }
    } catch (e: SecurityException) {
        Timber.e("User_Location Location permission not granted", e)
        LatLng(DEFAULT_LAT, DEFAULT_LNG)
    }
}
