package com.depromeet.team6.domain.model

data class Location(
    val name: String,
    val lat: Double,
    val lon: Double,
    val businessCategory: String,
    val address: String,
    val radius: String
)
