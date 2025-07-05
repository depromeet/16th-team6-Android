package com.depromeet.team6.presentation.util.snackbar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult

suspend fun showLocationPermissionSnackbar(context: Context) {
    val result = SnackbarManager.showSnackbar(
        message = "위치권한이 필요합니다.",
        actionLabel = "설정열기",
        duration = SnackbarDuration.Short
    ) ?: return

    if (result == SnackbarResult.ActionPerformed) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}
