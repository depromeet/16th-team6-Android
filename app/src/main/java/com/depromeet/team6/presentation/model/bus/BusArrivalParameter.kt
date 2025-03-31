package com.depromeet.team6.presentation.model.bus

import kotlinx.serialization.Serializable

@Serializable
data class BusArrivalParameter(
    val routeName: String,
    val stationName: String,
    val lat: Double,
    val lon: Double
)
