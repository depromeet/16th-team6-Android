package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusPositionsDto
import com.depromeet.team6.domain.model.BusCongestion
import com.depromeet.team6.domain.model.BusPositions

fun ResponseBusPositionsDto.toDomain(): BusPositions {
    return BusPositions(
        busRouteStationList = this.busRouteStationList.map { it.toDomain() },
        turnPoint = this.turnPoint ?: (busRouteStationList.size+1),
        busPositions = this.busPositions.orEmpty().map { it.toDomain() }
    )
}

fun com.depromeet.team6.data.dataremote.model.response.transits.BusRouteStation.toDomain():
    com.depromeet.team6.domain.model.BusRouteStation {
    return com.depromeet.team6.domain.model.BusRouteStation(
        busRouteId = busRouteId,
        busRouteName = busRouteName,
        busStationId = busStationId,
        busStationNumber = busStationNumber,
        busStationName = busStationName,
        busStationLat = busStationLat,
        busStationLon = busStationLon,
        order = order
    )
}

fun com.depromeet.team6.data.dataremote.model.response.transits.BusPosition.toDomain():
    com.depromeet.team6.domain.model.BusPosition {
    return com.depromeet.team6.domain.model.BusPosition(
        vehicleId = vehicleId,
        sectionOrder = sectionOrder,
        vehicleNumber = vehicleNumber,
        sectionProgress = sectionProgress,
        busCongestion = BusCongestion.valueOf(busCongestion.toString())
    )
}
