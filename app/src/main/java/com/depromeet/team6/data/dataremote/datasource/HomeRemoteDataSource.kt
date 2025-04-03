package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.response.base.toResult
import com.depromeet.team6.data.dataremote.service.HomeService
import javax.inject.Inject

class HomeRemoteDataSource @Inject constructor(
    private val homeService: HomeService
) {
    suspend fun getBusStarted(lastRouteId: String): Result<Boolean> =
        homeService.getBusStarted(lastRouteId = lastRouteId).toResult()
}
