package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.TaxiCostRemoteDataSource
import com.depromeet.team6.data.dataremote.model.request.taxi.RequestTaxiCostDto
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.repository.TaxiCostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaxiCostRepositoryImpl @Inject constructor(
    private val taxiCostRemoteDataSource: TaxiCostRemoteDataSource
) : TaxiCostRepository {

    private val _taxiCost = MutableStateFlow(0)

    override suspend fun getTaxiCost(routeLocation: RouteLocation): Result<Int> =
        taxiCostRemoteDataSource.getTaxiCost(requestTaxiDto = RequestTaxiCostDto(routeLocation.startLat, routeLocation.startLon, routeLocation.endLat, routeLocation.endLon))

    override fun observeTaxiCost(): Flow<Int> = _taxiCost.asStateFlow()

    override suspend fun saveTaxiCost(taxiCost: Int) {
        _taxiCost.value = taxiCost
    }

    override suspend fun getLastSavedTaxiCost(): Int = _taxiCost.value
}
