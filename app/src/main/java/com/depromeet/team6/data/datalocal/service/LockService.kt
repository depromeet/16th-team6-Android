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
import com.depromeet.team6.presentation.ui.alarm.NotificationScheduler.Companion.NOTIFICATION_ID
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
        val channelId = "ATCHA_SERVICE_CHANNEL"

        val channel = NotificationChannel(
            channelId,
            "앗차 서비스",
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
            .setContentTitle("앗차 서비스")
            .setContentText("막차 알림 서비스가 실행 중입니다.")
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
        // 위치 권한 체크
        if (!hasLocationPermission()) {
            Log.e("LockService", "위치 권한이 없음")
            scheduleNextLocationCheckAndStop()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("LockService", "위치 확인 시작")

                val currentLocation = getCurrentLocation()
                if (currentLocation == null) {
                    Log.e("LockService", "현재 위치를 가져올 수 없음")
                    scheduleNextLocationCheckAndStop()
                    return@launch
                }

                // 간단한 집 위치 (하드코딩 - 실제로는 사용자 설정값 사용)
                val homeLatitude = 37.5665  // 서울시청 좌표 (예시)
                val homeLongitude = 126.9780

                val distance = calculateDistance(
                    currentLocation.latitude, currentLocation.longitude,
                    homeLatitude, homeLongitude
                )

                Log.d("LockService", "집으로부터 거리: ${distance}km")

                if (distance > 1.0) { // 1km 이상 떨어져 있으면
                    showLocationNotification()
                }

                scheduleNextLocationCheckAndStop()

            } catch (e: Exception) {
                Log.e("LockService", "위치 확인 중 오류: ${e.message}", e)
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

        // Android 10 이상에서 백그라운드 위치 권한 확인
        val backgroundLocationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true  // Android 10 미만에서는 필요 없음
        }

        Log.d("LockService", "FINE_LOCATION: $fineLocationGranted")
        Log.d("LockService", "COARSE_LOCATION: $coarseLocationGranted")
        Log.d("LockService", "BACKGROUND_LOCATION: $backgroundLocationGranted")

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
                        Log.w("LockService", "위치를 가져올 수 없음")
                        continuation.resume(null)
                    }
                }?.addOnFailureListener { exception ->
                    Log.e("LockService", "위치 요청 실패: ${exception.message}")
                    continuation.resume(null)
                }
            }
        } catch (e: SecurityException) {
            Log.e("LockService", "위치 권한 없음: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("LockService", "위치 가져오기 실패: ${e.message}")
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
            "위치 알림",
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
            .setContentTitle("🏠 집에서 멀리 떨어져 있어요") // TODO 알림 내용 바꾸기
            .setContentText("막차 시간을 확인해보세요!")
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
    }
}
