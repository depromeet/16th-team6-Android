package com.depromeet.team6.domain.model

data class BusOperationInfo(
    val startStationName: String,
    val endStationName: String,
    val serviceHours: List<BusServiceHour>
)

data class BusServiceHour(
    val dailyType: String,
    val busDirection: BusDirection,
    val startTime: String?,
    val endTime: String?,
    val term: Int
)

enum class BusDirection(val text: String) {
    UP("상행"),
    DOWN("하행")
}
