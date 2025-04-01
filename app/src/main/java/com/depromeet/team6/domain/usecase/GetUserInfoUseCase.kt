package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.UserInfo
import com.depromeet.team6.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<UserInfo> =
        authRepository.getUserInfo()
}
