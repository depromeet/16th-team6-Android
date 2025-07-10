package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.request.search.RequestSearchHistoryDto
import com.depromeet.team6.data.dataremote.model.response.base.BaseResponse
import com.depromeet.team6.data.dataremote.model.response.locations.ResponseAddressDto
import com.depromeet.team6.data.dataremote.model.response.locations.ResponseLocationsDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LOCATIONS
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LocationsService {
    @GET("$API/$LOCATIONS")
    suspend fun getLocations(
        @Query("keyword") keyword: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): BaseResponse<List<ResponseLocationsDto>>

    @GET("$API/$LOCATIONS/rgeo")
    suspend fun getAddressFromCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): BaseResponse<ResponseAddressDto>

    @GET("api/locations/histories")
    suspend fun getSearchHistories(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): BaseResponse<List<ResponseLocationsDto>>

    @POST("api/locations/histories")
    suspend fun postSearchHistories(
        @Body requestSearchHistoryDto: RequestSearchHistoryDto
    ): BaseResponse<Unit>

    @DELETE("api/locations/history")
    suspend fun deleteSearchHistory(
        @Query("name") name: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("businessCategory") businessCategory: String,
        @Query("address") address: String
    ): BaseResponse<Unit>

    @DELETE("api/locations/histories")
    suspend fun deleteAllSearchHistory(): BaseResponse<Unit>
}
