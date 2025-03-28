package com.depromeet.team6.data.datalocal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.depromeet.team6.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Timber.d("[FCM] FcmService -> token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Timber.d("[FCM] FcmService -> data: ${message.data}")
        Timber.d("[FCM] FcmService -> data: ${message.notification}")

        val title = message.notification?.title
        val body = message.notification?.body
        val type = message.data["type"] ?: ""

        if (message.data.isNotEmpty()) {
            if (type == "FULL_SCREEN_ALERT")
            // TODO: 잠금화면 표출
            else if (type == "PUSH_ALERT")
                sendNotification(title, body)
            else
                sendDefaultNotification()
        } else {
            Timber.d("[FCM] FcmService -> empty data")
        }

    }

    private fun sendNotification(title: String?, body: String?) {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title ?: "")
            .setContentText(body ?: "막차 출발 시간을 확인해보세요!")
            .setSmallIcon(R.drawable.ic_app_logo_foreground)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(FCM_NOTIFICATION_ID, notification)

    }

    private fun sendDefaultNotification() {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("")
            .setContentText("막차 출발 시간을 확인해보세요!")
            .setSmallIcon(R.drawable.ic_app_logo_foreground)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(FCM_NOTIFICATION_ID, notification)

    }


    companion object {
        private const val CHANNEL_ID = "ATCHA_CHANNEL"
        private const val CHANNEL_NAME = "ATCHA"
        private const val FCM_NOTIFICATION_ID = 0
    }
}