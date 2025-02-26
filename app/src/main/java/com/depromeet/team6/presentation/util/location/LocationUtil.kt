package com.depromeet.team6.presentation.util.location

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat

object LocationUtil {
    private const val PREFS_NAME = "PermissionPrefs"
    private const val KEY_LOCATION_PERMISSION_REQUESTED = "location_permission_requested"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun saveLocationPermissionRequested(context: Context) {
        getPreferences(context).edit().putBoolean(KEY_LOCATION_PERMISSION_REQUESTED, true).commit()
    }

    fun isLocationPermissionRequested(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_LOCATION_PERMISSION_REQUESTED, false)
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

    fun requestLocationPermissions(
        context: Context,
        locationPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
    ) {
        saveLocationPermissionRequested(context)
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
