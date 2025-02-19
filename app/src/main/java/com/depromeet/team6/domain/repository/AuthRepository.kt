package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.Login

interface AuthRepository {
    suspend fun postLogin(authorization: String, logIn: Login): Result<Auth>

}
