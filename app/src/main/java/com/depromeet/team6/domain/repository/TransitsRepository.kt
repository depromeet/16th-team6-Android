package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.BusArrival
import com.depromeet.team6.domain.model.course.CourseInfo

interface TransitsRepository {
    suspend fun getAvailableCourses(startPosition: Address, endPosition: Address): Result<List<CourseInfo>>

    suspend fun getBusArrival(routeName: String, stationName: String, lat: Double, lon: Double) : Result<BusArrival>
}
