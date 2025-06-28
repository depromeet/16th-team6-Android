package com.depromeet.team6.presentation.ui.lock

import LockRoute
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import com.depromeet.team6.data.datalocal.service.LockService
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.LockAmplitude.LOCK
import com.depromeet.team6.presentation.util.LockAmplitude.LOCK_BUTTON
import com.depromeet.team6.presentation.util.LockAmplitude.LOCK_BUTTON_LATER_ROUTE
import com.depromeet.team6.presentation.util.LockAmplitude.LOCK_BUTTON_START
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.ui.theme.Team6Theme
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
                        AmplitudeUtils.trackEventWithProperties(
                            LOCK_BUTTON,
                            mapOf(
                                SCREEN_NAME to LOCK,
                                USER_ID to viewModel.getUserId(),
                                LOCK_BUTTON_START to 1
                            )
                        )
                        lockScreenNavigator.navigateToSpecificScreen(this)
                        finish()
                    },
                    onLateClick = {
                        viewModel.setEvent(LockContract.LockEvent.OnLateClick)

                        AmplitudeUtils.trackEventWithProperties(
                            LOCK_BUTTON,
                            mapOf(
                                SCREEN_NAME to LOCK,
                                USER_ID to viewModel.getUserId(),
                                LOCK_BUTTON_LATER_ROUTE to 1
                            )
                        )

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
