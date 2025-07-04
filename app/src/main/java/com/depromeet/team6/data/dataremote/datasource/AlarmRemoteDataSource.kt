package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.alarm.RequestAlarmDto
import com.depromeet.team6.data.dataremote.model.response.base.parse
import com.depromeet.team6.data.dataremote.service.AlarmService
import javax.inject.Inject

class AlarmRemoteDataSource @Inject constructor(
    private val alarmService: AlarmService
) {
    suspend fun postAlarm(lastRouteId: String): Result<Unit> {
        val response = alarmService.postAlarm(lastRouteId = RequestAlarmDto(lastRouteId))
        return response.parse()
    }

    suspend fun deleteAlarm(lastRouteId: String): Result<Unit> {
        val response = alarmService.deleteAlarm(lastRouteId = lastRouteId)
        return response.parse()
    }
}
