package com.depromeet.team6.domain.model

data class BusArrival(
    val busRouteId: String,
    val routeName: String,
    val serviceRegion: String,
    val busStationId: String,
    val stationName: String,
    val lastTime: String,
    val term: Int,
    val realTimeBusArrival: List<RealTimeBusArrival>
)

data class RealTimeBusArrival(
    val busStatus: BusStatus,
    val remainingTime: Int,
    val busCongestion: BusCongestion,
    val remainingSeats: Int,
    val expectedArrivalTime: String?,
    val vehicleId: String,
    val remainingStations: Int
)

enum class ServiceRegion {
    GYEONGGI,
    SEOUL
}
