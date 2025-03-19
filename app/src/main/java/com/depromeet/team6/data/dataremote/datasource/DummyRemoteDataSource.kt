package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.response.dummy.DummyResponseDto
import com.depromeet.team6.data.dataremote.service.DummyService
import javax.inject.Inject

class DummyRemoteDataSource @Inject constructor(
    private val service: DummyService
) {
    suspend fun funName(): List<DummyResponseDto> = service.funName()
}
