package com.depromeet.team6.data.dataremote.model.request.dummy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestDummyDto(
    @SerialName("dummy")
    val dummy: String
)
