package com.depromeet.team6.data.dataremote.model.request.alarm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestAlarmDto(
    @SerialName("lastRouteId")
    val lastRouteId: String
)
