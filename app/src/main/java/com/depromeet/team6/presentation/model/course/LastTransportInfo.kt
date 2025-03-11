package com.depromeet.team6.presentation.model.course

data class LastTransportInfo(
    val remainingMinutes: Int,
    val departureTime: String,
    val boardingTime: String,
    val legs: List<LegInfo>
)
