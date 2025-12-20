package com.depromeet.team6.data.datalocal.datasource

import android.content.Context
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LAT
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class GPSLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getCurrentLatLng(): LatLng {
        // 권한 체크는 UseCase나 ViewModel에서 미리 수행하는 것이 더 좋습니다.
        if (!PermissionUtil.hasLocationPermissions(context)) {
            return LatLng(DEFAULT_LAT, DEFAULT_LNG)
        }

        return try {
            suspendCancellableCoroutine { continuation ->
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude

                            Timber.d("User_Location Lat: $latitude, Lon: $longitude")
                            continuation.resume(LatLng(latitude, longitude))
                        } else {
                            Timber.d("User_Location Failed to get location")
                            continuation.resume(LatLng(DEFAULT_LAT, DEFAULT_LNG)) // 위치 정보를 가져오지 못한 경우
                        }
                    }
                    .addOnFailureListener { exception ->
                        Timber.e("User_Location Error fetching location")
                        continuation.resumeWithException(exception)
                    }
            }
        } catch (e: SecurityException) {
            Timber.e("User_Location Location permission not granted")
            LatLng(DEFAULT_LAT, DEFAULT_LNG)
        }
    }
}
