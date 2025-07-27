package com.depromeet.team6.data.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LockReceiver : BroadcastReceiver() {
    private lateinit var navigator: LockScreenNavigator
    private lateinit var taxiCostUseCase: GetTaxiCostUseCase

    fun initialize(navigator: LockScreenNavigator, taxiCostUseCase: GetTaxiCostUseCase) {
        LockReceiver.navigator = navigator
        LockReceiver.taxiCostUseCase = taxiCostUseCase
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                if (LockReceiver::navigator.isInitialized) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val taxiCost = taxiCostUseCase.getLastSavedTaxiCost()
                            navigator.navigateToLockScreen(context, taxiCost)
                        } catch (e: Exception) {
                            navigator.navigateToLockScreen(context, 0)
                        }
                    }
                }
            }
            Intent.ACTION_SCREEN_OFF -> {
                vibrate(context)
            }
        }
    }

    private fun vibrate(context: Context) {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            val pattern = longArrayOf(1000, 1000)
            val amplitudes = intArrayOf(255, 0)
            val repeatIndex = 0

            val vibrationEffect = VibrationEffect.createWaveform(
                pattern,
                amplitudes,
                repeatIndex
            )

            // for API level 33 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                vibrator!!.vibrate(
                    vibrationEffect,
                    VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM)
                )
            } else {
                vibrator!!.vibrate(
                    vibrationEffect,
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
                )
            }
        } catch (e: Exception) {
            Log.e("LockService", "진동 중 오류 발생: ${e.message}", e)
        }
    }
}
