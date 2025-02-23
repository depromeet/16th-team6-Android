package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.Login
import com.depromeet.team6.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(authorization: String, logIn: Login): Result<Auth> =
        authRepository.postLogin(authorization = authorization, logIn = logIn)
}
