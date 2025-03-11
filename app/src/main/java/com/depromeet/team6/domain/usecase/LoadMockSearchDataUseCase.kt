package com.depromeet.team6.domain.usecase

import androidx.compose.ui.graphics.Color
import com.depromeet.team6.R
import com.depromeet.team6.data.repositoryimpl.MockSearchDataImpl
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.model.course.WayPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class LoadMockSearchDataUseCase @Inject constructor(
    private val repository: MockSearchDataImpl
) {
    operator fun invoke(): List<LastTransportInfo> {
        val mockData = repository.loadMockData(R.raw.mock_data)!!.result[0]
        val departureDateTime = LocalDateTime
            .parse(mockData.departureDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val legs = mockData.legs.map { leg ->
            LegInfo(
                transportType = when (leg.mode) {
                    "BUS" -> TransportType.BUS
                    "SUBWAY" -> TransportType.SUBWAY
                    // 필요한 경우 다른 교통수단도 추가
                    else -> TransportType.WALK
                },
                subTypeIdx = 0, // 기본값으로 0을 사용, 필요에 따라 변경
                sectionTime = leg.sectionTime / 60,
                distance = leg.distance,
                routeColor = Color(0xFFFF0000), // 예시로 색상 코드, 실제로는 더 구체적인 값 필요
                startPoint = WayPoint("", leg.start.lat, leg.start.lon), // WayPoint는 start의 Location에 맞춰 생성
                endPoint = WayPoint("", leg.end.lat, leg.end.lon), // WayPoint는 end의 Location에 맞춰 생성
                passShape = leg.passShape ?: "" // null인 경우 빈 문자열로 대체
            )
        }

        val result = LastTransportInfo(
            remainingMinutes = mockData.totalTime / 60,
            departureTime = mockData.departureDateTime,
            boardingTime = mockData.departureDateTime,
            legs = legs
        )
        return listOf(result)
    }
}
