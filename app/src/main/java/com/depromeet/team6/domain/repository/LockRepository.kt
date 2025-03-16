package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.RouteLocation

interface LockRepository {
    fun getTaxiCost(routeLocation: RouteLocation): Result<Int>
}