package com.depromeet.team6.domain.repository

import retrofit2.Response

interface AlarmRepository {
    suspend fun postAlarm(lastRouteId: String): Response<Unit>
}