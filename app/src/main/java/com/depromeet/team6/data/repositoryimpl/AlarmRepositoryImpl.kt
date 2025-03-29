package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.AlarmRemoteDataSource
import com.depromeet.team6.domain.repository.AlarmRepository
import retrofit2.Response
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmRemoteDataSource: AlarmRemoteDataSource
) : AlarmRepository {

    override suspend fun postAlarm(lastRouteId: String): Response<Unit> =
        alarmRemoteDataSource.postAlarm(lastRouteId = lastRouteId)
}