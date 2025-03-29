package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.RequestException
import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.model.response.base.toResult
import com.depromeet.team6.data.dataremote.model.response.course.ResponseCourseSearchDto
import com.depromeet.team6.data.dataremote.service.CourseService
import com.google.gson.Gson
import javax.inject.Inject

class CourseRemoteDataSource @Inject constructor(
    private val courseService: CourseService
) {
    suspend fun getAvailableCourses(
        startLat: String,
        startLon: String,
        endLat: String,
        endLon: String
    ): Result<List<ResponseCourseSearchDto>> {
        val response = courseService.getAvailableCourses(startLat, startLon, endLat, endLon)
        if (response.isSuccessful) {
            return response.body()?.toResult() ?: Result.failure(IllegalStateException("response is null"))
        } else {
            if (response.code() in 400..499) {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)

                return Result.failure(RequestException(errorResponse.responseCode, errorResponse.message!!))

            } else {
                return Result.failure(IllegalStateException("서버로부터 응답이 없습니다."))
            }
        }
    }
}
