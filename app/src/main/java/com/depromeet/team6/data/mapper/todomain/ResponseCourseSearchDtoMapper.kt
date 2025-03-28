package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.course.ResponseCourseSearchDto
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType

fun List<ResponseCourseSearchDto>.toDomain(): List<CourseInfo> = filter { response ->
    true
//        response.legs.first().departureDateTime != null &&
//        response.legs.all { it.passShape != null }      // passShape 이 하나라도 없으면 제거
}.map { response ->
    val legInfo = response.legs.map { leg ->
        LegInfo(
            transportType = when {
                leg.type == null -> TransportType.WALK // 노선코드 없는경우 도보 처리
                leg.mode == "WALK" -> TransportType.WALK
                leg.mode == "BUS" -> TransportType.BUS
                leg.mode == "SUBWAY" -> TransportType.SUBWAY
                else -> TransportType.WALK // fallback
            },
            subTypeIdx = leg.type ?: 0,
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
            passShape = leg.passShape ?: ""
        )
    }

    CourseInfo(
        routeId = response.routeId,
        filterCategory = (response.pathType + 1) % 3, // 1 버스, 2 지하철, 3 전체 ->  0 : 전체,  1: 버스,  2: 지하철
        remainingTime = response.totalTime,
        departureTime = response.departureDateTime,
        boardingTime = response.legs.first().departureDateTime ?: "1999-06-06T00:00:00",
        legs = legInfo
    )
}
