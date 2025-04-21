package com.depromeet.team6.data.dataremote.model.response.locations

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ResponseLocationsDto(
    @SerialName("name")
    val name: String,

    @SerialName("lat")
    val lat: Double,

    @SerialName("lon")
    val lon: Double,

    @SerialName("businessCategory")
    val businessCategory: String,

    @SerialName("address")
    val address: String,

    @SerialName("radius")
    val radius: String
)
