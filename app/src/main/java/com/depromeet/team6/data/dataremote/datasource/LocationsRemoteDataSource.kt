package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.response.base.toResult
import com.depromeet.team6.data.dataremote.model.response.locations.ResponseLocationsDto
import com.depromeet.team6.data.dataremote.service.LocationsService
import javax.inject.Inject

class LocationsRemoteDataSource @Inject constructor(
    private val locationsService: LocationsService
) {
    suspend fun getLocations(keyword: String, lat: Double, lon: Double): Result<List<ResponseLocationsDto>> =
        locationsService.getLocations(keyword = keyword, lat = lat, lon = lon).toResult()
}
