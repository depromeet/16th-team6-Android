package com.depromeet.team6.presentation.model.course

data class LastTransportInfo(
    val remainingMinutes: Int,
    val departureTime: String,  // 자리에서 출발하는 시간
    val boardingTime: String,   // 탑승하는 시간
    val legs: List<LegInfo>
)
