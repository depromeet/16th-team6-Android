package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.model.SearchHistory
import retrofit2.Response

interface LocationsRepository {
    suspend fun getLocations(keyword: String, lat: Double, lon: Double): Result<List<Location>>

    suspend fun getAddressFromCoordinates(lat: Double, lon: Double): Result<Address>

    suspend fun getSearchHistories(lat: Double, lon: Double): Result<List<Location>>

    suspend fun postSearchHistories(requestSearchHistoryDto: SearchHistory): Response<Unit>

    suspend fun deleteSearchHistory(name: String, lat: Double, lon: Double, businessCategory: String, address: String): Response<Unit>

    suspend fun deleteAllSearchHistory(): Response<Unit>
}
