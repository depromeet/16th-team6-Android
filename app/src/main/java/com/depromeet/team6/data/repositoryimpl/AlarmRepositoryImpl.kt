package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.AlarmRemoteDataSource
import com.depromeet.team6.domain.repository.AlarmRepository
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmRemoteDataSource: AlarmRemoteDataSource
) : AlarmRepository {

    override suspend fun postAlarm(lastRouteId: String): Result<Unit> =
        alarmRemoteDataSource.postAlarm(lastRouteId = lastRouteId)

    override suspend fun deleteAlarm(lastRouteId: String): Result<Unit> =
        alarmRemoteDataSource.deleteAlarm(lastRouteId = lastRouteId)

    override suspend fun refreshAlarm(): Result<String> =
        alarmRemoteDataSource.refreshAlarm().map {
            it.departureTime
        }
}
