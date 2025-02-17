package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.DummyResponseDto
import retrofit2.http.POST

interface DummyService {
    @POST("/API")
    suspend fun funName(): List<DummyResponseDto>
}
