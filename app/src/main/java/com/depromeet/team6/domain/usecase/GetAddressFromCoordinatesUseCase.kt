package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.repository.LocationsRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAddressFromCoordinatesUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : ApiRequestUseCase<GetAddressFromCoordinatesUseCase.Params, Address>() {

    data class Params(val lat: Double, val lon: Double)

    suspend operator fun invoke(lat: Double, lon: Double): Result<Address> =
        invoke(Params(lat, lon))

    override suspend fun apiCall(params: Params): Result<Address> {
        return locationsRepository.getAddressFromCoordinates(lat = params.lat, lon = params.lon)
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        Timber.d("errorCode : $errorCode")

        return when (errorCode) {
            TOK_001 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            TOK_002 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            USR_002 -> ErrorControlFailureException.NavigateAndShowToastException(
                toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED,
                route = Route.Login
            )
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_NETWORK_FAILURE)
            else -> ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)
        }
    }
}
