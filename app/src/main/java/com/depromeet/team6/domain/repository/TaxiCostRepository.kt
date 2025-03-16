package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.RouteLocation
import kotlinx.coroutines.flow.Flow

interface TaxiCostRepository {
    suspend fun getTaxiCost(routeLocation: RouteLocation): Result<Int>

    fun observeTaxiCost(): Flow<Int>

    suspend fun saveTaxiCost(taxiCost: Int)

    suspend fun getLastSavedTaxiCost(): Int
}