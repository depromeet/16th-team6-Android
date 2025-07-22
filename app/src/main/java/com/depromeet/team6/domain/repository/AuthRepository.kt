package com.depromeet.team6.domain.repository

import com.depromeet.team6.data.dataremote.model.request.user.RequestModifyUserInfoDto
import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.GetUserInfo
import com.depromeet.team6.domain.model.SignUp
import com.depromeet.team6.domain.model.UserInfo

interface AuthRepository {
    suspend fun getCheck(authorization: String, provider: Int): Result<Boolean>

    suspend fun postSignUp(signUp: SignUp): Result<Auth>

    suspend fun getLogin(provider: Int, fcmToken: String): Result<Auth>

    suspend fun postLogout(): Result<Unit>

    suspend fun deleteWithDraw(): Result<Unit>

    suspend fun getUserInfo(): Result<GetUserInfo>

    suspend fun modifyUserInfo(modifyUserInfoDto: RequestModifyUserInfoDto): Result<UserInfo>
}
