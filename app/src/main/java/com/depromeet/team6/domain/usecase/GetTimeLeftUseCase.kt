package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.TimeLeftRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.util.ErrorToastMessage
import javax.inject.Inject

class GetTimeLeftUseCase @Inject constructor(
    private val timeLeftRepository: TimeLeftRepository
) : ApiRequestUseCase<GetTimeLeftUseCase.Params, Int>() {

    data class Params(val routeId: String)
    suspend operator fun invoke(routeId: String): Result<Int> =
        invoke(Params(routeId = routeId))

    override suspend fun apiCall(params: Params): Result<Int> {
        return timeLeftRepository.getDepartureRemainingTime(routeId = params.routeId)
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            "TRS_013" -> ErrorControlFailureException.SetUIStateException()
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException(toastMessage = ErrorToastMessage.MSG_UNKNOWN_NETWORK_ERROR)
            else -> ErrorControlFailureException.ShowToastException(toastMessage = ErrorToastMessage.MSG_UNKNOWN_NETWORK_ERROR)
        }
    }
}
