package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.REQ_001
import com.depromeet.team6.domain.RequestFormat.REQ_002
import com.depromeet.team6.domain.ToastMessage
import com.depromeet.team6.domain.repository.LocationsRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject

class DeleteAllSearchHistoryUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : NetworkRequestUseCase<Unit, Unit>() {
    suspend operator fun invoke(): Result<Unit> = invoke(Unit)

    override suspend fun apiCall(params: Unit): Result<Unit> =
        locationsRepository.deleteAllSearchHistory()

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            REQ_001, REQ_002 -> ErrorControlFailureException.ReportDiscordWithToast(ToastMessage.API_ERROR_UNKNOWN)
            TOK_001 -> ErrorControlFailureException.ShowToastException(ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED)
            TOK_002 -> ErrorControlFailureException.ShowToastException(ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED)
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(ToastMessage.API_ERROR_NETWORK_FAILURE)
            else -> ErrorControlFailureException.ShowToastException(ToastMessage.API_ERROR_UNKNOWN)
        }
    }
}
