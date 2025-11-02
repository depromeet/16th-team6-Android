package com.depromeet.team6.data.dataremote.model.request.auth

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RequestWithDrawDto(
    @SerializedName("reason")
    val reason: String
)
