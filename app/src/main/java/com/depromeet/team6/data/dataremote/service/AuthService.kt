package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.request.RequestLoginDto
import com.depromeet.team6.data.dataremote.model.response.ResponseAuthDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LOGIN
import com.depromeet.team6.data.dataremote.util.ApiConstraints.USERS
import com.depromeet.team6.data.dataremote.util.ApiConstraints.VERSION
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("$API/$VERSION/$USERS/$LOGIN")
    suspend fun postLogin(
        @Body requestLoginDto: RequestLoginDto
    ): ResponseAuthDto
}
