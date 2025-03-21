package com.depromeet.team6.data.dataremote.model.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAuthDto(
    @SerialName("id")
    val id: Int,
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double
)
