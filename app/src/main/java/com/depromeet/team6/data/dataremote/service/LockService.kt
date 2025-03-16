package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.request.lock.RequestTaxiCostDto
import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.model.response.lock.ResponseTaxiCostDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.TAXIFARE
import com.depromeet.team6.data.dataremote.util.ApiConstraints.TRANSITS
import retrofit2.http.Body
import retrofit2.http.GET

interface LockService {
    @GET("$API/$TRANSITS/$TAXIFARE")
    suspend fun getTaxiCost(
        @Body requestTaxiCostDto: RequestTaxiCostDto
    ): ApiResponse<ResponseTaxiCostDto>
}