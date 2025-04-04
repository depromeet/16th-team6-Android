package com.depromeet.team6.presentation.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.common.AtchaCommonBottomSheet
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryDetail
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryMap
import com.depromeet.team6.presentation.ui.itinerary.component.ItinerarySummary
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng

@Composable
fun ItineraryRoute(
    padding: PaddingValues,
    courseInfoJSON: String,
    departurePointJSON: String,
    destinationPointJSON: String,
    focusedMarkerParam: FocusedMarkerParameter?,
    navigateToBusCourse: (BusArrivalParameter) -> Unit,
    viewModel: ItineraryViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // SideEffect 감지
    LaunchedEffect(Unit) {
        viewModel.initItineraryInfo(
            courseInfoJSON,
            departurePointJSON,
            destinationPointJSON
        )
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
//        viewModel.getLegs()
    }

    when (uiState.courseDataLoadState) {
        LoadState.Idle -> {}
        LoadState.Success -> ItineraryScreen(
            marginTop = padding.calculateTopPadding(),
            marginBottom = padding.calculateBottomPadding(),
            uiState = uiState,
            focusedMarkerParam = focusedMarkerParam,
            onBackPressed = onBackPressed,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .background(defaultTeam6Colors.greyWashBackground),
            navigateToBusCourse = navigateToBusCourse
        )
        else -> Unit
    }
}

@Composable
fun ItineraryScreen(
    marginTop: Dp,
    marginBottom: Dp,
    modifier: Modifier = Modifier,
    uiState: ItineraryContract.ItineraryUiState = ItineraryContract.ItineraryUiState(),
    focusedMarkerParam: FocusedMarkerParameter? = null,
    navigateToBusCourse: (BusArrivalParameter) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val sheetScrollState = rememberScrollState()
    val itineraryInfo = uiState.itineraryInfo!!

    AtchaCommonBottomSheet(
        modifier = modifier,
        mainContent = {
            ItineraryMap(
                marginTop = marginTop,
                legs = itineraryInfo.legs,
                currentLocation = LatLng(37.5665, 126.9780),
                departurePoint = uiState.departurePoint!!,
                destinationPoint = uiState.destinationPoint!!,
                onBackPressed = onBackPressed,
                focusedMarkerParameter = focusedMarkerParam
            )
        },
        sheetContent = {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .noRippleClickable {
                        navigateToBusCourse(
                            BusArrivalParameter(
                                routeName = "일반:9-3",
                                stationName = "남서울중학교.성보중고등학교",
                                lat = 37.482817,
                                lon = 126.921012,
                                subtypeIdx = 2
                            )
                        )
                    }
//                    .nestedScroll(rememberNestedScrollInteropConnection())
                    .verticalScroll(sheetScrollState),
                verticalArrangement = Arrangement.Top
            ) {
                ItinerarySummary(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    totalTimeMinute = itineraryInfo.totalTime / 60,
                    boardingTime = itineraryInfo.boardingTime,
                    legs = itineraryInfo.legs
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(31.dp) // 8 + 1 + 22
                        .padding(top = 8.dp, bottom = 22.dp) // mimic the spacing
                        .background(Color(0x0AFFFFFF)) // applies to the 1.dp middle line
                )
                ItineraryDetail(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    courseInfo = itineraryInfo,
                    departurePoint = uiState.departurePoint!!,
                    destinationPoint = uiState.destinationPoint!!,
                    onClickBusInfo = navigateToBusCourse
                )
                Spacer(Modifier.height(marginBottom))
            }
        },
        sheetScrollState = sheetScrollState,
        marginBottom = marginBottom
    )
}

@Preview
@Composable
fun ItineraryScreenPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItineraryScreen(
        marginTop = 10.dp,
        marginBottom = 10.dp
    )
}
