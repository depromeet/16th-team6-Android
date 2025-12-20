package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.request.alarm.RequestAlarmDto
import com.depromeet.team6.data.dataremote.model.response.alarm.ResponseAlarmRefreshDTO
import com.depromeet.team6.data.dataremote.model.response.base.BaseResponse
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LAST_ROUTE_ID
import com.depromeet.team6.data.dataremote.util.ApiConstraints.ROUTES
import com.depromeet.team6.data.dataremote.util.ApiConstraints.USER_ROUTE
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AlarmService {
    @POST("$API/$ROUTES/$USER_ROUTE")
    suspend fun postAlarm(
        @Body lastRouteId: RequestAlarmDto
    ): BaseResponse<Unit>

    @DELETE("$API/$ROUTES/$USER_ROUTE")
    suspend fun deleteAlarm(
        @Query(LAST_ROUTE_ID) lastRouteId: String
    ): BaseResponse<Unit>

    @GET("$API/$ROUTES/$USER_ROUTE/refresh")
    suspend fun refreshAlarm(): BaseResponse<ResponseAlarmRefreshDTO>
}
