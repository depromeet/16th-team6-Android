package com.depromeet.team6.presentation.util.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import timber.log.Timber

object PermissionUtil {
    private const val PREFS_NAME = "PermissionPrefs"
    private const val KEY_LOCATION_PERMISSION_REQUESTED = "location_permission_requested"
    private const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
    private const val KEY_OVERLAY_PERMISSION_REQUESTED = "overlay_permission_requested"
    private const val KEY_OVERLAY_DIALOG_SHOWN = "overlay_dialog_shown"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun savePermissionRequested(context: Context, key: String) {
        Timber.d("king : $key")
        getPreferences(context).edit().putBoolean(key, true).apply()
    }

    fun isLocationPermissionRequested(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_LOCATION_PERMISSION_REQUESTED, false)
    }

    fun isNotificationPermissionRequested(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getPreferences(context).getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false)
        } else {
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

    fun needsOverlayPermission(context: Context): Boolean {
        return !Settings.canDrawOverlays(context)
    }

    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isOverlayPermissionRequested(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_OVERLAY_PERMISSION_REQUESTED, false)
    }

    fun openOverlayPermissionSettings(context: Context) {
        savePermissionRequested(context, KEY_OVERLAY_PERMISSION_REQUESTED)
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
