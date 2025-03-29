package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.model.response.course.ResponseCourseSearchDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CourseService {
    @GET("/api/transits/last-routes")
    suspend fun getAvailableCourses(
        @Query("startLat") startLat: String,
        @Query("startLon") startLon: String,
        @Query("endLat") endLat: String,
        @Query("endLon") endLon: String
    ): Response<ApiResponse<List<ResponseCourseSearchDto>>>
}
