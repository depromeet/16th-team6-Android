package com.depromeet.team6.data.dataremote.model.response.user

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ResponseUserInfoDto(
    @SerialName("id")
    val id: Int,
    @SerialName("providerId")
    val providerId: String,
    @SerialName("address")
    val address: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double
)
