package com.depromeet.team6.domain.repository

import com.depromeet.team6.data.dataremote.model.response.transits.Station
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.BusArrival
import com.depromeet.team6.domain.model.BusOperationInfo
import com.depromeet.team6.domain.model.BusPositions
import com.depromeet.team6.domain.model.course.CourseInfo
import kotlinx.coroutines.flow.Flow

interface TransitsRepository {
    suspend fun getAvailableCourses(startPosition: Address, endPosition: Address, sortType: Int): Result<List<CourseInfo>>

    suspend fun getAvailableCoursesStream(startPosition: Address, endPosition: Address, sortType: Int): Flow<CourseInfo>

    suspend fun getBusArrival(routeName: String, stationName: String, lat: Double, lon: Double, passingStations: List<Station>): Result<BusArrival>

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
