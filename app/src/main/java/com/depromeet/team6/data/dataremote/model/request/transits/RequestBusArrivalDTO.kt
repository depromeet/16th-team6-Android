package com.depromeet.team6.data.dataremote.model.request.transits

import com.depromeet.team6.data.dataremote.model.response.transits.Station
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestBusArrivalDTO(
    @SerialName("routeName") val routeName: String,
    @SerialName("stationName") val stationName: String,
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double,
    @SerialName("passStations") val passStations: List<Station>
)
