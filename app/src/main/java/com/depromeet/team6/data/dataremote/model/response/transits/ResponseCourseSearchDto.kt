package com.depromeet.team6.data.dataremote.model.response.transits

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ResponseCourseSearchDto(
    @SerialName("routeId") val routeId: String,
    @SerialName("departureDateTime") val departureDateTime: String,
    @SerialName("totalTime") val totalTime: Int,
    @SerialName("totalWalkTime") val totalWalkTime: Int,
    @SerialName("transferCount") val transferCount: Int,
    @SerialName("totalDistance") val totalDistance: Float,
    @SerialName("pathType") val pathType: Int,
    @SerialName("legs") val legs: List<Leg>
)

@Keep
@Serializable
data class Leg(
    @SerialName("distance") val distance: Float,
    @SerialName("sectionTime") val sectionTime: Int,
    @SerialName("mode") val mode: String,
    @SerialName("departureDateTime") val departureDateTime: String? = null,
    @SerialName("route") val route: String? = null,
    @SerialName("type") val type: Int? = null,
    @SerialName("service") val service: Int? = null,
    @SerialName("start") val start: Location,
    @SerialName("end") val end: Location,
    @SerialName("passStopList") val passStopList: List<Station>? = null,
    @SerialName("step") val step: List<Step>? = null,
    @SerialName("passShape") val passShape: String? = null
)

@Keep
@Serializable
data class Location(
    @SerialName("name") val name: String,
    @SerialName("lon") val lon: String,
    @SerialName("lat") val lat: String
)

@Keep
@Serializable
data class Step(
    @SerialName("streetName") val streetName: String,
    @SerialName("distance") val distance: Float,
    @SerialName("description") val description: String,
    @SerialName("linestring") val linestring: String
)

@Keep
@Serializable
data class Station(
    @SerialName("index") val index: Int? = null,
    @SerialName("stationId") val stationId: Int? = null,
    @SerialName("stationName") val stationName: String = "",
    @SerialName("lon") val lon: String? = null,
    @SerialName("lat") val lat: String? = null
)
