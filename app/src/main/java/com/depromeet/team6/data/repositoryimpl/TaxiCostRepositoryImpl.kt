package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.TaxiCostRemoteDataSource
import com.depromeet.team6.data.dataremote.model.request.taxi.RequestTaxiCostDto
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.repository.TaxiCostRepository
import javax.inject.Inject

class TaxiCostRepositoryImpl @Inject constructor(
    private val taxiCostRemoteDataSource: TaxiCostRemoteDataSource
) : TaxiCostRepository {
    override suspend fun getTaxiCost(routeLocation: RouteLocation): Result<Int> =
        taxiCostRemoteDataSource.getTaxiCost(requestTaxiDto = RequestTaxiCostDto(routeLocation.startLat, routeLocation.startLon, routeLocation.endLat, routeLocation.endLon))
}