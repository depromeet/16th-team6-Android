package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.CourseSearchRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import timber.log.Timber
import javax.inject.Inject

class GetCourseSearchResultsUseCase @Inject constructor(
    private val repository: CourseSearchRepositoryImpl
) {
    suspend operator fun invoke(startPoint: Address, endPoint: Address): Result<List<CourseInfo>> {
        val apiResult = repository.getAvailableCourses(
            startPosition = startPoint,
            endPosition = endPoint
        )
        Timber.d("respsonersedfews : $apiResult")
        return apiResult
    }
}
