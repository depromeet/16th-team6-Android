package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.repository.LockRepository
import javax.inject.Inject

class GetTaxiCostUseCase @Inject constructor(
    private val lockRepository: LockRepository
) {
    suspend fun invoke(routeLocation: RouteLocation): Result<Int> =
        lockRepository.getTaxiCost(routeLocation = routeLocation)
}