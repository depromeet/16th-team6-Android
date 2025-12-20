package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.request.transits.RequestBusArrivalDTO
import com.depromeet.team6.data.dataremote.model.response.base.BaseResponse
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusArrivalsDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusOperationInfoDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusPositionsDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseCourseSearchDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.BUS_ROUTES
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LAST_ROUTE_CONFIG
import com.depromeet.team6.data.dataremote.util.ApiConstraints.ROUTES
import com.depromeet.team6.data.dataremote.util.ApiConstraints.TRANSITS
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface TransitsService {
    @GET
    suspend fun getAvailableCourses(
        @Url url: String = "$API/$ROUTES/$LAST_ROUTE_CONFIG",
        @Query("startLat") startLat: String,
        @Query("startLon") startLon: String,
        @Query("endLat") endLat: String,
        @Query("endLon") endLon: String,
        @Query("sortType") sortType: Int
    ): BaseResponse<List<ResponseCourseSearchDto>>

    @POST("$API/$TRANSITS/bus-arrival")
    suspend fun getBusArrival(
        @Body requestBusArrivalDTO: RequestBusArrivalDTO
    ): BaseResponse<ResponseBusArrivalsDto>

    @GET("$API/$TRANSITS/bus-routes/positions")
    suspend fun getBusPositions(
        @Query("busRouteId") busRouteId: String,
        @Query("routeName") routeName: String,
        @Query("serviceRegion") serviceRegion: String
    ): BaseResponse<ResponseBusPositionsDto>

    @GET("$API/$TRANSITS/$BUS_ROUTES/operation-info")
    suspend fun getBusOperationInfo(
        @Query("busRouteId") busRouteId: String,
        @Query("routeName") routeName: String,
        @Query("serviceRegion") serviceRegion: String
    ): BaseResponse<ResponseBusOperationInfoDto>
}
