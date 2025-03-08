package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Location

interface LocationsRepository {
    suspend fun getLocations(keyword: String, lat: Double, lon: Double): Result<List<Location>>
}
