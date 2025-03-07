package com.depromeet.team6.presentation.model.course

data class LastTransportInfo(
    val remainingMinutes: Int,
    val departureHour: Int,
    val departureMinute: Int,
    val boardingHour: Int,
    val boardingMinute: Int,
    val legs: List<LegInfo>
)
