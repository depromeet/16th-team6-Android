package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.RequestLoginDto
import com.depromeet.team6.data.dataremote.model.response.ResponseAuthDto
import com.depromeet.team6.data.dataremote.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authService: AuthService
) {
    suspend fun postSignIn(authorization: String, requestLoginDto: RequestLoginDto): ResponseAuthDto =
        authService.postLogin(
            requestLoginDto = requestLoginDto
        )
}
