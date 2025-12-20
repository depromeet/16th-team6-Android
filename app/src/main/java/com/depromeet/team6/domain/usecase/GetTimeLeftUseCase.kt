package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.repository.TimeLeftRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject

class GetTimeLeftUseCase @Inject constructor(
    private val timeLeftRepository: TimeLeftRepository
) : NetworkRequestUseCase<GetTimeLeftUseCase.Params, Int>() {

    data class Params(val routeId: String)
    suspend operator fun invoke(routeId: String): Result<Int> =
        invoke(Params(routeId = routeId))

    override suspend fun apiCall(params: Params): Result<Int> {
        return timeLeftRepository.getDepartureRemainingTime(routeId = params.routeId)
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            "TRS_013" -> ErrorControlFailureException.SetUIStateException()
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_UNKNOWN)
            else -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_UNKNOWN)
        }
    }
}
