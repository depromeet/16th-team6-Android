package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCheckUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(authorization: String, provider: Int): Result<Boolean> =
        authRepository.getCheck(authorization = authorization, provider = provider)
}
