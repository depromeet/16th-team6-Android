package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.CourseSearchRepositoryImpl
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.WayPoint
import javax.inject.Inject

class GetCourseSearchResultsUseCase @Inject constructor(
    private val repository: CourseSearchRepositoryImpl
) {
    suspend operator fun invoke(startPoint: WayPoint, endPoint: WayPoint): Result<List<CourseInfo>> {
        val apiResult = repository.getAvailableCourses(
            startPosition = startPoint,
            endPosition = endPoint
        )
        return apiResult
    }
}
