package com.depromeet.team6.data.datalocal.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import com.depromeet.team6.data.datalocal.service.LocationCheckReceiver
import com.depromeet.team6.data.datalocal.service.LockService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

abstract class BaseForegroundServiceManager<T : Service>(
    val context: Context,
    val targetClass: Class<T>
) {
    fun start() = synchronized(this) {
        val intent = Intent(context, targetClass)
        if (!context.isMyServiceRunning(targetClass)) {
            context.startForegroundService(intent)
        }
    }

    fun stop() = synchronized(this) {
        val intent = Intent(context, targetClass)
        if (context.isMyServiceRunning(targetClass)) {
            context.stopService(intent)
        }
    }
}

@Singleton
class LockServiceManager @Inject constructor(
    @ApplicationContext val applicationContext: Context
) : BaseForegroundServiceManager<LockService>(
    context = applicationContext,
    targetClass = LockService::class.java
) {

    private val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleLocationCheck() {
        val intent = Intent(applicationContext, LocationCheckReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 11) // TODO 테스트 이후 22:00로 변경
        calendar.set(Calendar.MINUTE, 2)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}

fun Context.isMyServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}