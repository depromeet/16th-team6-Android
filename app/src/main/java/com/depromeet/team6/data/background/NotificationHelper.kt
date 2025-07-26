package com.depromeet.team6.data.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext val context: Context
) {

    companion object {
        private const val RECOMMENDATION_CHANNEL_ID = "atcha_recommendation"
        private const val RECOMMENDATION_CHANNEL_NAME = "10시 알람 추천"
        private const val ALARM_PUSH_CHANNEL_ID = "atcha_additional_alarm_push"
        private const val ALARM_PUSH_CHANNEL_NAME = "알람 전 푸시 알림"

        private const val ALARM_AWARE_5_MIN = "출발 5분 전이에요"
        private const val ALARM_AWARE_10_MIN = "출발 10분 전이에요"
        private const val ALARM_AWARE_15_MIN = "출발 15분 전이에요"
        private const val ALARM_AWARE_30_MIN = "출발 30분 전이에요"
        private const val ALARM_AWARE_1_HOUR = "출발 1시간 전이에요"

        private const val LOCATION_NOTIFICATION_ID = 1001
        private const val ALARM_AWARE_NOTIFICATION_ID = 2001
    }

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        createChannel(ALARM_PUSH_CHANNEL_ID, ALARM_PUSH_CHANNEL_NAME)
        createChannel(RECOMMENDATION_CHANNEL_ID, RECOMMENDATION_CHANNEL_NAME)
    }

    private fun createChannel(
        id: String,
        name: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(id, name, importance))
        }
    }

    fun sendAlarmAwareNotification(min: Int) {
        val channelId = ALARM_PUSH_CHANNEL_ID

        notificationManager.createNotificationChannel(NotificationChannel(channelId, ALARM_PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT))

        val message = when (min) {
            5 -> ALARM_AWARE_5_MIN
            10 -> ALARM_AWARE_10_MIN
            15 -> ALARM_AWARE_15_MIN
            30 -> ALARM_AWARE_30_MIN
            60 -> ALARM_AWARE_1_HOUR
            else -> return@sendAlarmAwareNotification
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_app_logo_foreground)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(ALARM_AWARE_NOTIFICATION_ID + min, notification)
    }

    fun sendRecommendationNotification() {
        val channelId = RECOMMENDATION_CHANNEL_ID

        notificationManager.createNotificationChannel(NotificationChannel(channelId, RECOMMENDATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT))

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            LOCATION_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentText(getString(context, R.string.notification_ten_text))
            .setSmallIcon(R.drawable.ic_app_logo_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(LOCATION_NOTIFICATION_ID, notification)
    }
}
