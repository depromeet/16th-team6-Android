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
import android.media.AudioManager
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
import androidx.core.content.ContextCompat
import com.depromeet.team6.R
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetTimeLeftUseCase
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator
import com.depromeet.team6.presentation.ui.main.MainActivity
import com.depromeet.team6.presentation.util.LockAmplitude.LOCK_ACTION_TAKEN
import com.depromeet.team6.presentation.util.LockAmplitude.LOCK_ACTION_TAKEN_TIME
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LockService : Service() {
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
    private var originalAlarmVolume: Int? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val timerScope = CoroutineScope(Dispatchers.Default)

    private fun playAlarm() {
        val isSound = userInfoRepositoryImpl.getIsAlarmSound()

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
            val audioAttr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val alarmVolume = userInfoRepositoryImpl.getAlarmVolume()
            originalAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, 0)

            val afd = resources.openRawResourceFd(R.raw.alarm_sound)
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttr)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                isLooping = true
                prepare()
                setVolume(1.0f, 1.0f)
                start()
            }
            afd.close()
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
        // --- 알람이 종료될 때 기존의 알람 채널 볼륨 복구---
        originalAlarmVolume?.let {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, it, 0) // 원래 시스템 볼륨으로 복원
        }
    }

    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }

    override fun onCreate() {
        super.onCreate()
        LockReceiver.initialize(lockScreenNavigator, taxiCostUseCase)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForeground(ALARM_NOTIFICATION_ID, createForegroundNotification())
    }

    private fun createForegroundNotification(): Notification {
        val channelId = ATCHA_SERVICE_CHANNEL_ID

        val channel = NotificationChannel(
            channelId,
            ATCHA_SERVICE_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
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
            .setContentText(
                getString(R.string.notification_content_text)
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LockService", "onStartCommand 호출됨")

        startLockReceiver()
        wakeLockAcquire()

        CoroutineScope(Dispatchers.IO).launch {
            val taxiCost = taxiCostUseCase.getLastSavedTaxiCost()

            withContext(Dispatchers.Main) {
                playAlarm()

                lockScreenNavigator.navigateToLockScreen(applicationContext, taxiCost)
            }
        }

        // 2분 후 알람 종료
        timerScope.launch {
            delay(ALARM_DURATION_MS)
            withContext(Dispatchers.Main) {
                AmplitudeUtils.trackEventWithProperties(
                    LOCK_ACTION_TAKEN,
                    mapOf(
                        LOCK_ACTION_TAKEN to 'N',
                        LOCK_ACTION_TAKEN_TIME to Unit
                    )
                )
                val notificationIntent = Intent(this@LockService, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    this@LockService,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(this@LockService, ATCHA_SERVICE_CHANNEL_ID)
                    .setContentTitle(
                        ContextCompat.getString(
                            this@LockService,
                            R.string.notification_alarm_timeout_title
                        )
                    )
                    .setContentText(
                        ContextCompat.getString(
                            this@LockService,
                            R.string.notification_alarm_timeout_body
                        )
                    )
                    .setSmallIcon(R.drawable.ic_app_logo_foreground)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(ALARM_NOTIFICATION_ID, notification)
                stopForeground(STOP_FOREGROUND_DETACH)
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
        timerScope.cancel()
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
        const val ALARM_NOTIFICATION_ID = 1
        const val ALARM_NOTIFICATION_TIMEOUT_ID = 2

        private const val ATCHA_SERVICE_CHANNEL_ID = "ATCHA_Alarm_Channel"
        private const val ATCHA_SERVICE_NAME = "ATCHA_Alarm"

        private const val WAKE_LOCK_TAG = "Atcha:WakeLock"

        const val ALARM_DURATION_MS = 60_000L * 2
    }
}
