package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.base.BaseResponse
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.DEPARTUREREMAINING
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LASTROUTES
import com.depromeet.team6.data.dataremote.util.ApiConstraints.ROUTEID
import com.depromeet.team6.data.dataremote.util.ApiConstraints.ROUTES
import retrofit2.http.GET
import retrofit2.http.Path

interface TimeLeftService {
    @GET("$API/$ROUTES/$LASTROUTES/{$ROUTEID}/$DEPARTUREREMAINING")
    suspend fun getDepartureRemainingTime(
        @Path(ROUTEID) routeId: String
    ): BaseResponse<Int>
}
