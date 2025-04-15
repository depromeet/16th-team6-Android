package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.transits.BusServiceHour
import com.depromeet.team6.data.dataremote.model.response.transits.ResponseBusOperationInfoDto
import com.depromeet.team6.domain.model.BusOperationInfo
import com.depromeet.team6.presentation.util.BusOperationInfo.HOLIDAY
import com.depromeet.team6.presentation.util.BusOperationInfo.HOLIDAY_KR
import com.depromeet.team6.presentation.util.BusOperationInfo.SATURDAY
import com.depromeet.team6.presentation.util.BusOperationInfo.SATURDAY_KR
import com.depromeet.team6.presentation.util.BusOperationInfo.UNKNOWN_KR
import com.depromeet.team6.presentation.util.BusOperationInfo.WEEKDAY
import com.depromeet.team6.presentation.util.BusOperationInfo.WEEKDAY_KR
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun ResponseBusOperationInfoDto.toDomain(): BusOperationInfo {
    return BusOperationInfo(
        startStationName = this.startStationName,
        endStationName = this.endStationName,
        serviceHours = this.serviceHours.map { it.toDomain() }
    )
}

fun BusServiceHour.toDomain(): com.depromeet.team6.domain.model.BusServiceHour {
    return com.depromeet.team6.domain.model.BusServiceHour(
        dailyType = dailyTypeMapper(this.dailyType),
        busDirection = this.busDirection.toString(),
        startTime = timeMapper(this.startTime),
        endTime = timeMapper(this.endTime),
        term = this.term
    )
}

private fun dailyTypeMapper(dailyType: String): String {
    return when (dailyType) {
        WEEKDAY -> WEEKDAY_KR
        SATURDAY -> SATURDAY_KR
        HOLIDAY -> HOLIDAY_KR
        else -> UNKNOWN_KR
    }
}

private fun timeMapper(dateTimeString: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val time = LocalDateTime.parse(dateTimeString, formatter)
        time.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        "잘못된 형식"
    }
}
