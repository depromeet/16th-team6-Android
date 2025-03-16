package com.depromeet.team6.data.dataremote.model.response.lock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseTaxiCostDto(
    @SerialName("result")
    val result: Int
)