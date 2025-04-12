package com.depromeet.team6.data.dataremote.model.request.user

import com.depromeet.team6.data.dataremote.util.ApiConstraints.FCM_TOKEN
import com.depromeet.team6.data.dataremote.util.ApiConstraints.PROVIDER
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestModifyUserInfoDto(
    @SerialName("nickname")
    val nickname: String? = null,

    @SerialName("profileImageUrl")
    val profileImageUrl: String? = null,

    @SerialName("address")
    val address: String? = null,

    @SerialName("lat")
    val lat: Double? = null,

    @SerialName("lon")
    val lon: Double? = null,

    @SerialName("alertFrequencies")
    val alertFrequencies: Set<Int>? = null,

    @SerialName(FCM_TOKEN)
    val fcmToken: String? = null
)
