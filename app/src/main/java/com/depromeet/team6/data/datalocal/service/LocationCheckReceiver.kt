package com.depromeet.team6.data.datalocal.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class LocationCheckReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("LocationCheck", "위치 확인 알람 수신됨")

        val serviceIntent = Intent(context, LockService::class.java).apply {
            putExtra(LockService.EXTRA_CHECK_LOCATION, true)
        }

        context.startForegroundService(serviceIntent)
    }
}