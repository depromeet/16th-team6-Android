package com.depromeet.team6.presentation.ui.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationScheduler(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "app_notification_channel"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_REQUEST_CODE = 100
        const val EXTRA_NOTIFICATION_TITLE = "notification_title"
        const val EXTRA_NOTIFICATION_MESSAGE = "notification_message"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "앱 알림"
        val descriptionText = "앱 내부 알림 채널"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    // 즉시 알림 표시
    fun showNotification(title: String, message: String) {
        val intent = Intent(context, context.javaClass).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    // 특정 시간에 알림 예약 및 표시
    fun scheduleNotificationForTime(title: String, message: String, dateTimeString: String) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(dateTimeString)

            date?.let {
                val timeInMillis = it.time

                val intent = Intent(context, NotificationReceiver::class.java).apply {
                    putExtra(EXTRA_NOTIFICATION_TITLE, title)
                    putExtra(EXTRA_NOTIFICATION_MESSAGE, message)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                // 알람 설정
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 시간 변환 실패 시 즉시 알림 표시
            showNotification(title, message)
        }
    }
}