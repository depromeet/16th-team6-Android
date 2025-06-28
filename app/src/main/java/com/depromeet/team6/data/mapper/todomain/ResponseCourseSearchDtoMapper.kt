package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.transits.ResponseCourseSearchDto
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.Station
import com.depromeet.team6.domain.model.course.TransportType

fun List<ResponseCourseSearchDto>.toDomain(): List<CourseInfo> = filter { response ->
    true
//        response.legs.first().departureDateTime != null &&
//        response.legs.all { it.passShape != null }      // passShape 이 하나라도 없으면 제거
}.map { response ->
    val legInfo = response.legs.map { leg ->
        val passShape = if (leg.mode == TransportType.WALK.name) {
            // WALK 모드일 경우, step을 이어서 leg.passShape 생성
            if (leg.step == null) {
                ""
            } else {
                leg.step.joinToString(" ") { step ->
                    step.linestring
                }
            }
        } else {
            // WALK 모드가 아닐 경우 기존 값을 그대로 사용합니다.
            leg.passShape
        }
        LegInfo(
            transportType = when {
                leg.type == null -> TransportType.WALK // 노선코드 없는경우 도보 처리
                leg.mode == TransportType.WALK.name -> TransportType.WALK
                leg.mode == TransportType.BUS.name -> TransportType.BUS
                leg.mode == TransportType.SUBWAY.name -> TransportType.SUBWAY
                else -> TransportType.WALK // fallback
            },
            subTypeIdx = leg.type ?: 0,
            departureDateTime = leg.departureDateTime ?: "",
            routeName = leg.route,
            sectionTime = leg.sectionTime,
            distance = leg.distance.toInt(),
            startPoint = Address(
                name = leg.start.name,
                lat = leg.start.lat.toDouble(),
                lon = leg.start.lon.toDouble(),
                address = ""
            ),
            endPoint = Address(
                name = leg.end.name,
                lat = leg.end.lat.toDouble(),
                lon = leg.end.lon.toDouble(),
                address = ""
            ),
            passShape = passShape ?: "",
            passStopList = leg.passStopList?.mapIndexedNotNull { idx, it ->
                if (it.lon == null || it.lat == null) return@mapIndexedNotNull null
                Station(
                    index = idx,
                    stationName = it.stationName,
                    lon = it.lon,
                    lat = it.lat
                )
            } ?: emptyList()
        )
    }

    val boardingDateTime = response.legs.first { it.mode != TransportType.WALK.name }.departureDateTime

    CourseInfo(
        routeId = response.routeId,
        filterCategory = (3 - response.pathType) % 3, // 1 지하철, 2 버스, 3 전체 ->  0 : 전체,  1: 버스,  2: 지하철
        totalTime = response.totalTime,
        departureTime = response.departureDateTime,
        boardingTime = boardingDateTime ?: "1999-06-06T00:00:00",
        legs = legInfo
    )
}
