package com.depromeet.team6.presentation.ui.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import androidx.core.app.NotificationCompat
import com.depromeet.team6.R
import java.text.SimpleDateFormat
import java.util.Locale

@Keep
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
        val name = context.getString(R.string.notification_channel_name)
        val descriptionText = context.getString(R.string.notification_channel_description_text)
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
            val dateFormat = SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.getDefault())
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

    // 이미 표시된 알림 취소
    fun cancelDisplayedNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    // 예약된 알림 취소
    fun cancelScheduledNotification() {
        // NotificationReceiver를 타겟으로 한 PendingIntent를 얻기
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE // 기존 PendingIntent가 없으면 null 반환
        )

        // 기존 PendingIntent가 있으면 취소
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    // 모든 알림 취소 (표시된 알림과 예약된 알림 모두)
    fun cancelAllNotifications() {
        cancelDisplayedNotification() // 표시된 알림 취소
        cancelScheduledNotification() // 예약된 알림 취소
    }
}
