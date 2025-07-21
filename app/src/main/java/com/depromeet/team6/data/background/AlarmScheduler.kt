package com.depromeet.team6.data.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

object AlarmScheduler {

    private const val LOCATION_NOTIFICATION_ID = 1001
    private const val ALARM_START_ID = 2001
    private const val ALARM_AWARE_NOTIFICATION_ID = 2002

    fun scheduleLockScreenAlarm(context: Context, timeStamp: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Timber.d("AlarmScheduler : 알람 시간 미뤄짐 : $timeStamp")

        val timeInMillis = isoLocalDateTimeToMillis(timeStamp)

        // 잠금화면 포그라운드 서비스 할당
        val intent = Intent(context, LockService::class.java)
        val pendingIntent = PendingIntent.getForegroundService(
            context,
            ALARM_START_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val exactSupported = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        if (exactSupported) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        }
    }

    fun scheduleAdditionalPushAlarm(context: Context, alarmTime: String, pushTimes: Set<Int>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmTimeMillis = isoLocalDateTimeToMillis(alarmTime)

        for (pushTime in pushTimes) {
            val pushInterval = pushTime * 60_000L
            val pushTimeMillis = alarmTimeMillis - pushInterval

            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("alarmTime", pushTime)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_AWARE_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val exactSupported = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
            if (exactSupported) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    pushTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    pushTimeMillis,
                    pendingIntent
                )
            }
        }
    }

    fun scheduleLocationCheck(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                scheduleInexactAlarm(context)
                return
            }
        }

        val intent = Intent(context, LocationCheckReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            LOCATION_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 22)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            scheduleInexactAlarm(context)
        }
    }

    private fun scheduleInexactAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, LocationCheckReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            LOCATION_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 22)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun isoLocalDateTimeToMillis(
        isoTime: String
    ): Long {
        val dateTime = LocalDateTime.parse(isoTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val zone = ZoneId.systemDefault()

        val instant = dateTime.atZone(zone).toInstant()

        // 3) epoch millis 반환
        return instant.toEpochMilli()
    }
}
