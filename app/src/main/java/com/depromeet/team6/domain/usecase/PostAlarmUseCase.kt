package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.AlarmRepository
import retrofit2.Response
import javax.inject.Inject

class PostAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
) {
    suspend operator fun invoke(lastRouteId: String): Response<Unit> =
        alarmRepository.postAlarm(lastRouteId)
}