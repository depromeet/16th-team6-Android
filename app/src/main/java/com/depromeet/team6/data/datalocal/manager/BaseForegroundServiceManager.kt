package com.depromeet.team6.data.datalocal.manager

import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import com.depromeet.team6.data.datalocal.service.LockService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

abstract class BaseForegroundServiceManager<T : Service>(
    val context: Context,
    val targetClass: Class<T>
) {
    fun start() = synchronized(this) {
        val intent = Intent(context, targetClass)

        if (!context.isServiceRunning(targetClass)) {
            Log.d("", "startForegroundService")
            context.startForegroundService(intent)
        }
    }

    fun stop() = synchronized(this) {
        val intent = Intent(context, targetClass)

        if (context.isServiceRunning(targetClass)) {
            context.stopService(intent)
        }
    }
}

class LockServiceManager @Inject constructor(
    @ApplicationContext val applicationContext: Context
) : BaseForegroundServiceManager<LockService>(
    context = applicationContext,
    targetClass = LockService::class.java
)
