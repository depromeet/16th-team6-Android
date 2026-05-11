package com.depromeet.team6.data.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.WorkManager
import com.depromeet.team6.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

object AlarmScheduler {

    private const val LOCATION_NOTIFICATION_ID = 1001
    private const val ALARM_START_ID = 2001
    private const val ALARM_AWARE_NOTIFICATION_ID = 2002
    const val ADDITIONAL_PUSH_WORK_NAME = "additional_push_alarm_work"
    const val ADDITIONAL_PUSH_WORK_TAG = "additional_push_alarm_tag"

    fun scheduleLockScreenAlarm(context: Context, alarmTimeStamp: String) {
        unScheduleLockAlarm(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // 디버그 모드에서는 알람설정 후 6분뒤 바로 울림
        val alarmTimeInMillis = if (BuildConfig.DEBUG) isoLocalDateTimeToMillis(alarmTimeStamp) - (60_000L * 6 - 10_000L) else isoLocalDateTimeToMillis(alarmTimeStamp)

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
                alarmTimeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTimeInMillis,
                pendingIntent
            )
        }
    }

    fun scheduleAdditionalPushAlarm(context: Context, alarmTime: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmTimeMillis = isoLocalDateTimeToMillis(alarmTime)

        // 10분 전 푸시알림
        val pushTime = 10
        val pushInterval = 10 * 60_000L
        val pushTimeMillis = alarmTimeMillis - pushInterval

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("alarmTime", pushTime)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_AWARE_NOTIFICATION_ID + pushTime,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

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

    fun unScheduleLockAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // 잠금화면 포그라운드 서비스 해제
        val lockAlarmIntent = Intent(context, LockService::class.java)
        val lockAlarmPendingIntent = PendingIntent.getForegroundService(
            context,
            ALARM_START_ID,
            lockAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(lockAlarmPendingIntent)
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(RuntimeException("deleteAlarm 오류 : 알람취소를 눌렀지만 실제로 unschedule 로직이 실행되지 않음"))
        }
    }

    private fun unScheduleAdditionalPushAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pushTime = 10
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_AWARE_NOTIFICATION_ID + pushTime,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(RuntimeException("deleteAlarm 오류 : 추가 푸시 알람 unschedule 실패"))
        }
    }

    fun cancelAdditionalPushWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(ADDITIONAL_PUSH_WORK_NAME)
        WorkManager.getInstance(context).cancelAllWorkByTag(ADDITIONAL_PUSH_WORK_TAG)
    }

    fun unScheduleAllAlarms(context: Context) {
        unScheduleLockAlarm(context)
        unScheduleAdditionalPushAlarm(context)
        cancelAdditionalPushWork(context)

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
