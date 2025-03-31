package com.depromeet.team6.domain.model

data class BusArrival(
    val busRouteId: String,
    val routeName: String,
    val serviceRegion: String,
    val busStationId: String,
    val stationName: String,
    val lastTime: String, // 시간 파싱 필요시 LocalDateTime 등으로 변경 가능
    val term: Int,
    val realTimeBusArrival: List<RealTimeBusArrival>
)

data class RealTimeBusArrival(
    val busStatus: BusStatus,
    val remainingTime: Int,
    val busCongestion: BusCongestion,
    val remainingSeats: Int,
    val expectedArrivalTime: String?, // 필요 시 LocalDateTime?
    val vehicleId: String,
    val remainingStations: Int
)