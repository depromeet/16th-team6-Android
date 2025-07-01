package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import com.depromeet.team6.presentation.ui.home.HomeContract
import com.depromeet.team6.presentation.util.ErrorToastMessage.MSG_INVALID_POSITION_RE_TRY
import com.depromeet.team6.presentation.util.ErrorToastMessage.MSG_NOT_SERVICE_REGION
import com.depromeet.team6.presentation.util.ErrorToastMessage.MSG_UNKNOWN_NETWORK_ERROR
import javax.inject.Inject

class GetBusStartedUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) : ApiRequestUseCase<GetBusStartedUseCase.Params, Boolean>() {

    data class Params(val lastRouteId: String)
    private lateinit var errorReduce : HomeContract.HomeUiState.() -> HomeContract.HomeUiState

    suspend operator fun invoke(lastRouteId: String): Result<Boolean> =
        invoke(params = Params(lastRouteId = lastRouteId))

    override suspend fun apiCall(params: Params): Result<Boolean> {
        return homeRepository.getBusStarted(lastRouteId = params.lastRouteId)
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            "TRS_012" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = MSG_NOT_SERVICE_REGION, route = Route.Home)
            "TRS_013" -> TODO()
            "TRS_014" -> ErrorControlFailureException.SetUIStateException()
            "TRS_015" -> TODO()
            "TRS_019" -> ErrorControlFailureException.SetUIStateException()
            "LOC_003" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = MSG_INVALID_POSITION_RE_TRY, route = Route.Home)
            "LOC_004" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = MSG_INVALID_POSITION_RE_TRY, route = Route.Home)
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException(toastMessage = MSG_UNKNOWN_NETWORK_ERROR)
            else -> ErrorControlFailureException.ShowToastException(toastMessage = MSG_UNKNOWN_NETWORK_ERROR)
        }
    }
}
