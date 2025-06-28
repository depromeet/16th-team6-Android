package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.TransitsRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import javax.inject.Inject

class GetCourseSearchResultsUseCase @Inject constructor(
    private val repository: TransitsRepositoryImpl
) {
    suspend operator fun invoke(startPoint: Address, endPoint: Address, sortType: Int): Result<List<CourseInfo>> {
        val apiResult = repository.getAvailableCourses(
            startPosition = startPoint,
            endPosition = endPoint,
            sortType = sortType
        )
        return apiResult
    }
}
