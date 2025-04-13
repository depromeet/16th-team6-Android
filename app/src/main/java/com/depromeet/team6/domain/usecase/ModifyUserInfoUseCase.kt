package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.dataremote.model.request.user.RequestModifyUserInfoDto
import com.depromeet.team6.domain.model.UserInfo
import com.depromeet.team6.domain.repository.AuthRepository
import javax.inject.Inject

class ModifyUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(modifyUserInfoDto: RequestModifyUserInfoDto): Result<UserInfo> =
        authRepository.modifyUserInfo(modifyUserInfoDto = modifyUserInfoDto)
}
