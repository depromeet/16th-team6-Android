package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RouteMap.TRS_014
import com.depromeet.team6.domain.RouteMap.TRS_015
import com.depromeet.team6.domain.RouteMap.TRS_019
import com.depromeet.team6.domain.ToastMessage.BUS_LOCATION
import com.depromeet.team6.domain.ToastMessage.BUS_ROUTE
import com.depromeet.team6.domain.ToastMessage.NETWORK
import com.depromeet.team6.domain.ToastMessage.UNKNOWN
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
            ErrorControlFailureException.ShowToastException(BUS_LOCATION)

        TRS_015,TRS_019 ->
            ErrorControlFailureException.ShowToastException(BUS_ROUTE)

        INTERNAL_SERVER_ERROR ->
            ErrorControlFailureException.ShowToastException(NETWORK)

        else -> ErrorControlFailureException.ShowToastException(UNKNOWN)
    }
}
