package com.depromeet.team6.data.datalocal.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.StringRes
import com.depromeet.team6.R
import com.depromeet.team6.data.datalocal.builder.SimpleNotificationBuilder
import com.depromeet.team6.data.datalocal.manager.LockServiceManager
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetTimeLeftUseCase
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

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

    private var mediaPlayer: MediaPlayer? = null

    private val handler = Handler(Looper.getMainLooper())
    private var isTimeCheckEnabled = false

    private val timeCheckRunnable = object : Runnable {
        override fun run() {
            checkTimeLeft()
            if (isTimeCheckEnabled) {
                handler.postDelayed(this, 60 * 1000)
            }
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
            stopAlarmSound()
            return START_STICKY
        }

        // 인텐트에서 시간 체크 사용 여부와 직접 잠금화면 표시 여부 확인
        val enableTimeCheck = intent?.getBooleanExtra(EXTRA_ENABLE_TIME_CHECK, false) ?: false
        val showLockScreen = intent?.getBooleanExtra(EXTRA_SHOW_LOCK_SCREEN, false) ?: false
        val routeId = intent?.getStringExtra(EXTRA_ROUTE_ID)

        createNotificationChannel()
        startForeground(SERVICE_ID, createNotificationBuilder())
        startLockReceiver()

        if (enableTimeCheck) {
            startTimeCheck()
        }

        if (showLockScreen) {
            CoroutineScope(Dispatchers.IO).launch {
                val taxiCost = taxiCostUseCase.getLastSavedTaxiCost()

                withContext(Dispatchers.Main) {

                    playAlarmSound()

                    lockScreenNavigator.navigateToLockScreen(applicationContext, taxiCost)
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("LockService", "onDestroy 호출됨")

        stopTimeCheck()
        stopLockReceiver()
        stopAlarmSound()
        lockServiceManager.stop()
        super.onDestroy()
    }

    private fun startTimeCheck() {
        Log.d("LockService", "시간 체크 시작됨")

        isTimeCheckEnabled = true

        handler.postDelayed(timeCheckRunnable, 60 * 1000)
    }

    private fun stopTimeCheck() {
        isTimeCheckEnabled = false
        handler.removeCallbacks(timeCheckRunnable)
    }

    // 남은 시간 확인
    private fun checkTimeLeft() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val routeId = "3aae204e-10b9-405e-b613-2ae391b6d8dc"
                if (routeId.isEmpty()) {
                    Log.d("LockService", "저장된 경로 ID 없음")
                    return@launch
                }

                Log.d("LockService", "남은 시간 확인 중: routeId=$routeId")

                getTimeLeftUseCase.invoke(routeId).onSuccess { timeLeft ->
                    Log.d("LockService", "남은 시간 API 응답 성공: $timeLeft 초")

                    // TODO : API 테스트 후 주석 제거
//                    // 남은 시간이 0초 이하이면 잠금화면 표시
//                    if (timeLeft <= 0) {
//                        Log.d("LockService", "출발 시간이 되었습니다. 잠금화면 표시")
//
//                        val taxiCost = taxiCostUseCase.getLastSavedTaxiCost()
//
//                    // 알림음 재생
//                    withContext(Dispatchers.Main) {
//                        playAlarmSound()
//                    }
//                        withContext(Dispatchers.Main) {
//                            lockScreenNavigator.navigateToLockScreen(applicationContext, taxiCost)
//
//                            stopTimeCheck()
//                        }
//                    }
                }.onFailure { error ->
                    Log.e("LockService", "남은 시간 조회 실패", error)
                }
            } catch (e: Exception) {
                Log.e("LockService", "시간 체크 중 오류 발생", e)
            }
        }
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

    private fun createNotificationChannel() {
        val notificationChannel = SimpleNotificationBuilder.createChannel(
            LOCK_CHANNEL,
            getStringWithContext(R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH,
            getStringWithContext(R.string.lock_screen_description)
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun getStringWithContext(
        @StringRes stringRes: Int
    ): String {
        return applicationContext.getString(stringRes)
    }

    private fun createNotificationBuilder(): Notification {
        return SimpleNotificationBuilder.createBuilder(
            context = this,
            channelId = LOCK_CHANNEL,
            title = getStringWithContext(R.string.app_name),
            text = getStringWithContext(R.string.lock_screen_description),
            icon = R.drawable.ic_launcher_foreground
        )
    }

    companion object {
        const val LOCK_CHANNEL = "LOCK_CHANNEL"
        const val SERVICE_ID: Int = 1

        // 인텐트 extras
        const val EXTRA_ENABLE_TIME_CHECK = "extra_enable_time_check"
        const val EXTRA_ROUTE_ID = "extra_route_id"
        const val EXTRA_SHOW_LOCK_SCREEN = "extra_show_lock_screen"

        const val ACTION_STOP_ALARM_SOUND = "com.depromeet.team6.STOP_ALARM_SOUND"
    }
}
