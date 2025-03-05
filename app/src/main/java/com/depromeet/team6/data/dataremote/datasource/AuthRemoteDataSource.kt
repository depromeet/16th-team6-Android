package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.RequestSignUpDto
import com.depromeet.team6.data.dataremote.model.response.ResponseAuthDto
import com.depromeet.team6.data.dataremote.model.response.ResponseCheckDto
import com.depromeet.team6.data.dataremote.model.response.toResult
import com.depromeet.team6.data.dataremote.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authService: AuthService
) {
    suspend fun getCheck(authorization: String, provider: Int): Result<ResponseCheckDto> =
        authService.getCheck(provider).toResult()

    suspend fun postSignUp(requestSignUpDto: RequestSignUpDto): Result<ResponseAuthDto> =
        authService.postSignUp(requestSignUpDto).toResult()

    suspend fun getLogin(provider: Int): Result<ResponseAuthDto> =
        authService.getLogin(provider).toResult()

    suspend fun getLogout(): Result<Unit> =
        authService.getLogout().toResult()
}
