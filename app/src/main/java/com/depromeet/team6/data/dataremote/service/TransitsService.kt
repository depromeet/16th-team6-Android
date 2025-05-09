package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusArrivalsDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusOperationInfoDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusPositionsDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseCourseSearchDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.BUS_ROUTES
import com.depromeet.team6.data.dataremote.util.ApiConstraints.TRANSITS
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TransitsService {
    @GET("$API/$TRANSITS/last-routes")
    suspend fun getAvailableCourses(
        @Query("startLat") startLat: String,
        @Query("startLon") startLon: String,
        @Query("endLat") endLat: String,
        @Query("endLon") endLon: String,
        @Query("sortType") sortType: Int
    ): Response<ApiResponse<List<ResponseCourseSearchDto>>>

    @GET("$API/$TRANSITS/bus-arrival")
    suspend fun getBusArrival(
        @Query("routeName") routeName: String,
        @Query("stationName") stationName: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): ApiResponse<ResponseBusArrivalsDto>

    @GET("$API/$TRANSITS/bus-routes/positions")
    suspend fun getBusPositions(
        @Query("busRouteId") busRouteId: String,
        @Query("routeName") routeName: String,
        @Query("serviceRegion") serviceRegion: String
    ): ApiResponse<ResponseBusPositionsDto>

    @GET("$API/$TRANSITS/$BUS_ROUTES/operation-info")
    suspend fun getBusOperationInfo(
        @Query("busRouteId") busRouteId: String,
        @Query("routeName") routeName: String,
        @Query("serviceRegion") serviceRegion: String
    ): ApiResponse<ResponseBusOperationInfoDto>
}
