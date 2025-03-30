package com.depromeet.team6.data.dataremote.model.request.taxi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTaxiCostDto(
    @SerialName("startLat")
    val startLat: Double,

    @SerialName("startLon")
    val startLon: Double,

    @SerialName("endLat")
    val endLat: Double,

    @SerialName("endLon")
    val endLon: Double
)