package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.AuthRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteWithDrawUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Response<Unit> =
        authRepository.deleteWithDraw()
}
