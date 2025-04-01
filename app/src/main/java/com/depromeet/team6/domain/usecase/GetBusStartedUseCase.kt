package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.HomeRepository
import javax.inject.Inject

class GetBusStartedUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(lastRouteId: String): Result<Boolean> =
        homeRepository.getBusStarted(lastRouteId = lastRouteId)
}
