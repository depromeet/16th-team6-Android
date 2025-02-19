package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.ResponseAuthDto
import retrofit2.http.Body
import retrofit2.http.POST
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.VERSION
import com.depromeet.team6.data.dataremote.util.ApiConstraints.USERS
import com.depromeet.team6.data.dataremote.util.ApiConstraints.SIGN_IN
import com.depromeet.team6.data.dataremote.model.request.RequestLoginDto

interface AuthService {
    @POST("$API/$VERSION/$USERS/$SIGN_IN")
    suspend fun postLogin(
        @Body requestLoginDto: RequestLoginDto
    ): ResponseAuthDto
}
