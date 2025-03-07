package com.depromeet.team6.data.datalocal.manager

import android.app.ActivityManager
import android.content.Context

fun Context.isServiceRunning(targetClass: Class<*>): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    // Foreground 서비스인지 확인
    return activityManager.getRunningServices(Int.MAX_VALUE)
        .any { it.service.className == targetClass.name }
}
