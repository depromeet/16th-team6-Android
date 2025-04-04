package com.depromeet.team6.data.dataremote.model.response.transits

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ResponseBusArrivalsDto(
    @SerialName("busRouteId") val busRouteId: String,
    @SerialName("routeName") val routeName: String,
    @SerialName("serviceRegion") val serviceRegion: String,
    @SerialName("busStationId") val busStationId: String,
    @SerialName("stationName") val stationName: String,
    @SerialName("lastTime") val lastTime: String,
    @SerialName("term") val term: Int,
    @SerialName("realTimeBusArrival") val realTimeBusArrival: List<RealTimeBusArrival>? = emptyList()
)

@Keep
@Serializable
data class RealTimeBusArrival(
    @SerialName("busStatus") val busStatus: String,
    @SerialName("remainingTime") val remainingTime: Int,
    @SerialName("busCongestion") val busCongestion: String,
    @SerialName("remainingSeats") val remainingSeats: Int,
    @SerialName("expectedArrivalTime") val expectedArrivalTime: String? = null,
    @SerialName("vehicleId") val vehicleId: String,
    @SerialName("remainingStations") val remainingStations: Int? = 0
)
