package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.BusArrival
import com.depromeet.team6.domain.repository.TransitsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBusArrivalUseCase @Inject constructor(
    private val transitsRepository: TransitsRepository
) {
    suspend operator fun invoke(routeName: String, stationName: String, lat: Double, lon: Double): Result<BusArrival> =
        transitsRepository.getBusArrival(
            routeName = routeName,
            stationName = stationName,
            lat = lat,
            lon = lon
        )
}
