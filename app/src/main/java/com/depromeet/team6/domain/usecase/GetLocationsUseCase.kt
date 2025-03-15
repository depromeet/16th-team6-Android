package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.repository.LocationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocationsUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) {
    suspend operator fun invoke(keyword: String, lat: Double, lon: Double): Result<List<Location>> =
        locationsRepository.getLocations(keyword = keyword, lat = lat, lon = lon)
}
