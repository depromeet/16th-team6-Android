package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.search.RequestSearchHistoryDto
import com.depromeet.team6.data.dataremote.model.response.base.toResult
import com.depromeet.team6.data.dataremote.model.response.locations.ResponseAddressDto
import com.depromeet.team6.data.dataremote.model.response.locations.ResponseLocationsDto
import com.depromeet.team6.data.dataremote.service.LocationsService
import retrofit2.Response
import javax.inject.Inject

class LocationsRemoteDataSource @Inject constructor(
    private val locationsService: LocationsService
) {
    suspend fun getLocations(
        keyword: String,
        lat: Double,
        lon: Double
    ): Result<List<ResponseLocationsDto>> =
        locationsService.getLocations(keyword = keyword, lat = lat, lon = lon).toResult()

    suspend fun getAddressFromCoordinates(lat: Double, lon: Double): Result<ResponseAddressDto> =
        locationsService.getAddressFromCoordinates(lat = lat, lon = lon).toResult()

    suspend fun getSearchHistories(lat: Double, lon: Double): Result<List<ResponseLocationsDto>> =
        locationsService.getSearchHistories(lat = lat, lon = lon).toResult()

    suspend fun postSearchHistories(requestSearchHistoryDto: RequestSearchHistoryDto): Response<Unit> =
        locationsService.postSearchHistories(requestSearchHistoryDto = requestSearchHistoryDto)

    suspend fun deleteSearchHistory(name: String, lat: Double, lon: Double, businessCategory: String, address: String): Response<Unit> =
        locationsService.deleteSearchHistory(name = name, lat = lat, lon = lon, businessCategory= businessCategory, address = address)

    suspend fun deleteAllSearchHistory(): Response<Unit> = locationsService.deleteAllSearchHistory()
}
