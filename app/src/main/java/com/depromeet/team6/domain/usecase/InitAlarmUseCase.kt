package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.datalocal.datasource.AlarmFiredLocalDataSource
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.repository.HomeRepository
import javax.inject.Inject

class InitAlarmUseCase @Inject constructor(
    private val alarmFiredLocalDataSource: AlarmFiredLocalDataSource,
    private val homeRepository: HomeRepository
) {
    operator fun invoke(
        departureAddress: Address,
        destinationAddress: Address,
        registeredCourseInfo: CourseInfo,
        lastRouteId: String
    ) {
        alarmFiredLocalDataSource.resetAll()
        homeRepository.setDeparturePoint(departureAddress)
        homeRepository.setDestinationPoint(destinationAddress)
        homeRepository.setLastCourseInfo(registeredCourseInfo)
        homeRepository.setLastRouteId(lastRouteId)
        homeRepository.setAlarmRegistered(true)
    }
}
