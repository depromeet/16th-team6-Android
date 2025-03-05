package com.depromeet.team6.data.dataremote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseCheckDto(
    @SerialName("exists")
    val exists: Boolean
)
