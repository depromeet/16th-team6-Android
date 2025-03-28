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
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.common.AtchaCommonBottomSheet
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryDetail
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryMap
import com.depromeet.team6.presentation.ui.itinerary.component.ItinerarySummary
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng

@Composable
fun ItineraryRoute(
    padding: PaddingValues,
    courseInfoJSON: String,
    viewModel: ItineraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // SideEffect 감지
    LaunchedEffect(Unit) {
        viewModel.initItineraryInfo(courseInfoJSON)
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
            uiState = uiState,
            modifier = Modifier
                .padding(padding)
        )
        else -> Unit
    }
}

@Composable
fun ItineraryScreen(
    modifier: Modifier = Modifier,
    uiState: ItineraryContract.ItineraryUiState = ItineraryContract.ItineraryUiState()
) {
    val itineraryInfo = uiState.itineraryInfo!!
    AtchaCommonBottomSheet(
        mainContent = {
            ItineraryMap(
                legs = itineraryInfo.legs,
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
                    totalTimeMinute = itineraryInfo.remainingTime / 60,
                    boardingTime = itineraryInfo.boardingTime,
                    legs = itineraryInfo.legs
                )
                ItineraryDetail(
                    legs = itineraryInfo.legs
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
