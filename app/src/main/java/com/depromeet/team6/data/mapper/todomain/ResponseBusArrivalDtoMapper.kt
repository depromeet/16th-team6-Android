package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.transits.RealTimeBusArrival
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusArrivalsDto
import com.depromeet.team6.domain.model.BusArrival
import com.depromeet.team6.domain.model.BusCongestion
import com.depromeet.team6.domain.model.BusStatus

fun ResponseBusArrivalsDto.toDomain(): BusArrival {
    return BusArrival(
        busRouteId = this.busRouteId,
        routeName = this.routeName,
        serviceRegion = this.serviceRegion,
        busStationId = this.busStationId,
        stationName = this.stationName,
        lastTime = this.lastTime,
        term = this.term,
        realTimeBusArrival = this.realTimeBusArrival.orEmpty().map { it.toDomain() }
    )
}

fun RealTimeBusArrival.toDomain(): com.depromeet.team6.domain.model.RealTimeBusArrival {
    return com.depromeet.team6.domain.model.RealTimeBusArrival(
        busStatus = BusStatus.entries.find { it.name == this.busStatus } ?: BusStatus.END,
        remainingTime = this.remainingTime,
        busCongestion = BusCongestion.entries.find { it.name == this.busCongestion } ?: BusCongestion.UNKNOWN,
        remainingSeats = this.remainingSeats ?: 0,
        expectedArrivalTime = this.expectedArrivalTime,
        vehicleId = this.vehicleId,
        remainingStations = this.remainingStations ?: 0
    )
}
