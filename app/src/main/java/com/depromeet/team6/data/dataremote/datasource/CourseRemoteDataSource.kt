package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.response.base.toResult
import com.depromeet.team6.data.dataremote.model.response.course.ResponseCourseSearchDto
import com.depromeet.team6.data.dataremote.service.CourseService
import javax.inject.Inject

class CourseRemoteDataSource @Inject constructor(
    private val courseService: CourseService
) {
    suspend fun getAvailableCourses(
        startLat: String,
        startLon: String,
        endLat: String,
        endLon: String
    ) : Result<List<ResponseCourseSearchDto>> = courseService.getAvailableCourses(startLat, startLon, endLat, endLon).toResult()
}