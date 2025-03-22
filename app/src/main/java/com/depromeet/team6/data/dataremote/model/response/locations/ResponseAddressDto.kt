package com.depromeet.team6.data.dataremote.model.response.locations

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAddressDto(
    @SerialName("name")
    val name: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double
//    @SerialName("address")
//    val address: String
)
