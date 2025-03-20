package com.depromeet.team6.presentation.util.permission

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionUtil {
    private const val PREFS_NAME = "PermissionPrefs"
    private const val KEY_LOCATION_PERMISSION_REQUESTED = "location_permission_requested"
    private const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun savePermissionRequested(context: Context, key: String) {
        getPreferences(context).edit().putBoolean(key, true).apply()
    }

    fun isLocationPermissionRequested(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_LOCATION_PERMISSION_REQUESTED, false)
    }

    fun isNotificationPermissionRequested(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getPreferences(context).getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false)
        }else{
            savePermissionRequested(context, KEY_NOTIFICATION_PERMISSION_REQUESTED)
            return true
        }
    }

    fun hasLocationPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // 안드로이드 12 이하에서는 자동 허용
        }
    }

    fun requestLocationPermissions(
        context: Context,
        locationPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
    ) {
        savePermissionRequested(context, KEY_LOCATION_PERMISSION_REQUESTED)
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun requestNotificationPermission(
        context: Context,
        notificationPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savePermissionRequested(context, KEY_NOTIFICATION_PERMISSION_REQUESTED)
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
