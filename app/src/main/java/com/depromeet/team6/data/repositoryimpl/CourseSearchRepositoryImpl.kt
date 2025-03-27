package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.CourseRemoteDataSource
import com.depromeet.team6.data.mapper.todomain.toDomain
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.WayPoint
import com.depromeet.team6.domain.repository.CourseSearchRepository
import javax.inject.Inject

class CourseSearchRepositoryImpl @Inject constructor(
    private val courseRemoteDataSource: CourseRemoteDataSource
) : CourseSearchRepository {

    override suspend fun getAvailableCourses(startPosition: WayPoint, endPosition: WayPoint)
    : Result<List<CourseInfo>> =
        courseRemoteDataSource.getAvailableCourses(
            startLat = startPosition.latitude.toString(),
            startLon = startPosition.longitude.toString(),
            endLat = endPosition.latitude.toString(),
            endLon = endPosition.longitude.toString()
        ).map {
            it.toDomain()
        }
}
