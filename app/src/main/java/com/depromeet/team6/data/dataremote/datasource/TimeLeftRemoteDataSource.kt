package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.response.base.parse
import com.depromeet.team6.data.dataremote.service.TimeLeftService
import javax.inject.Inject

class TimeLeftRemoteDataSource @Inject constructor(
    private val timeLeftService: TimeLeftService
) {
    suspend fun getDepartureRemainingTime(routeId: String): Result<Int> =
        timeLeftService.getDepartureRemainingTime(routeId).parse()
}
