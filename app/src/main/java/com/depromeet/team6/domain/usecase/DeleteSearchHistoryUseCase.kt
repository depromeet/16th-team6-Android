package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.repository.LocationsRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject

class DeleteSearchHistoryUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : NetworkRequestUseCase<DeleteSearchHistoryUseCase.Params, Unit>() {

    data class Params(
        val name: String,
        val lat: Double,
        val lon: Double,
        val businessCategory: String,
        val address: String
    )

    suspend operator fun invoke(
        name: String,
        lat: Double,
        lon: Double,
        businessCategory: String,
        address: String
    ): Result<Unit> = invoke(Params(name, lat, lon, businessCategory, address))

    override suspend fun apiCall(params: Params): Result<Unit> {
        return locationsRepository.deleteSearchHistory(
            name = params.name,
            lat = params.lat,
            lon = params.lon,
            businessCategory = params.businessCategory,
            address = params.address
        )
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            TOK_001 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            TOK_002 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            USR_002 -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED, route = Route.Login)
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_NETWORK_FAILURE)
            else -> ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)
        }
    }
}
