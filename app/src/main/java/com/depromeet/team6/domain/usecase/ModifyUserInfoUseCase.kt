package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.dataremote.model.request.user.RequestModifyUserInfoDto
import com.depromeet.team6.data.repositoryimpl.AuthRepositoryImpl
import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.LOC_003
import com.depromeet.team6.domain.RequestFormat.LOC_004
import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_LOCATION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.model.UserInfo
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject

class ModifyUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepositoryImpl
) : NetworkRequestUseCase<ModifyUserInfoUseCase.Params, UserInfo>() {
    data class Params(val modifyUserInfoDto: RequestModifyUserInfoDto)

    suspend operator fun invoke(modifyUserInfoDto: RequestModifyUserInfoDto): Result<UserInfo> =
        authRepository.modifyUserInfo(modifyUserInfoDto = modifyUserInfoDto)

    override suspend fun apiCall(params: Params): Result<UserInfo> {
        return authRepository.modifyUserInfo(
            params.modifyUserInfoDto
        )
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            TOK_001 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED, Route.Login)
            TOK_002 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED, Route.Login)
            USR_002 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED, Route.Login)
            LOC_003 -> ErrorControlFailureException.ShowToastException(API_ERROR_INVALID_LOCATION)
            LOC_004 -> ErrorControlFailureException.ShowToastException(API_ERROR_INVALID_LOCATION)
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
            else -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
        }
    }
}
