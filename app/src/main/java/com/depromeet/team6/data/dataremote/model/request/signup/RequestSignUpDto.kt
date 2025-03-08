package com.depromeet.team6.data.dataremote.model.request.signup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestSignUpDto(
    @SerialName("provider")
    val provider: Int,

    @SerialName("address")
    val address: String,

    @SerialName("lat")
    val lat: Double,

    @SerialName("lon")
    val lon: Double,

    @SerialName("alertFrequencies")
    val alertFrequencies: Set<Int>
)
