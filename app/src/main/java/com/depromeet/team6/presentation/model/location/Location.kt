package com.depromeet.team6.presentation.model.location

data class Location(
    val name: String,
    val lat: Double,
    val lon: Double,
    val businessCategory: String,
    val address: String,
    val radius: String
)
