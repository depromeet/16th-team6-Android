package com.depromeet.team6.data.datalocal.manager

import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import com.depromeet.team6.data.datalocal.service.LockService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

abstract class BaseForegroundServiceManager<T : Service>(
    val context: Context,
    val targetClass: Class<T>
) {
    fun start() = synchronized(this) {
        val intent = Intent(context, targetClass)

        if (!context.isServiceRunning(targetClass)) {
            Log.d("", "startForegroundService")
            context.startForegroundService(intent)
        }
    }

    fun stop() = synchronized(this) {
        val intent = Intent(context, targetClass)

        if (context.isServiceRunning(targetClass)) {
            context.stopService(intent)
        }
    }
}

class LockServiceManager @Inject constructor(
    @ApplicationContext val applicationContext: Context
) : BaseForegroundServiceManager<LockService>(
    context = applicationContext,
    targetClass = LockService::class.java
) {

    // 시간 체크 활성화하여 서비스 시작 - 잠금화면 표시 없음
    fun startWithTimeCheck(routeId: String) {
        val intent = Intent(context, targetClass).apply {
            putExtra(LockService.EXTRA_ENABLE_TIME_CHECK, true)
            putExtra(LockService.EXTRA_ROUTE_ID, routeId)
            putExtra(LockService.EXTRA_SHOW_LOCK_SCREEN, false)
        }

        if (!context.isServiceRunning(targetClass)) {
            Log.d("LockServiceManager", "시간 체크와 함께 서비스 시작 (잠금화면 표시 없음)")
            context.startForegroundService(intent)
        } else {
            // 서비스가 이미 실행 중이면 새 인텐트로 업데이트
            context.startService(intent)
        }
    }

    // 잠금화면을 표시하는 서비스 시작
    fun startWithLockScreen() {
        val intent = Intent(context, targetClass).apply {
            putExtra(LockService.EXTRA_SHOW_LOCK_SCREEN, true)
        }

        if (!context.isServiceRunning(targetClass)) {
            Log.d("LockServiceManager", "서비스 시작 (잠금화면 표시)")
            context.startForegroundService(intent)
        } else {
            // 서비스가 이미 실행 중이면 새 인텐트로 업데이트
            context.startService(intent)
        }
    }
}
