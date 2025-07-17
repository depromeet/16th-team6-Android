package com.depromeet.team6.data.dataremote.model.response.alarm

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ResponseAlarmRefreshDTO(
    @SerialName("departureTime") val departureTime: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("lastRouteId") val lastRouteId: String
)
