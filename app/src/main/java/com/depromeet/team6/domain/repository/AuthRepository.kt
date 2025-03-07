package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.SignUp

interface AuthRepository {
    suspend fun getCheck(authorization: String, provider: Int): Result<Boolean>
    suspend fun postSignUp(signUp: SignUp): Result<Auth>
    suspend fun getLogin(provider: Int): Result<Auth>
    suspend fun getLogout(): Result<Unit>
    suspend fun deleteWithDraw(): Result<Unit>
}
