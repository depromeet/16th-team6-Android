package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.LOC_001
import com.depromeet.team6.domain.RequestFormat.LOC_003
import com.depromeet.team6.domain.RequestFormat.LOC_004
import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_CURRENT_LOCATION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_LOCATION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.repository.LocationsRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocationsUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : ApiRequestUseCase<GetLocationsUseCase.Params, List<Location>>() {

    data class Params(val keyword: String, val lat: Double, val lon: Double)

    suspend operator fun invoke(keyword: String, lat: Double, lon: Double): Result<List<Location>> =
        invoke(Params(keyword = keyword, lat = lat, lon = lon))

    override suspend fun apiCall(params: Params): Result<List<Location>> =
        locationsRepository.getLocations(keyword = params.keyword, lat = params.lat, lon = params.lon)

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException = when (errorCode) {
        LOC_003 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_INVALID_LOCATION)

        LOC_004 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_INVALID_CURRENT_LOCATION)

        LOC_001 ->
            ErrorControlFailureException.ReportDiscordWithToast(API_ERROR_NETWORK_FAILURE)

        INTERNAL_SERVER_ERROR ->
            ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)

        else -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
    }
}
