package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RouteMap.TRS_017
import com.depromeet.team6.domain.ToastMessage.API_ERROR_BUS_ARRIVAL_MISSING
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.model.BusOperationInfo
import com.depromeet.team6.domain.repository.TransitsRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBusOperationInfoUseCase @Inject constructor(
    private val transitsRepository: TransitsRepository
) : NetworkRequestUseCase<GetBusOperationInfoUseCase.Params, BusOperationInfo>() {

    data class Params(val busRouteId: String, val routeName: String, val serviceRegion: String)

    suspend operator fun invoke(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ): Result<BusOperationInfo> =
        invoke(Params(busRouteId = busRouteId, routeName = routeName, serviceRegion = serviceRegion))

    override suspend fun apiCall(params: Params): Result<BusOperationInfo> =
        transitsRepository.getBusOperationInfo(
            busRouteId = params.busRouteId,
            routeName = params.routeName,
            serviceRegion = params.serviceRegion
        )

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException = when (errorCode) {
        TRS_017 ->
            ErrorControlFailureException.ShowToastException(API_ERROR_BUS_ARRIVAL_MISSING)

        INTERNAL_SERVER_ERROR ->
            ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)

        else -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
    }
}
