package com.depromeet.team6.data.dataremote.model.response.base

import kotlinx.serialization.SerialName

data class ApiErrorResponse(
    @SerialName("responseCode") val responseCode: String,
    @SerialName("timeStamp") val timeStamp: String? = null,
    @SerialName("path") val path: String? = null,
    @SerialName("message") val message: String? = null
)