package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.base.BaseResponse
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import retrofit2.http.GET

interface VersionService {
    @GET("$API/app/version")
    suspend fun getVersion(): BaseResponse<String>
}
