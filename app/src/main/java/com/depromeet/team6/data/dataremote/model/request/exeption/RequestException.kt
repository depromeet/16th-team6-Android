package com.depromeet.team6.data.dataremote.model.request.exeption

data class RequestException(
    val responseCode: String,
    override val message: String
) : Exception()
