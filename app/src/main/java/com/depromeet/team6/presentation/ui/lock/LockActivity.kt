package com.depromeet.team6.presentation.ui.lock

import LockRoute
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import com.depromeet.team6.data.datalocal.service.LockService
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.ui.theme.Team6Theme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LockActivity : ComponentActivity() {

    @Inject
    lateinit var lockScreenNavigator: LockScreenNavigator

    private val viewModel: LockViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        }

        val taxiCost = intent.getIntExtra(LockScreenNavigator.EXTRA_TAXI_COST, 0)
        viewModel.setTaxiCost(taxiCost)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        setContent {
            Team6Theme {
                LockRoute(
                    padding = PaddingValues(),
                    viewModel = viewModel,
                    onTimerFinish = {
                        stopLockServiceAndExit(this)
                    },
                    onDepartureClick = {
                        viewModel.setEvent(LockContract.LockEvent.OnDepartureClick)
                        lockScreenNavigator.navigateToSpecificScreen(this)
                        finish()
                    },
                    onLateClick = {
                        viewModel.setEvent(LockContract.LockEvent.OnLateClick)

                        try {
                            val departurePoint = sharedPreferences.getString("departurePoint", "") ?: ""
                            val destinationPoint = sharedPreferences.getString("destinationPoint", "") ?: ""

                            val editor = sharedPreferences.edit()
                            editor.putBoolean("fromLockScreen", true)
                            editor.apply()

                            Timber.d("LockActivity onLateClick: departurePoint=$departurePoint, destinationPoint=$destinationPoint")

                            lockScreenNavigator.navigateToCourseSearch(this, departurePoint, destinationPoint)
                        } catch (e: Exception) {
                            Timber.e(e, "Error in onLateClick, navigating to home")
                            lockScreenNavigator.navigateToSpecificScreen(this)
                        }

                        finish()
                    }
                )
            }
        }
    }

    private fun stopLockServiceAndExit(context: Context) {
        // 알림음을 중지하기 위한 인텐트 추가
        val stopSoundIntent = Intent(context, LockService::class.java).apply {
            action = LockService.ACTION_STOP_ALARM_SOUND
        }
        startService(stopSoundIntent)

        // 서비스 종료
        val stopIntent = Intent(context, LockService::class.java)
        context.stopService(stopIntent)

        finish() // 액티비티 종료
    }
}