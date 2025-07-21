package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.search.RequestSearchHistoryDto
import com.depromeet.team6.data.dataremote.model.response.base.parse
import com.depromeet.team6.data.dataremote.model.response.locations.ResponseAddressDto
import com.depromeet.team6.data.dataremote.model.response.locations.ResponseLocationsDto
import com.depromeet.team6.data.dataremote.service.LocationsService
import javax.inject.Inject

class LocationsRemoteDataSource @Inject constructor(
    private val locationsService: LocationsService
) {
    suspend fun getLocations(
        keyword: String,
        lat: Double,
        lon: Double
    ): Result<List<ResponseLocationsDto>> =
        locationsService.getLocations(keyword = keyword, lat = lat, lon = lon).parse()

    suspend fun getAddressFromCoordinates(lat: Double, lon: Double): Result<ResponseAddressDto> {
        val response = locationsService.getAddressFromCoordinates(lat = lat, lon = lon)
        return response.parse()

//        locationsService.getAddressFromCoordinates(lat = lat, lon = lon).toResult()
    }

    suspend fun getSearchHistories(lat: Double, lon: Double): Result<List<ResponseLocationsDto>> {
        val response = locationsService.getSearchHistories(lat = lat, lon = lon)
        return response.parse()
    }

    suspend fun postSearchHistories(requestSearchHistoryDto: RequestSearchHistoryDto): Result<Unit> {
        val response = locationsService.postSearchHistories(requestSearchHistoryDto = requestSearchHistoryDto)
        if (response.isSuccessful) return Result.success(Unit)
        else return Result.failure(Exception("Failed to post search history"))
    }

    suspend fun deleteSearchHistory(
        name: String,
        lat: Double,
        lon: Double,
        businessCategory: String,
        address: String
    ): Result<Unit> =
        locationsService.deleteSearchHistory(
            name = name,
            lat = lat,
            lon = lon,
            businessCategory = businessCategory,
            address = address
        ).parse()

    suspend fun deleteAllSearchHistory(): Result<Unit> =
        locationsService.deleteAllSearchHistory().parse()
}
