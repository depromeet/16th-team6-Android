package com.depromeet.team6.domain.repository

interface VersionRepository {
    suspend fun getVersion(): Result<String>
}
