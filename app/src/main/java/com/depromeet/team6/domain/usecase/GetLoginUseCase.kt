package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(provider: Int, fcmToken: String): Result<Auth> =
        authRepository.getLogin(provider = provider, fcmToken = fcmToken)
}
