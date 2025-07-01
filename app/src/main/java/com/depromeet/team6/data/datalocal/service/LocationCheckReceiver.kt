package com.depromeet.team6.data.datalocal.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocationCheckReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, LockService::class.java).apply {
            putExtra(LockService.EXTRA_CHECK_LOCATION, true)
        }

        context.startForegroundService(serviceIntent)
    }
}
