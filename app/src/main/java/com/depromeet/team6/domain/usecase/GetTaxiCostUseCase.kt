package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.repository.TaxiCostRepository
import javax.inject.Inject

class GetTaxiCostUseCase @Inject constructor(
    private val taxiCostRepository: TaxiCostRepository
) {
    suspend operator fun invoke(routeLocation: RouteLocation): Result<Int> =
        taxiCostRepository.getTaxiCost(routeLocation = routeLocation)
}