package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.AlarmRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
) {
    suspend operator fun invoke(lastRouteId: String): Response<Unit> =
        alarmRepository.deleteAlarm(lastRouteId = lastRouteId)
}
