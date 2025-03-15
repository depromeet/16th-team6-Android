package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.signup.RequestSignUpDto
import com.depromeet.team6.data.dataremote.model.response.base.toResult
import com.depromeet.team6.data.dataremote.model.response.user.ResponseAuthDto
import com.depromeet.team6.data.dataremote.model.response.user.ResponseCheckDto
import com.depromeet.team6.data.dataremote.service.AuthService
import retrofit2.Response
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

    suspend fun postLogout(): Response<Unit> =
        authService.postLogout()

    suspend fun deleteWithDraw(): Response<Unit> =
        authService.deleteWithDraw()
}
