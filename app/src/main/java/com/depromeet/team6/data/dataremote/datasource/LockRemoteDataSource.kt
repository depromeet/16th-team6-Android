package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.lock.RequestTaxiCostDto
import com.depromeet.team6.data.dataremote.model.response.base.toResult
import com.depromeet.team6.data.dataremote.model.response.lock.ResponseTaxiCostDto
import com.depromeet.team6.data.dataremote.service.LockService
import javax.inject.Inject

class LockRemoteDataSource @Inject constructor(
    private val lockService: LockService
){
    suspend fun getTaxiCost(requestTaxiDto: RequestTaxiCostDto): Result<ResponseTaxiCostDto> =
        lockService.getTaxiCost(requestTaxiDto).toResult()
}