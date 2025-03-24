package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.AuthRemoteDataSource
import com.depromeet.team6.data.mapper.todata.toData
import com.depromeet.team6.data.mapper.todomain.toDomain
import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.SignUp
import com.depromeet.team6.domain.repository.AuthRepository
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun getCheck(authorization: String, provider: Int): Result<Boolean> =
        authRemoteDataSource.getCheck(authorization = authorization, provider = provider)
            .mapCatching { it.toDomain() }

    override suspend fun postSignUp(signUp: SignUp): Result<Auth> =
        authRemoteDataSource.postSignUp(requestSignUpDto = signUp.toData())
            .mapCatching { it.toDomain() }

    override suspend fun getLogin(provider: Int, fcmToken: String): Result<Auth> =
        authRemoteDataSource.getLogin(provider = provider, fcmToken = fcmToken)
            .mapCatching { it.toDomain() }

    override suspend fun postLogout(): Response<Unit> =
        authRemoteDataSource.postLogout()

    override suspend fun deleteWithDraw(): Response<Unit> =
        authRemoteDataSource.deleteWithDraw()
}
