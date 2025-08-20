package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.LOC_003
import com.depromeet.team6.domain.RequestFormat.LOC_004
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.repository.LocationsRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSearchHistoriesUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : NetworkRequestUseCase<GetSearchHistoriesUseCase.Params, List<Location>>() {
    data class Params(val lat: Double, val lon: Double)

    suspend operator fun invoke(lat: Double, lon: Double): Result<List<Location>> =
        invoke(Params(lat, lon))

    override suspend fun apiCall(params: Params): Result<List<Location>> {
        return locationsRepository.getSearchHistories(lat = params.lat, lon = params.lon)
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            TOK_001 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            TOK_002 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            USR_002 -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED, route = Route.Login)
            LOC_003 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            LOC_004 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_NETWORK_FAILURE)
            else -> ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)
        }
    }
}
