package com.depromeet.team6.data.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.depromeet.team6.data.datalocal.datasource.AlarmFiredLocalDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var alarmRepository: AlarmFiredLocalDataSource

    override fun onReceive(context: Context?, intent: Intent?) {
        val timeMin = intent!!.getIntExtra("alarmTime", 0)
        when (timeMin) {
            5 -> {
                if (alarmRepository.push5fired) return
                alarmRepository.push5fired = true
            }
            10 -> {
                if (alarmRepository.push10fired) return
                alarmRepository.push10fired = true
            }
            15 -> {
                if (alarmRepository.push15fired) return
                alarmRepository.push15fired = true
            }
            30 -> {
                if (alarmRepository.push30fired) return
                alarmRepository.push30fired = true
            }
            60 -> {
                if (alarmRepository.push60fired) return
                alarmRepository.push60fired = true
            }
        }
        notificationHelper.sendAlarmAwareNotification(timeMin)
    }
}
