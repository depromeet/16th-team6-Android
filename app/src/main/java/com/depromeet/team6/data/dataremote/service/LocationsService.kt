package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.model.response.locations.ResponseAddressDto
import com.depromeet.team6.data.dataremote.model.response.locations.ResponseLocationsDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LOCATIONS
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationsService {
    @GET("$API/$LOCATIONS")
    suspend fun getLocations(
        @Query("keyword") keyword: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): ApiResponse<List<ResponseLocationsDto>>

    @GET("api/locations/rgeo")
    suspend fun getAddressFromCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): ApiResponse<ResponseAddressDto>
}
