package com.depromeet.team6.domain.repository

interface AlarmRepository {
    suspend fun postAlarm(lastRouteId: String): Result<Unit>

    suspend fun deleteAlarm(lastRouteId: String): Result<Unit>

    suspend fun refreshAlarm(): Result<String>
}
