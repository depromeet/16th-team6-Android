package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.repository.LocationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAddressFromCoordinatesUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<Address> =
        locationsRepository.getAddressFromCoordinates(lat = lat, lon = lon)
}
