package com.depromeet.team6.data.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.depromeet.team6.R
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetTimeLeftUseCase
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator
import com.depromeet.team6.presentation.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private fun playAlarm() {
        val isSound = userInfoRepositoryImpl.getAlarmSound()

        if (isSound) {
            playAlarmSound()
            vibrate()
        } else {
            vibrate()
        }
    }

    private fun playAlarmSound() {
        try {
            Log.d("LockService", "알림음 재생 시작")

            // TODO : 미디어 볼륨말고 알람볼륨 채널 사용하게 바꿔야해요
            val audioAttr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.alarm_sound)
            mediaPlayer?.apply {
                isLooping = true
                setAudioAttributes(audioAttr)
                setVolume(4.0f, 4.0f)
                start()
            }
        } catch (e: Exception) {
            Log.e("LockService", "알림음 재생 중 오류 발생: ${e.message}", e)
        }
    }

    private fun vibrate() {
        try {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

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
    }

    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }

    override fun onCreate() {
        super.onCreate()
        LockReceiver.initialize(lockScreenNavigator, taxiCostUseCase)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForeground(NOTIFICATION_ID, createForegroundNotification())
    }

    private fun createForegroundNotification(): Notification {
        val channelId = ATCHA_SERVICE_CHANNEL

        val channel = NotificationChannel(
            channelId,
            ATCHA_SERVICE_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_app_logo_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
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

        startLockReceiver()
        wakeLockAcquire()

        CoroutineScope(Dispatchers.IO).launch {
            val taxiCost = taxiCostUseCase.getLastSavedTaxiCost()

            withContext(Dispatchers.Main) {
                playAlarm()

                lockScreenNavigator.navigateToLockScreen(applicationContext, taxiCost)
            }

            delay(ALARM_DURATION_MS)
            withContext(Dispatchers.Main) {
                stopSelf()
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

    private fun wakeLockAcquire() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                WAKE_LOCK_TAG
            )

            // 10초 동안 화면 유지
            wakeLock.acquire(10 * 1000L)
        } catch (e: Exception) {
            Log.e("FCM", "WakeLock error: ${e.message}")
        }
    }
    companion object {
        const val EXTRA_SHOW_LOCK_SCREEN = "extra_show_lock_screen"
        const val EXTRA_CHECK_LOCATION = "extra_check_location"
        const val ACTION_STOP_ALARM_SOUND = "com.depromeet.team6.STOP_ALARM_SOUND"

        const val LOCATION_NOTIFICATION_ID = 1001

        private const val LOCATION_CHANNEL_ID = "ATCHA_LOCATION_CHANNEL"
        private const val LOCATION_CHANNEL_NAME = "ATCHA_LOCATION"

        private const val ATCHA_SERVICE_CHANNEL = "ATCHA_SERVICE_CHANNEL"
        private const val ATCHA_SERVICE_NAME = "ATCHA_SERVICE"

        const val NOTIFICATION_ID = 1
        private const val WAKE_LOCK_TAG = "Atcha:WakeLock"

        const val ALARM_DURATION_MS = 60_000L * 2
    }
}
