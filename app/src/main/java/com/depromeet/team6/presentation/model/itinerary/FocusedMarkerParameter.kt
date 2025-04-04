package com.depromeet.team6.presentation.model.itinerary

import com.depromeet.team6.domain.model.course.TransportType
import kotlinx.serialization.Serializable

@Serializable
data class FocusedMarkerParameter(
    val lat: Double,
    val lon: Double,
    val transportType: TransportType,
    val subTypeIdx: Int,
    val legIndex: Int
)
