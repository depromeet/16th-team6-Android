package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter

interface HomeRepository {
    suspend fun getBusStarted(lastRouteId: String): Result<Boolean>

    fun setAlarmRegistered(isRegistered: Boolean)
    fun isAlarmRegistered(): Boolean

    fun setLastRouteId(routeId: String)
    fun getLastRouteId(): String

    fun setDeparturePoint(address: Address?)
    fun getDeparturePoint(): Address?

    fun setDestinationPoint(destinationPoint: Address?)
    fun getDestinationPoint(): Address?

    fun setLastCourseInfo(courseInfo: CourseInfo?)
    fun getLastCourseInfo(): CourseInfo?

    fun setUserDeparture(isDeparted: Boolean)
    fun isUserDeparted(): Boolean

    fun setBusArrivalParameter(parameter: BusArrivalParameter?)
    fun getBusArrivalParameter(): BusArrivalParameter?

    fun clearAlarmData()
    fun clearUserDeparture()
}
