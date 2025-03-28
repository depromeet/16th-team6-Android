package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo

interface CourseSearchRepository {
    suspend fun getAvailableCourses(startPosition: Address, endPosition: Address): Result<List<CourseInfo>>
}
