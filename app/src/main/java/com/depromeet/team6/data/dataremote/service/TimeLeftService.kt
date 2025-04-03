package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.DEPARTUREREMAINING
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LASTROUTES
import com.depromeet.team6.data.dataremote.util.ApiConstraints.ROUTEID
import com.depromeet.team6.data.dataremote.util.ApiConstraints.TRANSITS
import retrofit2.http.GET
import retrofit2.http.Path

interface TimeLeftService {
    @GET("$API/$TRANSITS/$LASTROUTES/{$ROUTEID}/$DEPARTUREREMAINING")
    suspend fun getDepartureRemainingTime(
        @Path(ROUTEID) routeId: String
    ): ApiResponse<Int>
}
