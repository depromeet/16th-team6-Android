package com.depromeet.team6.data.datalocal.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator

object LockReceiver : BroadcastReceiver() {
    private lateinit var navigator: LockScreenNavigator

    fun initialize(navigator: LockScreenNavigator) {
        this.navigator = navigator
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                if (::navigator.isInitialized) {
                    navigator.navigateToLockScreen(context)
                }
            }
        }
    }
}
