package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.ATH_001
import com.depromeet.team6.domain.Auth.ATH_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.LOC_003
import com.depromeet.team6.domain.RequestFormat.LOC_004
import com.depromeet.team6.domain.RequestFormat.REQ_001
import com.depromeet.team6.domain.RequestFormat.REQ_002
import com.depromeet.team6.domain.ToastMessage.API_ERROR_DUPLICATED_TOKEN
import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_CURRENT_LOCATION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_LOCATION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.SignUp
import com.depromeet.team6.domain.repository.AuthRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : NetworkRequestUseCase<SignUp, Auth>() {

    override suspend fun apiCall(params: SignUp): Result<Auth> =
        authRepository.postSignUp(signUp = params)

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException = when (errorCode) {
        REQ_001, REQ_002 ->
            ErrorControlFailureException.ReportDiscordWithToast(API_ERROR_UNKNOWN)

        ATH_001 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED)

        ATH_002 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_DUPLICATED_TOKEN)

        LOC_003 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_INVALID_LOCATION)

        LOC_004 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_INVALID_CURRENT_LOCATION)

        INTERNAL_SERVER_ERROR ->
            ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)

        else -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
    }
}
