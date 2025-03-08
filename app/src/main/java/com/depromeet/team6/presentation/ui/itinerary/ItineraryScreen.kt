package com.depromeet.team6.presentation.ui.itinerary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.model.course.WayPoint
import com.depromeet.team6.presentation.ui.common.AtchaCommonBottomSheet
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryDetail
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryMap
import com.depromeet.team6.presentation.ui.itinerary.component.ItinerarySummary
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng

@Composable
fun ItineraryScreen(
    legs: List<LegInfo>,
    modifier: Modifier = Modifier
) {
    // TODO : 나중에 없애야함
    val tempTimeToLeave = "2025-03-07 22:52:00"
    val tempTotalSec = 4294
    val dummyLegs = listOf(
        LegInfo(
            transportType = TransportType.WALK,
            sectionTime = 7,
            startPoint = WayPoint(
                name = "화서역",
                longitude = 127.02479803562213,
                latitude = 37.504585233865086
            ),
            endPoint = WayPoint(
                name = "지하철2호선방배역",
                longitude = 127.02569444444444,
                latitude = 37.501725
            ),
            routeColor = defaultTeam6Colors.black,
            distance = 10
        ),
        LegInfo(
            transportType = TransportType.BUS,
            sectionTime = 27,
            startPoint = WayPoint(
                name = "수원 KT위즈파크",
                longitude = 127.02569444444444,
                latitude = 37.501725
            ),
            endPoint = WayPoint(
                name = "사당역 2호선",
                longitude = 127.03824722222222,
                latitude = 37.47904722222222
            ),
            routeColor = defaultTeam6Colors.systemGreen,
            distance = 57
        ),
        LegInfo(
            transportType = TransportType.SUBWAY,
            sectionTime = 17,
            startPoint = WayPoint(
                name = "사당역 2호선",
                longitude = 127.03824722222222,
                latitude = 37.47904722222222
            ),
            endPoint = WayPoint(
                name = "강남역 신분당선",
                longitude = 127.03747630119366,
                latitude = 37.479103923078995
            ),
            routeColor = defaultTeam6Colors.primaryRed,
            distance = 37
        ),
        LegInfo(
            transportType = TransportType.WALK,
            sectionTime = 13,
            startPoint = WayPoint(
                name = "강남역 신분당선",
                longitude = 0.1,
                latitude = 0.1
            ),
            endPoint = WayPoint(
                name = "할리스 커피 강남1호점",
                longitude = 127.037445,
                latitude = 37.47924
            ),
            routeColor = defaultTeam6Colors.black,
            distance = 10
        )
    )

    AtchaCommonBottomSheet(
        mainContent = {
            ItineraryMap(
                legs = dummyLegs,
                currentLocation = LatLng(37.5665, 126.9780)
            )
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                ItinerarySummary(
                    totalTimeSec = tempTotalSec,
                    timeToLeave = tempTimeToLeave,
                    legs = legs
                )
                ItineraryDetail(
                    legs = legs
                )
            }
        }
    )
}

@Preview
@Composable
fun ItineraryScreenPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItineraryScreen(
        legs = legs
    )
}