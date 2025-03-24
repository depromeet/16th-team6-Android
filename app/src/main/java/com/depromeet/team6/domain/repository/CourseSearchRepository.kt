package com.depromeet.team6.domain.repository

import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.WayPoint

interface CourseSearchRepository {
    suspend fun getAvailableCourses(startPosition : WayPoint, endPosition : WayPoint) : Result<List<CourseInfo>>
}