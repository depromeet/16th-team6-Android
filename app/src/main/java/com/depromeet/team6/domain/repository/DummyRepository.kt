package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.DummyData

interface DummyRepository {
    suspend fun funName(): Result<List<DummyData>>
}
