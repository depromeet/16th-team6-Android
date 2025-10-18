package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.LocationsRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRealtimeLocationUseCase @Inject constructor(
    private val locationRepository: LocationsRepository
) {
    operator fun invoke(): Flow<LatLng> = locationRepository.getRealtimeLocation()
}
