package com.depromeet.team6.presentation.ui.alarm

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

@Keep
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(NotificationScheduler.EXTRA_NOTIFICATION_TITLE) ?: "앗차"
        val message = intent.getStringExtra(NotificationScheduler.EXTRA_NOTIFICATION_MESSAGE) ?: "지금 바로 출발하셔야 해요!"

        // 메인 액티비티로 이동하기 위한 인텐트
        val contentIntent = Intent(
            context,
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.component?.className?.let {
                Class.forName(it)
            }
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            NotificationScheduler.NOTIFICATION_REQUEST_CODE,
            contentIntent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 알림 생성
        val builder = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 알림 표시
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(NotificationScheduler.NOTIFICATION_ID, builder.build())
    }
}
