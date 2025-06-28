package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.BusArrival
import com.depromeet.team6.domain.model.BusOperationInfo
import com.depromeet.team6.domain.model.BusPositions
import com.depromeet.team6.domain.model.course.CourseInfo

interface TransitsRepository {
    suspend fun getAvailableCourses(startPosition: Address, endPosition: Address, sortType: Int): Result<List<CourseInfo>>

    suspend fun getBusArrival(routeName: String, stationName: String, lat: Double, lon: Double): Result<BusArrival>

    suspend fun getBusPositions(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ): Result<BusPositions>

    suspend fun getBusOperationInfo(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ): Result<BusOperationInfo>
}
