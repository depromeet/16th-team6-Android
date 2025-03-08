package com.depromeet.team6.presentation.ui.itinerary

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.model.course.WayPoint
import com.depromeet.team6.ui.theme.defaultTeam6Colors

class LegInfoDummyProvider : PreviewParameterProvider<List<LegInfo>> {
    override val values = sequenceOf(
        listOf(
            LegInfo(
                transportType = TransportType.WALK,
                sectionTime = 7,
                startPoint = WayPoint(
                    name = "화서역",
                    latitude = 127.02479803562213,
                    longitude = 37.504585233865086
                ),
                endPoint = WayPoint(
                    name = "지하철2호선방배역",
                    latitude = 127.02569444444444,
                    longitude = 37.501725
                ),
                routeColor = defaultTeam6Colors.black,
                distance = 10
            ),
            LegInfo(
                transportType = TransportType.BUS,
                sectionTime = 27,
                startPoint = WayPoint(
                    name = "수원 KT위즈파크",
                    latitude = 127.02569444444444,
                    longitude = 37.501725
                ),
                endPoint = WayPoint(
                    name = "사당역 2호선",
                    latitude = 127.03824722222222,
                    longitude = 37.47904722222222
                ),
                routeColor = defaultTeam6Colors.systemGreen,
                distance = 57
            ),
            LegInfo(
                transportType = TransportType.SUBWAY,
                sectionTime = 17,
                startPoint = WayPoint(
                    name = "사당역 2호선",
                    latitude = 127.03824722222222,
                    longitude = 37.47904722222222
                ),
                endPoint = WayPoint(
                    name = "강남역 신분당선",
                    latitude = 127.03747630119366,
                    longitude = 37.479103923078995
                ),
                routeColor = defaultTeam6Colors.primaryRed,
                distance = 37
            ),
            LegInfo(
                transportType = TransportType.WALK,
                sectionTime = 13,
                startPoint = WayPoint(
                    name = "강남역 신분당선",
                    latitude = 0.1,
                    longitude = 0.1
                ),
                endPoint = WayPoint(
                    name = "할리스 커피 강남1호점",
                    latitude = 127.037445,
                    longitude = 37.47924
                ),
                routeColor = defaultTeam6Colors.black,
                distance = 10
            )
        )
    )
}