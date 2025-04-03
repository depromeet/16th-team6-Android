package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.request.alarm.RequestAlarmDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LAST_ROUTE_ID
import com.depromeet.team6.data.dataremote.util.ApiConstraints.NOTIFICATIONS
import com.depromeet.team6.data.dataremote.util.ApiConstraints.ROUTE
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface AlarmService {
    @POST("$API/$NOTIFICATIONS/$ROUTE")
    suspend fun postAlarm(
        @Body lastRouteId: RequestAlarmDto
    ): Response<Unit>

    @DELETE("$API/$NOTIFICATIONS/$ROUTE")
    suspend fun deleteAlarm(
        @Query(LAST_ROUTE_ID) lastRouteId: String
    ): Response<Unit>
}
