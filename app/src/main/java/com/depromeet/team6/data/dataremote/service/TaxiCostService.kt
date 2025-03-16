package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.TAXIFARE
import com.depromeet.team6.data.dataremote.util.ApiConstraints.TRANSITS
import retrofit2.http.GET
import retrofit2.http.Query

interface TaxiCostService {
    @GET("$API/$TRANSITS/$TAXIFARE")
    suspend fun getTaxiCost(
        @Query("startLat") startLat: Double,
        @Query("startLon") startLon: Double,
        @Query("endLat") endLat: Double,
        @Query("endLon") endLon: Double
    ) : ApiResponse<Int>
}