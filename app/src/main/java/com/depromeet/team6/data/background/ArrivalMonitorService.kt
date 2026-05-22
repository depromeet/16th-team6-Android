package com.depromeet.team6.data.background

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArrivalMonitorService : Service() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var destinationLatLng: LatLng? = null
    private var hasArrived = false
    private var expectedArrivalDeadline: LocalDateTime? = null
    private var vibrator: Vibrator? = null
    private val timerScope = CoroutineScope(Dispatchers.Default)
    private val gson = Gson()

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    // Foreground Notification 생성
    private fun createForegroundNotification(): Notification {
        val channelId = ATCHA_ARRIVAL_MONITOR_CHANNEL_ID
        val channel = NotificationChannel(
            channelId,
            ATCHA_ARRIVAL_MONITOR_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(
                getString(R.string.notification_arrival_monitor_start_text)
            )
            .setSmallIcon(R.drawable.ic_atcha_logo)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            checkAutoFinishByExpectedArrival()
            val current = result.lastLocation ?: return
            checkArrival(current)
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        startForeground(ARRIVAL_NOTIFICATION_ID, createForegroundNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val lat = intent?.getDoubleExtra(EXTRA_DEST_LAT, 0.0) ?: 0.0
        val lng = intent?.getDoubleExtra(EXTRA_DEST_LNG, 0.0) ?: 0.0
        destinationLatLng = LatLng(lat, lng)
        expectedArrivalDeadline = getExpectedArrivalDeadline()

        startLocationTracking()
        return START_STICKY
    }

    private fun startLocationTracking() {
        if (checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 10_000L).build()
        fusedLocationClient?.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun checkArrival(current: Location) {
        val dest = destinationLatLng ?: return
        if (hasArrived) return

        val results = FloatArray(1)
        Location.distanceBetween(
            current.latitude,
            current.longitude,
            dest.latitude,
            dest.longitude,
            results
        )

        if (results[0] <= 30f) {
            hasArrived = true
            onArrived(isTimeout = false)
        }
    }

    private fun checkAutoFinishByExpectedArrival() {
        if (hasArrived) return
        val deadline = expectedArrivalDeadline ?: return
        if (LocalDateTime.now().isAfter(deadline)) {
            hasArrived = true
            onArrived(isTimeout = true)
        }
    }

    private fun onArrived(isTimeout: Boolean) {
        // 진동 2회
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 500, 300, 500), -1)
        vibrator?.vibrate(effect)

        val finishChannel = NotificationChannel(
            ARRIVAL_FINISH_CHANNEL_ID,
            ARRIVAL_FINISH_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(finishChannel)

        // 푸시 알림
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, ARRIVAL_FINISH_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_atcha_logo)
            .setContentTitle(
                getString(R.string.notification_arrival_guide_finish_title)
            )
            .setContentText(
                if (isTimeout) {
                    getString(R.string.notification_arrival_expected_time_over_text)
                } else {
                    getString(R.string.notification_arrival_near_destination_text)
                }
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .build()

        notificationManager.notify(ARRIVAL_NOTIFICATION_ID, notification)
        clearArrivalGuideState()

        val intent = Intent(
            if (isTimeout) {
                ACTION_ARRIVAL_TIMEOUT
            } else {
                ACTION_ARRIVAL
            }
        ).apply {
            `package` = packageName
        }
        sendBroadcast(intent) // MainActivity 에서 수신

        stopSelf()
    }

    private fun clearArrivalGuideState() {
        val sharedPreferences = getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .remove(KEY_DEPARTURE_POINT)
            .remove(KEY_LAST_COURSE_INFO)
            .remove(KEY_LAST_ROUTE_ID)
            .remove(KEY_ALARM_REGISTERED)
            .remove(KEY_USER_DEPARTURE)
            .remove(KEY_BUS_ARRIVAL_PARAMETER)
            .apply()

        AlarmScheduler.unScheduleAllAlarms(this)
    }

    private fun getExpectedArrivalDeadline(): LocalDateTime? {
        return runCatching {
            val sharedPreferences = getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
            val courseInfoJson = sharedPreferences.getString(KEY_LAST_COURSE_INFO, "") ?: return null
            if (courseInfoJson.isBlank()) return null

            val courseInfo = gson.fromJson(courseInfoJson, CourseInfo::class.java) ?: return null
            LocalDateTime.parse(courseInfo.departureTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .plusSeconds(courseInfo.totalTime.toLong())
                .plusMinutes(AUTO_END_AFTER_ARRIVAL_MINUTES)
        }.getOrNull()
    }

    override fun onDestroy() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        timerScope.cancel()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    companion object {
        const val ARRIVAL_NOTIFICATION_ID = 3
        const val ATCHA_ARRIVAL_MONITOR_CHANNEL_ID = "Arrival_Monitor_Channel"
        const val ATCHA_ARRIVAL_MONITOR_CHANNEL_NAME = "Arrival Monitor Service"
        const val ARRIVAL_FINISH_CHANNEL_ID = "Arrival_Finish_Channel"
        const val ARRIVAL_FINISH_CHANNEL_NAME = "Arrival Guide Finish"
        const val ACTION_ARRIVAL = "arrival_event"
        const val ACTION_ARRIVAL_TIMEOUT = "arrival_timeout_event"
        const val EXTRA_DEST_LAT = "dest_lat"
        const val EXTRA_DEST_LNG = "dest_lng"
        private const val AUTO_END_AFTER_ARRIVAL_MINUTES = 10L
        private const val PREFERENCE_FILE_NAME = "MyPreferences"
        private const val KEY_ALARM_REGISTERED = "alarmRegistered"
        private const val KEY_LAST_ROUTE_ID = "lastRouteId"
        private const val KEY_DEPARTURE_POINT = "departurePoint"
        private const val KEY_LAST_COURSE_INFO = "lastCourseInfo"
        private const val KEY_USER_DEPARTURE = "userDeparture"
        private const val KEY_BUS_ARRIVAL_PARAMETER = "busArrivalParameter"
    }
}
