package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.TimeLeftRemoteDataSource
import com.depromeet.team6.domain.repository.TimeLeftRepository
import javax.inject.Inject

class TimeLeftRepositoryImpl @Inject constructor(
    private val timeLeftRemoteDataSource: TimeLeftRemoteDataSource
) : TimeLeftRepository {
    override suspend fun getDepartureRemainingTime(routeId: String): Result<Int> =
        timeLeftRemoteDataSource.getDepartureRemainingTime(routeId = routeId)
}