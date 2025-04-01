package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.taxi.RequestTaxiCostDto
import com.depromeet.team6.data.dataremote.model.response.base.toResult
import com.depromeet.team6.data.dataremote.service.TaxiCostService
import javax.inject.Inject

class TaxiCostRemoteDataSource @Inject constructor(
    private val lockService: TaxiCostService
) {
    suspend fun getTaxiCost(requestTaxiDto: RequestTaxiCostDto): Result<Int> =
        lockService.getTaxiCost(
            startLat = requestTaxiDto.startLat,
            startLon = requestTaxiDto.startLon,
            endLat = requestTaxiDto.endLat,
            endLon = requestTaxiDto.endLon
        ).toResult()
}
