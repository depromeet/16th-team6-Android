package com.depromeet.team6.presentation.ui.lock

import LockScreen
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.depromeet.team6.data.datalocal.service.LockService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        }

        setContent {
//            LockScreen(onTimerFinish = {
//                stopLockServiceAndExit(this)
//            })
        }
    }

    private fun stopLockServiceAndExit(context: Context) {
        val stopIntent = Intent(context, LockService::class.java)
        context.stopService(stopIntent) // 서비스 종료
        finish() // 액티비티 종료
    }
}
