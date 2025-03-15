package com.depromeet.team6.domain.model

data class SignUp(
    val provider: Int,
    val address: String,
    val lat: Double,
    val lon: Double,
    val alertFrequencies: Set<Int>
)
