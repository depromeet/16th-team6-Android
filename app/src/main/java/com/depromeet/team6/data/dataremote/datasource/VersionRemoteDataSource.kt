package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.response.base.parse
import com.depromeet.team6.data.dataremote.service.VersionService
import javax.inject.Inject

class VersionRemoteDataSource @Inject constructor(
    private val service: VersionService
) {
    suspend fun getVersion(): Result<String> = service.getVersion().parse()
}
