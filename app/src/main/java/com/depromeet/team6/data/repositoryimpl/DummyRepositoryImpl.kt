package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.DummyRemoteDataSource
import com.depromeet.team6.data.mapper.toDomainModel
import com.depromeet.team6.domain.model.DummyData
import com.depromeet.team6.domain.repository.DummyRepository
import javax.inject.Inject

class DummyRepositoryImpl @Inject constructor(
    private val remoteDataSource: DummyRemoteDataSource
) : DummyRepository {

    override suspend fun funName(): Result<List<DummyData>> = runCatching {
        remoteDataSource.funName().map {
            it.toDomainModel()
        }
    }
}
