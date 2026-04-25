package com.depromeet.team6.data.background.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.depromeet.team6.data.background.AlarmScheduler
import com.depromeet.team6.data.dataremote.datasource.AuthRemoteDataSource
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AddPushAlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRemoteDataSource: AuthRemoteDataSource
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val timeStamp = inputData.getString(KEY_TIME_STAMP) ?: return Result.failure()

        authRemoteDataSource.getUserInfo()
            .onSuccess {
                AlarmScheduler.scheduleAdditionalPushAlarm(applicationContext, timeStamp)
            }
            .onFailure { throwable ->
                Firebase.crashlytics.recordException(
                    RuntimeException("10분전 푸시알림 세팅 실패", throwable)
                )
            }

        return Result.success()
    }

    companion object {
        const val KEY_TIME_STAMP = "timeStamp"
    }
}
