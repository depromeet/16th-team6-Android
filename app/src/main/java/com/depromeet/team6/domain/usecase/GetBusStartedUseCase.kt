package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_LOCATION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.ToastMessage.API_ERROR_OUT_OF_SERVICE_REGION
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject

class GetBusStartedUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) : ApiRequestUseCase<GetBusStartedUseCase.Params, Boolean>() {

    data class Params(val lastRouteId: String)
    suspend operator fun invoke(lastRouteId: String): Result<Boolean> =
        invoke(params = Params(lastRouteId = lastRouteId))

    override suspend fun apiCall(params: Params): Result<Boolean> {
        return homeRepository.getBusStarted(lastRouteId = params.lastRouteId)
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            "TRS_012" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_OUT_OF_SERVICE_REGION, route = Route.Home)
            "TRS_013" -> TODO()
            "TRS_017" -> TODO()
            "TRS_018" -> TODO()
            "TRS_019" -> TODO()
            "LOC_003" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_INVALID_LOCATION, route = Route.Home)
            "LOC_004" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_INVALID_LOCATION, route = Route.Home)
            "INTERNAL_SERVER_ERROR" -> TODO()
            else -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_NETWORK_FAILURE)
        }
    }
}
