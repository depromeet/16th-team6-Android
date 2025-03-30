package com.depromeet.team6.data.datalocal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.depromeet.team6.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("FCM","[FCM] FcmService -> token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM","[FCM] FcmService -> data: ${message.data}")
        Log.d("FCM","[FCM] FcmService -> notification: ${message.notification}")

        val title = message.notification?.title
        val body = message.notification?.body
        val type = message.data["type"] ?: ""

        Log.d("FCM","[FCM] FcmService -> title: $title")
        Log.d("FCM","[FCM] FcmService -> body: $body")
        Log.d("FCM","[FCM] FcmService -> type: $type")

        if (message.notification != null) {
            sendNotification(title, body)
        }
        if (message.data.isNotEmpty()) {
            if (type == "FULL_SCREEN_ALERT"){
                startLockScreenService()
            }
            else if (type == "PUSH_ALERT")
                sendNotification(title, body)
            else
                sendDefaultNotification()
        } else {
            Log.d("FCM","[FCM] FcmService -> 알림 및 데이터 필드 모두 없음")
        }
    }

    private fun sendNotification(title: String?, body: String?) {
        Log.d("FCM", "[FCM] sendNotification 호출됨 - title: $title, body: $body")

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title ?: "앗차")
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

    private fun startLockScreenService() {

        // LockService를 시작하는 인텐트 생성
        val intent = Intent(this, LockService::class.java).apply {
            // 잠금화면 표시 플래그 설정
            putExtra(LockService.EXTRA_SHOW_LOCK_SCREEN, true)
        }

         ContextCompat.startForegroundService(this, intent)

    }

    companion object {
        private const val CHANNEL_ID = "ATCHA_CHANNEL"
        private const val CHANNEL_NAME = "ATCHA"
        private const val FCM_NOTIFICATION_ID = 0
    }
}