package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.AuthRemoteDataSource
import com.depromeet.team6.data.mapper.todata.toData
import com.depromeet.team6.data.mapper.todomain.toDomain
import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.Login
import com.depromeet.team6.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun postLogin(authorization: String, logIn: Login): Result<Auth> = runCatching {
        authRemoteDataSource.postSignIn(authorization = authorization, requestLoginDto = logIn.toData()).toDomain()
    }


}
