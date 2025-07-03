package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RouteMap.TRS_014
import com.depromeet.team6.domain.RouteMap.TRS_015
import com.depromeet.team6.domain.RouteMap.TRS_019
import com.depromeet.team6.domain.ToastMessage.API_ERROR_BUS_LOCATION_MISSING
import com.depromeet.team6.domain.ToastMessage.API_ERROR_BUS_ROUTE_MISSING
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.model.BusPositions
import com.depromeet.team6.domain.repository.TransitsRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBusPositionsUseCase @Inject constructor(
    private val transitsRepository: TransitsRepository
) : ApiRequestUseCase<GetBusPositionsUseCase.Params, BusPositions>() {

    data class Params(val busRouteId: String, val routeName: String, val serviceRegion: String)

    suspend operator fun invoke(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ): Result<BusPositions> =
        invoke(Params(busRouteId = busRouteId, routeName = routeName, serviceRegion = serviceRegion))

    override suspend fun apiCall(params: Params): Result<BusPositions> =
        transitsRepository.getBusPositions(
            busRouteId = params.busRouteId,
            routeName = params.routeName,
            serviceRegion = params.serviceRegion
        )

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException = when (errorCode) {
        TRS_014 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_BUS_LOCATION_MISSING)

        TRS_015, TRS_019 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_BUS_ROUTE_MISSING)

        INTERNAL_SERVER_ERROR ->
            ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)

        else -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
    }
}
