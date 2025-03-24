package com.depromeet.team6.data.dataremote.model.response.course

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseCourseSearchDto(
    @SerialName("routeId") val routeId: String,
    @SerialName("departureDateTime") val departureDateTime: String,
    @SerialName("totalTime") val totalTime: Int,
    @SerialName("totalWalkTime") val totalWalkTime: Int,
    @SerialName("transferCount") val transferCount: Int,
    @SerialName("totalDistance") val totalDistance: Int,
    @SerialName("totalWalkDistance") val totalWalkDistance: Int,
    @SerialName("pathType") val pathType: Int,
    @SerialName("legs") val legs: List<Leg>
)

@Serializable
data class Leg(
    @SerialName("distance") val distance: Int,
    @SerialName("sectionTime") val sectionTime: Int,
    @SerialName("mode") val mode: String,
    @SerialName("departureDateTime") val departureDateTime: String? = null,
    @SerialName("route") val route: String? = null,
    @SerialName("type") val type: Int? = null,
    @SerialName("service") val service: Int? = null,
    @SerialName("start") val start: Location,
    @SerialName("end") val end: Location,
    @SerialName("passStopList") val passStopList: List<Station>? = null,
    @SerialName("step") val step: Step? = null,
    @SerialName("passShape") val passShape: String? = null
)

@Serializable
data class Location(
    @SerialName("name") val name: String,
    @SerialName("lon") val lon: String,
    @SerialName("lat") val lat: String
)

@Serializable
data class Step(
    @SerialName("streetName") val streetName: String,
    @SerialName("distance") val distance: Int,
    @SerialName("description") val description: String,
    @SerialName("linestring") val linestring: String
)

@Serializable
data class Station(
    @SerialName("index") val index: Int,
    @SerialName("stationId") val stationId: Int,
    @SerialName("stationName") val stationName: String,
    @SerialName("lon") val lon: String,
    @SerialName("lat") val lat: String,
)
