package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.datalocal.datasource.HomeInfoLocalDataSource
import com.depromeet.team6.data.dataremote.datasource.HomeRemoteDataSource
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeRemoteDataSource: HomeRemoteDataSource,
    private val homeInfoLocalDataSource: HomeInfoLocalDataSource
) : HomeRepository {
    override suspend fun getBusStarted(lastRouteId: String): Result<Boolean> =
        homeRemoteDataSource.getBusStarted(lastRouteId = lastRouteId)

    override fun setAlarmRegistered(isRegistered: Boolean) {
        homeInfoLocalDataSource.isAlarmRegistered = isRegistered
    }

    override fun isAlarmRegistered(): Boolean = homeInfoLocalDataSource.isAlarmRegistered

    override fun setLastRouteId(routeId: String) {
        homeInfoLocalDataSource.lastRouteId = routeId
    }

    override fun getLastRouteId(): String = homeInfoLocalDataSource.lastRouteId

    override fun setDeparturePoint(address: Address?) {
        homeInfoLocalDataSource.departurePoint = address
    }

    override fun getDeparturePoint(): Address? = homeInfoLocalDataSource.departurePoint

    override fun setDestinationPoint(destinationPoint: String) {
        homeInfoLocalDataSource.destinationPoint = destinationPoint
    }

    override fun getDestinationPoint(): String = homeInfoLocalDataSource.destinationPoint

    override fun setLastCourseInfo(courseInfo: CourseInfo?) {
        homeInfoLocalDataSource.lastCourseInfo = courseInfo
    }

    override fun getLastCourseInfo(): CourseInfo? = homeInfoLocalDataSource.lastCourseInfo

    override fun setUserDeparture(isDeparted: Boolean) {
        homeInfoLocalDataSource.userDeparture = isDeparted
    }

    override fun isUserDeparted(): Boolean = homeInfoLocalDataSource.userDeparture

    override fun setBusArrivalParameter(parameter: BusArrivalParameter?) {
        homeInfoLocalDataSource.busArrivalParameter = parameter
    }

    override fun getBusArrivalParameter(): BusArrivalParameter? = homeInfoLocalDataSource.busArrivalParameter

    override fun clearAlarmData() {
        homeInfoLocalDataSource.clearAlarmData()
    }

    override fun clearUserDeparture() {
        homeInfoLocalDataSource.clearUserDeparture()
    }
}
