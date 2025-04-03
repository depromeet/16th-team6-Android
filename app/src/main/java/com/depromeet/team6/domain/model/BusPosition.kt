package com.depromeet.team6.domain.model

data class BusPositions(
    val busRouteStationList: List<BusRouteStation>,
    val turnPoint: Int,
    val busPositions: List<BusPosition>
)

data class BusRouteStation(
    val busRouteId: String,
    val busRouteName: String,
    val busStationId: String,
    val busStationNumber: String,
    val busStationName: String,
    val busStationLat: Double,
    val busStationLon: Double,
    val order: Int
)

data class BusPosition(
    val vehicleId: String,
    val sectionOrder: Int,
    val vehicleNumber: String,
    val sectionProgress: Double,
    val busCongestion: BusCongestion
)
