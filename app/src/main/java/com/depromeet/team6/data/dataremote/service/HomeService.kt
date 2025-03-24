package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.BUS_STARTED
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LAST_ROUTES
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LAST_ROUTE_ID
import com.depromeet.team6.data.dataremote.util.ApiConstraints.TRANSITS
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeService {
    @GET("$API/$TRANSITS/$LAST_ROUTES/{$LAST_ROUTE_ID}/$BUS_STARTED")
    suspend fun getBusStarted(
        @Path(LAST_ROUTE_ID) lastRouteId: String
    ) : ApiResponse<Boolean>
}