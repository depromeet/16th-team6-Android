package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.repository.LocationsRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
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
            else -> ErrorControlFailureException.ShowToastException("알 수 없음")
        }
    }
}