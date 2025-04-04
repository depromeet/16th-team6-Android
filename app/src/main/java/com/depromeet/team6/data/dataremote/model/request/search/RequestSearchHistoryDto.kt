package com.depromeet.team6.data.dataremote.model.request.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestSearchHistoryDto(
    @SerialName("name")
    val name: String,

    @SerialName("lat")
    val lat: Double,

    @SerialName("lon")
    val lon: Double,

    @SerialName("businessCategory")
    val businessCategory: String,

    @SerialName("address")
    val address: String
)
