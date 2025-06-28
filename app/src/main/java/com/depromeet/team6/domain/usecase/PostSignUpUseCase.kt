package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.ATH_001
import com.depromeet.team6.domain.Auth.ATH_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.LOC_003
import com.depromeet.team6.domain.RequestFormat.LOC_004
import com.depromeet.team6.domain.RequestFormat.REQ_001
import com.depromeet.team6.domain.RequestFormat.REQ_002
import com.depromeet.team6.domain.ToastMessage.EXIST_USER
import com.depromeet.team6.domain.ToastMessage.LOGIN_DATA_EXPIRED
import com.depromeet.team6.domain.ToastMessage.NETWORK
import com.depromeet.team6.domain.ToastMessage.RANGE_CURRENT_LOCATION
import com.depromeet.team6.domain.ToastMessage.RANGE_LOCATION
import com.depromeet.team6.domain.ToastMessage.UNKNOWN
import com.depromeet.team6.domain.model.Auth
import com.depromeet.team6.domain.model.SignUp
import com.depromeet.team6.domain.repository.AuthRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : ApiRequestUseCase<SignUp, Auth>() {


    override suspend fun apiCall(params: SignUp): Result<Auth> =
        authRepository.postSignUp(signUp = params)

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException = when (errorCode) {
        REQ_001, REQ_002 ->
            ErrorControlFailureException.ReportDiscordWithToast(UNKNOWN)

        ATH_001 ->
            ErrorControlFailureException.ShowToastException(LOGIN_DATA_EXPIRED)

        ATH_002 ->
            ErrorControlFailureException.ShowToastException(EXIST_USER)

        LOC_003 ->
            ErrorControlFailureException.ShowToastException(RANGE_LOCATION)

        LOC_004 ->
            ErrorControlFailureException.ShowToastException(RANGE_CURRENT_LOCATION)

        INTERNAL_SERVER_ERROR ->
            ErrorControlFailureException.ShowToastException(NETWORK)

        else -> ErrorControlFailureException.ShowToastException(UNKNOWN)
    }
}
