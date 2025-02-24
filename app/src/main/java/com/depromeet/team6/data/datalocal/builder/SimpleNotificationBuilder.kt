package com.depromeet.team6.data.datalocal.builder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.annotation.DrawableRes

object SimpleNotificationBuilder {
    fun createChannel(
        channelId: String,
        name: String,
        importance: Int = NotificationManager.IMPORTANCE_HIGH,
        description: String
    ) =
        NotificationChannel(channelId, name, importance).apply {
            setShowBadge(false)
            enableLights(true)
            this.description = description
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            lightColor = Color.BLACK
        }

    fun createBuilder(
        context: Context,
        channelId: String,
        title: String,
        text: String,
        @DrawableRes icon: Int
    ) = Notification.Builder(context, channelId).apply {
        setOngoing(true)
        setShowWhen(true)
        setSmallIcon(icon)
        setContentTitle(title)
        setContentText(text)
    }.build()
}
