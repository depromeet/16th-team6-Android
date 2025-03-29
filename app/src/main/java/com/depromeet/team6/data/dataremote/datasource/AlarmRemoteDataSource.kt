package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.alarm.RequestAlarmDto
import com.depromeet.team6.data.dataremote.service.AlarmService
import retrofit2.Response
import javax.inject.Inject

class AlarmRemoteDataSource @Inject constructor(
    private val alarmService: AlarmService
) {
    suspend fun postAlarm(lastRouteId: String): Response<Unit> =
        alarmService.postAlarm(lastRouteId = RequestAlarmDto(lastRouteId))
}
