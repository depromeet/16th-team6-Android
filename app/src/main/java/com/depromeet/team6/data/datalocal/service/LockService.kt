package com.depromeet.team6.data.datalocal.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.depromeet.team6.R
import com.depromeet.team6.data.datalocal.manager.LockServiceManager
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetTimeLeftUseCase
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LockService : Service() {

    @Inject
    lateinit var lockServiceManager: LockServiceManager

    @Inject
    lateinit var lockScreenNavigator: LockScreenNavigator

    @Inject
    lateinit var taxiCostUseCase: GetTaxiCostUseCase

    @Inject
    lateinit var getTimeLeftUseCase: GetTimeLeftUseCase

    @Inject
    lateinit var userInfoRepositoryImpl: UserInfoRepositoryImpl

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    private var vibrationTimer: CountDownTimer? = null

    private fun playAlarm() {
        val isSound = userInfoRepositoryImpl.getAlarmSound()

        if (isSound) {
            playAlarmSound()
        } else {
            vibrate()
        }
    }

    private fun playAlarmSound() {
        try {
            Log.d("LockService", "알림음 재생 시작")
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.alarm_sound)
            mediaPlayer?.apply {
                isLooping = false
                setVolume(4.0f, 4.0f)
                setOnCompletionListener {
                    Log.d("LockService", "알림음 재생 완료")
                }
                start()
                Log.d("LockService", "알림음 재생 시작됨")
            }
        } catch (e: Exception) {
            Log.e("LockService", "알림음 재생 중 오류 발생: ${e.message}", e)
        }
    }

    private fun vibrate() {
        try {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            val pattern = longArrayOf(0, 1000, 500)
            val repeatIndex = 0

            val vibrationEffect = VibrationEffect.createWaveform(
                pattern,
                repeatIndex
            )
            vibrator?.vibrate(vibrationEffect)

            vibrationTimer?.cancel()
            vibrationTimer = object : CountDownTimer(60000, 60000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    stopVibration()
                }
            }.start()
        } catch (e: Exception) {
            Log.e("LockService", "진동 중 오류 발생: ${e.message}", e)
        }
    }

    private fun stopAlarm() {
        stopAlarmSound()
        stopVibration()
    }

    private fun stopAlarmSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        Log.d("LockService", "알림음 재생 중지")
    }

    private fun stopVibration() {
        vibrationTimer?.cancel()
        vibrationTimer = null
        vibrator?.cancel()
        vibrator = null
        Log.d("LockService", "진동 중지")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("LockService", "onCreate 호출됨")
        LockReceiver.initialize(lockScreenNavigator, taxiCostUseCase)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LockService", "onStartCommand 호출됨")

        if (intent?.action == ACTION_STOP_ALARM_SOUND) {
            stopAlarm()
            return START_STICKY
        }

        val showLockScreen = intent?.getBooleanExtra(EXTRA_SHOW_LOCK_SCREEN, false) ?: false

        startLockReceiver()

        if (showLockScreen) {
            CoroutineScope(Dispatchers.IO).launch {
                val taxiCost = taxiCostUseCase.getLastSavedTaxiCost()

                withContext(Dispatchers.Main) {
                    playAlarm()

                    lockScreenNavigator.navigateToLockScreen(applicationContext, taxiCost)
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("LockService", "onDestroy 호출됨")

        stopLockReceiver()
        stopAlarm()
        vibrationTimer?.cancel()
        vibrationTimer = null
        lockServiceManager.stop()
        super.onDestroy()
    }

    private fun startLockReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(LockReceiver, intentFilter)
    }

    private fun stopLockReceiver() {
        unregisterReceiver(LockReceiver)
    }

    companion object {
        const val EXTRA_SHOW_LOCK_SCREEN = "extra_show_lock_screen"

        const val ACTION_STOP_ALARM_SOUND = "com.depromeet.team6.STOP_ALARM_SOUND"
    }
}
