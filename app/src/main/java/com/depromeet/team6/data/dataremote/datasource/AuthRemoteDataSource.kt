package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.signup.RequestSignUpDto
import com.depromeet.team6.data.dataremote.model.request.user.RequestModifyUserInfoDto
import com.depromeet.team6.data.dataremote.model.response.base.parse
import com.depromeet.team6.data.dataremote.model.response.user.ResponseAuthDto
import com.depromeet.team6.data.dataremote.model.response.user.ResponseCheckDto
import com.depromeet.team6.data.dataremote.model.response.user.ResponseUserInfoDto
import com.depromeet.team6.data.dataremote.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authService: AuthService
) {
    suspend fun getCheck(provider: Int): Result<ResponseCheckDto> =
        authService.getCheck(provider = provider).parse()

    suspend fun postSignUp(requestSignUpDto: RequestSignUpDto): Result<ResponseAuthDto> =
        authService.postSignUp(requestSignUpDto = requestSignUpDto).parse()

    suspend fun getLogin(provider: Int, fcmToken: String): Result<ResponseAuthDto> =
        authService.getLogin(provider = provider, fcmToken = fcmToken).parse()

    suspend fun postLogout(): Result<Unit> =
        authService.postLogout().parse()

    suspend fun deleteWithDraw(): Result<Unit> =
        authService.deleteWithDraw().parse()

    suspend fun getUserInfo(): Result<ResponseUserInfoDto> {
        val response = authService.getUserInfo()
        return response.parse()
    }

    suspend fun modifyUserInfo(requestModifyUserInfoDto: RequestModifyUserInfoDto): Result<ResponseUserInfoDto> {
        val response = authService.modifyUserInfo(requestModifyUserInfoDto = requestModifyUserInfoDto)
        return response.parse()
    }
}
