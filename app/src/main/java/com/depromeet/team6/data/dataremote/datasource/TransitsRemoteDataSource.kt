package com.depromeet.team6.data.dataremote.datasource

import com.depromeet.team6.data.dataremote.model.request.transits.RequestBusArrivalDTO
import com.depromeet.team6.data.dataremote.model.response.base.ApiException
import com.depromeet.team6.data.dataremote.model.response.base.parse
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusArrivalsDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusOperationInfoDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusPositionsDto
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseCourseSearchDto
import com.depromeet.team6.data.dataremote.model.response.transits.Station
import com.depromeet.team6.data.dataremote.service.TransitsService
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
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
        val response = transitsService.getAvailableCourses(
            startLat = startLat,
            startLon = startLon,
            endLat = endLat,
            endLon = endLon,
            sortType = sortType
        )
        return response.parse()
    }

    fun getAvailableCoursesV3(
        startLat: String,
        startLon: String,
        endLat: String,
        endLon: String
    ): Flow<ResponseCourseSearchDto> = flow {
        val response = transitsService.getAvailableCoursesV3(
            startLat = startLat,
            startLon = startLon,
            endLat = endLat,
            endLon = endLon
        )

        if (!response.isSuccessful) {
            val errorBodyString = response.errorBody()?.string() ?: ""
            val (errorCode, message) = try {
                val json = JsonParser.parseString(errorBodyString).asJsonObject
                Timber.d("Response body asdrasdr : $json")
                val code = json.get("responseCode")?.asString ?: "GET_COURSES_FAILURE"
                val msg = json.get("message")?.asString ?: "Unknown error"
                code to msg
            } catch (e: Exception) {
                Timber.d("Response body asdrasdr : $e")
                "GET_COURSES_FAILURE" to errorBodyString.ifEmpty { "Unknown error" }
            }
            throw ApiException.ApiRequestFailureException(errorCode, message)
        }

        val responseBody = response.body() ?: run {
            Timber.e("Response body is null")
            return@flow
        }

        val gson = Gson()
        try {
            responseBody.byteStream().bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line?.startsWith("data:") == true) {
                        val jsonString = line.substringAfter("data:").trim()
                        if (jsonString.isNotEmpty()) {
                            val jsonElement = JsonParser.parseString(jsonString).asJsonObject

                            when {
                                jsonElement.has("routeId") -> {
                                    val result = gson.fromJson(jsonElement, ResponseCourseSearchDto::class.java)
                                    emit(result)
                                }

                                else -> {
                                    Timber.e("Unknown streaming data format: $jsonString")
                                    throw ApiException.NetworkFailureException.UnknownFailure("Unknown streaming data format: $jsonString")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getBusArrival(
        routeName: String,
        stationName: String,
        lat: Double,
        lon: Double,
        passStations: List<Station>
    ): Result<ResponseBusArrivalsDto> = transitsService.getBusArrival(
        RequestBusArrivalDTO(
            routeName = routeName,
            stationName = stationName,
            lat = lat,
            lon = lon,
            passStations = passStations
        )
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
