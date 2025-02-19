package com.depromeet.team6.data.dataremote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RequestLoginDto(
    @SerialName("platform")
    val platform: String
)
