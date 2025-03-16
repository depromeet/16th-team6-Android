package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.RouteLocation

interface TaxiCostRepository {
    suspend fun getTaxiCost(routeLocation: RouteLocation): Result<Int>
}