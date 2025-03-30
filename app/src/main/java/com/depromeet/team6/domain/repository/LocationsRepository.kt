package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.model.SearchHistory

interface LocationsRepository {
    suspend fun getLocations(keyword: String, lat: Double, lon: Double): Result<List<Location>>

    suspend fun getAddressFromCoordinates(lat: Double, lon: Double): Result<Address>

    suspend fun getSearchHistories(lat: Double, lon: Double): Result<List<Location>>

    suspend fun postSearchHistories(requestSearchHistoryDto: SearchHistory): Result<Unit>
}
