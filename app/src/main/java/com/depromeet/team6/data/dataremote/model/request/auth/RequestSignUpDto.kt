package com.depromeet.team6.data.dataremote.model.request.auth

import com.depromeet.team6.data.dataremote.util.ApiConstraints.FCM_TOKEN
import com.depromeet.team6.data.dataremote.util.ApiConstraints.PROVIDER
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestSignUpDto(
    @SerialName(PROVIDER)
    val provider: Int,

    @SerialName("address")
    val address: String,

    @SerialName("lat")
    val lat: Double,

    @SerialName("lon")
    val lon: Double,

    @SerialName(FCM_TOKEN)
    val fcmToken: String
)
