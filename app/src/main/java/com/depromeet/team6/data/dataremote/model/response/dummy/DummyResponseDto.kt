package com.depromeet.team6.data.dataremote.model.response.dummy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DummyResponseDto(
    @SerialName("dummy")
    val dummy: String
)
