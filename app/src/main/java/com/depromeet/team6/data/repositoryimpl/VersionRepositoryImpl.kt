package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.VersionRemoteDataSource
import com.depromeet.team6.domain.repository.VersionRepository
import javax.inject.Inject

class VersionRepositoryImpl @Inject constructor(
    private val versionRemoteDataSource: VersionRemoteDataSource
) : VersionRepository {

    override suspend fun getVersion(): Result<String> = runCatching {
        val a = versionRemoteDataSource.getVersion().toString()
        a
    }
}
