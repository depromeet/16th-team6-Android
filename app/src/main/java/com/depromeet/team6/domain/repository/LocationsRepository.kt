package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.model.SearchHistory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationsRepository {
    fun getRealtimeLocation(): Flow<LatLng>
    suspend fun getLocations(keyword: String, lat: Double, lon: Double): Result<List<Location>>

    suspend fun getAddressFromCoordinates(lat: Double, lon: Double): Result<Address>

    suspend fun getSearchHistories(lat: Double, lon: Double): Result<List<Location>>

    suspend fun postSearchHistories(requestSearchHistoryDto: SearchHistory): Result<Unit>

    suspend fun deleteSearchHistory(name: String, lat: Double, lon: Double, businessCategory: String, address: String): Result<Unit>

    suspend fun deleteAllSearchHistory(): Result<Unit>

    suspend fun getIsServiceRegion(lat: Double, lon: Double): Result<Boolean>
}
