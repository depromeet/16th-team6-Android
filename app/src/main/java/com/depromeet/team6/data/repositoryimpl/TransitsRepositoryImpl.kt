package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.TransitsRemoteDataSource
import com.depromeet.team6.data.mapper.todomain.toDomain
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.BusArrival
import com.depromeet.team6.domain.model.BusOperationInfo
import com.depromeet.team6.domain.model.BusPositions
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.repository.TransitsRepository
import javax.inject.Inject

class TransitsRepositoryImpl @Inject constructor(
    private val transitsRemoteDataSource: TransitsRemoteDataSource
) : TransitsRepository {

    override suspend fun getAvailableCourses(startPosition: Address, endPosition: Address, sortType: Int): Result<List<CourseInfo>> =
        transitsRemoteDataSource.getAvailableCourses(
            startLat = startPosition.lat.toString(),
            startLon = startPosition.lon.toString(),
            endLat = endPosition.lat.toString(),
            endLon = endPosition.lon.toString(),
            sortType = sortType
        ).map {
            it.toDomain()
        }

    override suspend fun getBusArrival(
        routeName: String,
        stationName: String,
        lat: Double,
        lon: Double
    ): Result<BusArrival> =
        transitsRemoteDataSource.getBusArrival(
            routeName = routeName,
            stationName = stationName,
            lat = lat,
            lon = lon
        ).map { it.toDomain() }

    override suspend fun getBusPositions(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ): Result<BusPositions> =
        transitsRemoteDataSource.getBusPositions(
            busRouteId = busRouteId,
            routeName = routeName,
            serviceRegion = serviceRegion
        ).map { it.toDomain() }

    override suspend fun getBusOperationInfo(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ): Result<BusOperationInfo> =
        transitsRemoteDataSource.getBusOperationInfo(
            busRouteId = busRouteId,
            routeName = routeName,
            serviceRegion = serviceRegion
        ).map { it.toDomain() }
}
