package com.depromeet.team6.presentation.ui.itinerary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.model.course.WayPoint
import com.depromeet.team6.presentation.ui.common.AtchaCommonBottomSheet
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryDetail
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryMap
import com.depromeet.team6.presentation.ui.itinerary.component.ItinerarySummary
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng

@Composable
fun ItineraryRoute(
    padding: PaddingValues,
    viewModel: ItineraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // SideEffect 감지
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getLegs()
    }

    when (uiState.courseDataLoadState) {
        LoadState.Idle -> Unit
        LoadState.Success -> ItineraryScreen(
            uiState = uiState
        )
        else -> Unit
    }
}

@Composable
fun ItineraryScreen(
    modifier: Modifier = Modifier,
    uiState: ItineraryContract.ItineraryUiState = ItineraryContract.ItineraryUiState()
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
            distance = 10,
            passShape = "127.02481,37.504562 127.024666,37.50452"
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
            distance = 57,
            passShape = "127.025675,37.501708 127.026528,37.499994 127.026856,37.499408 127.027367,37.498353 127.027525,37.498025 127.027589,37.497892 127.027717,37.497525 127.028253,37.496306 127.028436,37.495922 127.029794,37.493072 127.029858,37.492939 127.030497,37.491664 127.031522,37.489619 127.031586,37.489483 127.032458,37.487656 127.033683,37.485086 127.033819,37.484811 127.033928,37.484633 127.034036,37.484503 127.034319,37.484181 127.034628,37.483844 127.034800,37.483678 127.037200,37.481392 127.037383,37.481169 127.037661,37.480753 127.038047,37.480053 127.038289,37.479453 127.038389,37.479136"
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
            distance = 37,
            passShape = "127.0381,37.479004 127.03806,37.4791 127.03799,37.479313"
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
            distance = 10,
            passShape = "127.03799,37.479313 127.037636,37.479263 127.037445,37.47924"
        )
    )

    AtchaCommonBottomSheet(
        mainContent = {
            ItineraryMap(
                legs = uiState.courseData,
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
                    legs = uiState.courseData
                )
                ItineraryDetail(
                    legs = uiState.courseData
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
    ItineraryScreen()
}
