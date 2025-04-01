package com.depromeet.team6.domain.repository

interface TimeLeftRepository {
    suspend fun getDepartureRemainingTime(routeId: String): Result<Int>
}
