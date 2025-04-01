package com.depromeet.team6.domain.repository

interface HomeRepository {
    suspend fun getBusStarted(lastRouteId: String): Result<Boolean>
}
