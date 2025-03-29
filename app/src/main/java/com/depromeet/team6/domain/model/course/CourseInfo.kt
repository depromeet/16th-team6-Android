package com.depromeet.team6.domain.model.course

data class CourseInfo(
    val routeId: String,
    val filterCategory: Int, // 0 : 전체,  1: 버스,  2: 지하철
    val remainingTime: Int, // 남은시간 (초)
    val departureTime: String, // 자리에서 출발하는 시간
    val boardingTime: String, // 탑승하는 시간
    val legs: List<LegInfo>
)
