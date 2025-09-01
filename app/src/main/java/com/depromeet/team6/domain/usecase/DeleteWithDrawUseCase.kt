package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.REQ_001
import com.depromeet.team6.domain.RequestFormat.REQ_002
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.repository.AuthRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteWithDrawUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : NetworkRequestUseCase<Unit, Unit>() {

    suspend operator fun invoke(): Result<Unit> = invoke(Unit)

    override suspend fun apiCall(params: Unit): Result<Unit> =
        authRepository.deleteWithDraw()

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException = when (errorCode) {
        REQ_001, REQ_002 ->
            ErrorControlFailureException.ReportDiscordWithToast(API_ERROR_UNKNOWN)

        TOK_001 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED)

        TOK_002 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED)

        USR_002 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED)

        INTERNAL_SERVER_ERROR ->
            ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)

        else -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
    }
}
