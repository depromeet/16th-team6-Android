package com.depromeet.team6.presentation.ui.lock

import LockRoute
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import com.depromeet.team6.data.datalocal.service.LockService
import com.depromeet.team6.ui.theme.Team6Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockActivity : ComponentActivity() {
    private val viewModel: LockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        }

        val taxiCost = intent.getIntExtra(LockScreenNavigator.EXTRA_TAXI_COST, 0)
        viewModel.setTaxiCost(taxiCost)

        setContent {
            Team6Theme {
                LockRoute(
                    padding = PaddingValues(),
                    viewModel = viewModel,
                    onTimerFinish = {
                        stopLockServiceAndExit(this) }
                )
            }
        }
    }

    private fun stopLockServiceAndExit(context: Context) {
        val stopIntent = Intent(context, LockService::class.java)
        context.stopService(stopIntent) // 서비스 종료
        finish() // 액티비티 종료
    }
}
