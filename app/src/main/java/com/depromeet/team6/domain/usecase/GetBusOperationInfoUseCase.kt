package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.BusOperationInfo
import com.depromeet.team6.domain.repository.TransitsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBusOperationInfoUseCase @Inject constructor(
    private val transitsRepository: TransitsRepository
) {
    suspend operator fun invoke(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ): Result<BusOperationInfo> =
        transitsRepository.getBusOperationInfo(
            busRouteId = busRouteId,
            routeName = routeName,
            serviceRegion = serviceRegion
        )
}
