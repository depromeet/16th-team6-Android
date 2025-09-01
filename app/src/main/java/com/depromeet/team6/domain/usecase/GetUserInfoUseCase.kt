package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.AuthRepositoryImpl
import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.model.GetUserInfo
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepositoryImpl
) : NetworkRequestUseCase<Unit, GetUserInfo>() {

    suspend operator fun invoke(): Result<GetUserInfo> =
        invoke(Unit)

    override suspend fun apiCall(params: Unit): Result<GetUserInfo> {
        return authRepository.getUserInfo()
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            TOK_001 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED, Route.Login)
            TOK_002 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED, Route.Login)
            USR_002 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED, Route.Login)
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
            else -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
        }
    }
}
