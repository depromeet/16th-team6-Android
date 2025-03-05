package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.SignUp
import com.depromeet.team6.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(signUp: SignUp): Result<Auth> =
        authRepository.postSignUp(signUp = signUp)
}
