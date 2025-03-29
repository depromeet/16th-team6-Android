package com.depromeet.team6.data.dataremote.model.request

data class RequestException(
    val responseCode: String,
    override val message : String
) : Exception()
