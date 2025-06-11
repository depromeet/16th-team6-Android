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
import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.depromeet.team6.presentation.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForeground(NOTIFICATION_ID, createForegroundNotification())
    }

    private fun createForegroundNotification(): Notification {
        val channelId = ATCHA_SERVICE_CHANNEL

        val channel = NotificationChannel(
            channelId,
            ATCHA_SERVICE_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_app_logo_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
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

        val showLockScreen = intent?.getBooleanExtra(EXTRA_SHOW_LOCK_SCREEN, false) ?: false

        val checkLocation = intent?.getBooleanExtra(EXTRA_CHECK_LOCATION, false) ?: false

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

        if (checkLocation) {
            handleLocationCheckRequest()
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

    private fun handleLocationCheckRequest() {
        if (!hasLocationPermission()) {
            scheduleNextLocationCheckAndStop()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {

                val currentLocation = getCurrentLocation()
                if (currentLocation == null) {
                    scheduleNextLocationCheckAndStop()
                    return@launch
                }

                val homeLatitude = userInfoRepositoryImpl.getUserHome().latitude
                val homeLongitude = userInfoRepositoryImpl.getUserHome().longitude

                val distance = calculateDistance(
                    currentLocation.latitude, currentLocation.longitude,
                    homeLatitude, homeLongitude
                )

                if (distance > 1.0) {
                    showLocationNotification()
                }

                scheduleNextLocationCheckAndStop()

            } catch (e: Exception) {
                scheduleNextLocationCheckAndStop()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundLocationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        return (fineLocationGranted || coarseLocationGranted) && backgroundLocationGranted
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? {
        return try {
            suspendCancellableCoroutine { continuation ->
                fusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        continuation.resume(task.result)
                    } else {
                        continuation.resume(null)
                    }
                }?.addOnFailureListener { exception ->
                    continuation.resume(null)
                }
            }
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return (results[0] / 1000.0)
    }

    private fun showLocationNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            LOCATION_CHANNEL_ID,
            LOCATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, LOCATION_CHANNEL_ID)
            .setContentText(getString(R.string.notification_ten_text))
            .setSmallIcon(R.drawable.ic_app_logo_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(LOCATION_NOTIFICATION_ID, notification)
    }

    private fun scheduleNextLocationCheckAndStop() {
        lockServiceManager.scheduleLocationCheck()
        stopSelf()
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
        const val EXTRA_CHECK_LOCATION = "extra_check_location"
        const val ACTION_STOP_ALARM_SOUND = "com.depromeet.team6.STOP_ALARM_SOUND"

        private const val LOCATION_NOTIFICATION_ID = 1001

        private const val LOCATION_CHANNEL_ID = "ATCHA_LOCATION_CHANNEL"
        private const val LOCATION_CHANNEL_NAME = "ATCHA_LOCATION"

        private const val ATCHA_SERVICE_CHANNEL = "ATCHA_SERVICE_CHANNEL"
        private const val ATCHA_SERVICE_NAME = "ATCHA_SERVICE"

        const val NOTIFICATION_ID = 1
    }
}
