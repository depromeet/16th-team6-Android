package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.LOC_003
import com.depromeet.team6.domain.RequestFormat.LOC_004
import com.depromeet.team6.domain.RouteMap.TRS_012
import com.depromeet.team6.domain.RouteMap.TRS_016
import com.depromeet.team6.domain.RouteMap.TRS_017
import com.depromeet.team6.domain.RouteMap.TRS_018
import com.depromeet.team6.domain.RouteMap.TRS_019
import com.depromeet.team6.domain.ToastMessage.BUS_ARRIVAL_INCORRECT
import com.depromeet.team6.domain.ToastMessage.BUS_LOCATION
import com.depromeet.team6.domain.ToastMessage.BUS_ROUTE
import com.depromeet.team6.domain.ToastMessage.NETWORK
import com.depromeet.team6.domain.ToastMessage.OUT_OF_RANGE
import com.depromeet.team6.domain.ToastMessage.RANGE_CURRENT_LOCATION
import com.depromeet.team6.domain.ToastMessage.RANGE_LOCATION
import com.depromeet.team6.domain.ToastMessage.UNKNOWN
import com.depromeet.team6.domain.model.BusArrival
import com.depromeet.team6.domain.repository.TransitsRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBusArrivalUseCase @Inject constructor(
    private val transitsRepository: TransitsRepository
) : ApiRequestUseCase<GetBusArrivalUseCase.Params, BusArrival>() {

    data class Params(val routeName: String, val stationName: String, val lat: Double, val lon: Double)

    suspend operator fun invoke(routeName: String, stationName: String, lat: Double, lon: Double): Result<BusArrival> =
        invoke(Params(routeName = routeName, stationName = stationName, lat = lat, lon = lon))

    override suspend fun apiCall(params: Params): Result<BusArrival> =
        transitsRepository.getBusArrival(
            routeName = params.routeName,
            stationName = params.stationName,
            lat = params.lat,
            lon = params.lon
        )

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException = when (errorCode) {
        LOC_003 ->
            ErrorControlFailureException.ShowToastException(RANGE_LOCATION)

        LOC_004 ->
            ErrorControlFailureException.ShowToastException(RANGE_CURRENT_LOCATION)

        TRS_012 ->
            ErrorControlFailureException.NavigateAndShowToastException(OUT_OF_RANGE, Route.Home)

        TRS_016, TRS_017 ->
            ErrorControlFailureException.ShowToastException(BUS_ARRIVAL_INCORRECT)

        TRS_018, TRS_019 ->
            ErrorControlFailureException.ShowToastException(BUS_ROUTE)

        INTERNAL_SERVER_ERROR ->
            ErrorControlFailureException.ShowToastException(NETWORK)

        else -> ErrorControlFailureException.ShowToastException(UNKNOWN)
    }
}