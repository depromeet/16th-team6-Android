package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.HomeRemoteDataSource
import com.depromeet.team6.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeRepoteDataSource: HomeRemoteDataSource
) : HomeRepository {
    override suspend fun getBusStarted(lastRouteId: String): Result<Boolean> =
        homeRepoteDataSource.getBusStarted(lastRouteId = lastRouteId)
}