package com.depromeet.team6.data.datalocal.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.depromeet.team6.R
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService : FirebaseMessagingService() {

    private lateinit var body: String
    private lateinit var title: String

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("FCM", "[FCM] FcmService -> token: $token")
    }

    override fun handleIntent(intent: Intent?) {
        body = intent?.extras?.getString("gcm.notification.body") ?: ""
        title = intent?.extras?.getString("gcm.notification.title") ?: ""

        val new = intent?.apply {
            val temp = extras?.apply {
                remove(Constants.MessageNotificationKeys.ENABLE_NOTIFICATION)
                remove("gcm.notification.e")
            }
            replaceExtras(temp)
        }
        super.handleIntent(new)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "[FCM] FcmService -> data: ${message.data}")
        Log.d("FCM", "[FCM] FcmService -> notification: ${message.notification}")

        val title = title
        val body = body
        val type = message.data["type"] ?: ""

        Log.d("FCM", "[FCM] FcmService -> title: $title")
        Log.d("FCM", "[FCM] FcmService -> body: $body")
        Log.d("FCM", "[FCM] FcmService -> type: $type")

        if (message.notification != null) {
            sendHeadsUpNotification(title, body)
        }
        if (message.data.isNotEmpty()) {
            if (type == "FULL_SCREEN_ALERT") {
                wakeLockAcquire()
                startLockScreenService()
            } else if (type == "PUSH_ALERT") {
                wakeLockAcquire()
                sendHeadsUpNotification(title, body)
            } else {
                sendDefaultNotification()
            }
        } else {
            Log.d("FCM", "[FCM] FcmService -> 알림 및 데이터 필드 모두 없음")
        }
    }

    private fun wakeLockAcquire() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "Atcha:WakeLock"
            )

            // 10초 동안 화면 유지
            wakeLock.acquire(10 * 1000L)
        } catch (e: Exception) {
            Log.e("FCM", "WakeLock error: ${e.message}")
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

    private fun sendHeadsUpNotification(title: String?, body: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "ATCHA_HEADS_UP_CHANNEL"
        val channelName = "앗차 중요 알림"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.description = "앗차 중요 알림"
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)

        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(this, channelId)
            .setContentTitle(title ?: "앗차")
            .setContentText(body ?: "막차 출발 시간을 확인해보세요!")
            .setSmallIcon(R.drawable.ic_app_logo_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .build()

        notificationManager.notify(9999, notification)
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
