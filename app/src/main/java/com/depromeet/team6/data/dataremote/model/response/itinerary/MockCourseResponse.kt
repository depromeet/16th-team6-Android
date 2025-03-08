package com.depromeet.team6.data.dataremote.model.response.itinerary

import kotlinx.serialization.Serializable

@Serializable
data class RouteResponse(
    val responseCode: String,
    val result: List<Route>
)

@Serializable
data class Route(
    val routeId: String,
    val departureDateTime: String,
    val totalTime: Int,
    val totalWalkTime: Int,
    val transferCount: Int,
    val totalDistance: Int,
    val pathType: Int,
    val totalWalkDistance: Int,
    val legs: List<Leg>
)

@Serializable
data class Leg(
    val mode: String,
    val sectionTime: Int,
    val distance: Int,
    val start: Location,
    val end: Location,
    val steps: List<Step>? = null,
    val route: String? = null,
    val passStopList: PassStopList? = null,
    val arriveDateTime: String? = null,
    val type: Int? = null,
    val passShape: String? = null
)

@Serializable
data class Location(
    val name: String,
    val lon: Double,
    val lat: Double
)

@Serializable
data class Step(
    val streetName: String,
    val distance: Int,
    val description: String,
    val linestring: String
)

@Serializable
data class PassStopList(
    val stationList: List<Station>
)

@Serializable
data class Station(
    val index: Int,
    val stationName: String,
    val lon: String,
    val lat: String,
    val stationID: String
)