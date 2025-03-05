package com.depromeet.team6.data.dataremote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("responseCode") val responseCode: String,
    @SerialName("timeStamp") val timeStamp: String? = null,
    @SerialName("result") val result: T? = null,
    @SerialName("message") val message: String? = null
)

suspend fun <T> ApiResponse<T>.toResult(): Result<T> =
    when {
        this.result != null -> Result.success(this.result)
        this.message != null -> Result.failure(Exception(this.message))
        else -> Result.failure(Exception("Unknown error occurred"))
    }
