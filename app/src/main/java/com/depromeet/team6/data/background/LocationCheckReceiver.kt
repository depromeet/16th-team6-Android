package com.depromeet.team6.data.background

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@AndroidEntryPoint
class LocationCheckReceiver : BroadcastReceiver() {

    @Inject
    lateinit var userInfoRepositoryImpl: UserInfoRepositoryImpl

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        // 위치 권한이 없으면 다음날로 알림 미루기
        if (!hasLocationPermission(context)) {
            AlarmScheduler.scheduleLocationCheck(context)
            return
        }

        // 위치 권한이 있으면 현재위치 고려해서 알람추천 노티 발송
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentLocation = getCurrentLocation(context)
                if (currentLocation == null) {
                    AlarmScheduler.scheduleLocationCheck(context)
                    return@launch
                }

                val homeLatitude = userInfoRepositoryImpl.getUserHome().latitude
                val homeLongitude = userInfoRepositoryImpl.getUserHome().longitude

                val distance = calculateDistance(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    homeLatitude,
                    homeLongitude
                )

                // 집으로부터 1km 이상 떨어져있으면 알람추천 노티 발송
                if (distance > 1.0) {
                    notificationHelper.sendRecommendationNotification()
                }

                AlarmScheduler.scheduleLocationCheck(context)
            } catch (e: Exception) {
                AlarmScheduler.scheduleLocationCheck(context)
            }
        }
    }

    private fun hasLocationPermission(context: Context): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundLocationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        return (fineLocationGranted || coarseLocationGranted) && backgroundLocationGranted
    }

    private suspend fun getCurrentLocation(context: Context): Location? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        return try {
            suspendCancellableCoroutine { continuation ->
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        continuation.resume(task.result)
                    } else {
                        continuation.resume(null)
                    }
                }.addOnFailureListener { exception ->
                    continuation.resume(null)
                }
            }
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return (results[0] / 1000.0)
    }
}
