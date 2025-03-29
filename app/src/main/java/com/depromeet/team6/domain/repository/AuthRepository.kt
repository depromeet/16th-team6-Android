package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.SignUp
import retrofit2.Response

interface AuthRepository {
    suspend fun getCheck(authorization: String, provider: Int): Result<Boolean>

    suspend fun postSignUp(signUp: SignUp): Result<Auth>

    suspend fun getLogin(provider: Int, fcmToken: String): Result<Auth>

    suspend fun postLogout(): Response<Unit>

    suspend fun deleteWithDraw(): Response<Unit>
}
