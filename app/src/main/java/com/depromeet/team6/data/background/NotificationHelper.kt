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
import com.depromeet.team6.data.background.LockService.Companion.LOCATION_NOTIFICATION_ID
import com.depromeet.team6.presentation.ui.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor (
    @ApplicationContext val context: Context
) {

    companion object {
        private const val RECOMMENDATION_CHANNEL_ID   = "atcha_recommendation"
        private const val RECOMMENDATION_CHANNEL_NAME = "10시 알람 추천"
        private const val DEFAULT_CHANNEL_ID   = "default_channel"
        private const val DEFAULT_CHANNEL_NAME = "일반 알림"
    }

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        createChannel(DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME)
        createChannel(RECOMMENDATION_CHANNEL_ID, RECOMMENDATION_CHANNEL_NAME)
    }

    private fun createChannel(
        id: String,
        name: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(id, name, importance))
        }
    }

    fun sendRecommendationNotification() {
        Timber.d("Notification Recommendation : 유후~~!")
        val channelId = RECOMMENDATION_CHANNEL_ID

        notificationManager.createNotificationChannel(NotificationChannel(channelId, RECOMMENDATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT))

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
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
