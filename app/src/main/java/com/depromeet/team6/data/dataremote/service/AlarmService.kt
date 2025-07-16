package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.request.alarm.RequestAlarmDto
import com.depromeet.team6.data.dataremote.model.response.base.BaseResponse
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LAST_ROUTE_ID
import com.depromeet.team6.data.dataremote.util.ApiConstraints.NOTIFICATIONS
import com.depromeet.team6.data.dataremote.util.ApiConstraints.ROUTE
import com.depromeet.team6.data.dataremote.util.ApiConstraints.ROUTES
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface AlarmService {
    @POST("$API/$ROUTES/$ROUTE")
    suspend fun postAlarm(
        @Body lastRouteId: RequestAlarmDto
    ): BaseResponse<Unit>

    @DELETE("$API/$ROUTES/$ROUTE")
    suspend fun deleteAlarm(
        @Query(LAST_ROUTE_ID) lastRouteId: String
    ): BaseResponse<Unit>
}
