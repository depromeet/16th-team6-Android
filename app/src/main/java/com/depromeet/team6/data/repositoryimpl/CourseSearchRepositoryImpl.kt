package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.CourseRemoteDataSource
import com.depromeet.team6.data.mapper.todomain.toDomain
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.repository.CourseSearchRepository
import javax.inject.Inject

class CourseSearchRepositoryImpl @Inject constructor(
    private val courseRemoteDataSource: CourseRemoteDataSource
) : CourseSearchRepository {

    override suspend fun getAvailableCourses(startPosition: Address, endPosition: Address)
    : Result<List<CourseInfo>> =
        courseRemoteDataSource.getAvailableCourses(
            startLat = startPosition.lat.toString(),
            startLon = startPosition.lon.toString(),
            endLat = endPosition.lat.toString(),
            endLon = endPosition.lon.toString()
        ).map {
            it.toDomain()
        }
}
