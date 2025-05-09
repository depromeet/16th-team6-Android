package com.depromeet.team6.data.dataremote.model.response.transits

import androidx.annotation.Keep
import com.depromeet.team6.domain.model.BusCongestion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ResponseBusPositionsDto(
    @SerialName("busRouteStationList") val busRouteStationList: List<BusRouteStation>,
    @SerialName("turnPoint") val turnPoint: Int? = null,
    @SerialName("busPositions") val busPositions: List<BusPosition>? = emptyList()
)

@Keep
@Serializable
data class BusRouteStation(
    @SerialName("busRouteId") val busRouteId: String,
    @SerialName("busRouteName") val busRouteName: String,
    @SerialName("busStationId") val busStationId: String,
    @SerialName("busStationNumber") val busStationNumber: String,
    @SerialName("busStationName") val busStationName: String,
    @SerialName("busStationLat") val busStationLat: Double,
    @SerialName("busStationLon") val busStationLon: Double,
    @SerialName("order") val order: Int
)

@Keep
@Serializable
data class BusPosition(
    @SerialName("vehicleId") val vehicleId: String,
    @SerialName("sectionOrder") val sectionOrder: Int,
    @SerialName("vehicleNumber") val vehicleNumber: String,
    @SerialName("sectionProgress") val sectionProgress: Double,
    @SerialName("busCongestion") val busCongestion: String? = BusCongestion.UNKNOWN.toString()
)

@Keep
@Serializable
data class ResponseBusOperationInfoDto(
    @SerialName("startStationName") val startStationName: String,
    @SerialName("endStationName") val endStationName: String,
    @SerialName("serviceHours") val serviceHours: List<BusServiceHour>
)

@Keep
@Serializable
data class BusServiceHour(
    @SerialName("dailyType") val dailyType: String,
    @SerialName("busDirection") val busDirection: String? = "",
    @SerialName("startTime") val startTime: String,
    @SerialName("endTime") val endTime: String,
    @SerialName("term") val term: Int
)
