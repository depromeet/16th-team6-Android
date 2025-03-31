package com.depromeet.team6.domain.model

data class SearchHistory(
    val name: String,
    val lat: Double,
    val lon: Double,
    val businessCategory: String,
    val address: String
)
