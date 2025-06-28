package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.response.base.parse
import com.depromeet.team6.data.dataremote.model.response.base.toResult
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusArrivalsDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusOperationInfoDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusPositionsDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseCourseSearchDto
import com.depromeet.team6.data.dataremote.service.TransitsService
import javax.inject.Inject

class TransitsRemoteDataSource @Inject constructor(
    private val transitsService: TransitsService
) {
    suspend fun getAvailableCourses(
        startLat: String,
        startLon: String,
        endLat: String,
        endLon: String,
        sortType: Int
    ): Result<List<ResponseCourseSearchDto>> {
        val response = transitsService.getAvailableCourses(startLat, startLon, endLat, endLon, sortType)
        return response.parse()
    }

    suspend fun getBusArrival(
        routeName: String,
        stationName: String,
        lat: Double,
        lon: Double
    ): Result<ResponseBusArrivalsDto> = transitsService.getBusArrival(
        routeName = routeName,
        stationName = stationName,
        lat = lat,
        lon = lon
    ).parse()

    suspend fun getBusPositions(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ): Result<ResponseBusPositionsDto> = transitsService.getBusPositions(
        busRouteId = busRouteId,
        routeName = routeName,
        serviceRegion = serviceRegion
    ).parse()

    suspend fun getBusOperationInfo(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ): Result<ResponseBusOperationInfoDto> = transitsService.getBusOperationInfo(
        busRouteId = busRouteId,
        routeName = routeName,
        serviceRegion = serviceRegion
    ).parse()
}
