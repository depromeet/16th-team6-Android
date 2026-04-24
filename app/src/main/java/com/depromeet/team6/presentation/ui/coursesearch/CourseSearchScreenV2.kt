package com.depromeet.team6.presentation.ui.coursesearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.coursesearch.component.DestinationSearchBar
import com.depromeet.team6.presentation.ui.coursesearch.component.TransportTabMenuV2
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.gson.Gson

@Composable
fun CourseSearchScreenV2(
    modifier: Modifier = Modifier,
    uiState: CourseSearchContract.CourseUiState = CourseSearchContract.CourseUiState(),
    navigateToItinerary: (String, String, String) -> Unit = { s: String, s1: String, s2: String -> },
    setNotification: (String) -> Unit = {},
    backButtonClicked: () -> Unit = {},
    courseInfoToggleClick: () -> Unit = {},
    itemCardClick: (Boolean) -> Unit = {},
    searchBarClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(defaultTeam6Colors.gray950)
    ) {
        DestinationSearchBar(
            startingPoint = uiState.startingPoint?.name ?: uiState.startingPoint?.address!!,
            destination = "우리집",
            closeButtonClicked = backButtonClicked,
            addressRowClicked = searchBarClick,
            modifier = Modifier
        )
        TransportTabMenuV2(
            availableCourses = uiState.courseData,
            isLoaded = uiState.courseUiLoadState == LoadState.Success,
            courseSearchDataState = uiState.courseSearchDataLoadState,
            onItemClick = { courseInfoJson, isTextClicked ->
                itemCardClick(isTextClicked)
                navigateToItinerary(
                    courseInfoJson,
                    Gson().toJson(uiState.startingPoint),
                    Gson().toJson(uiState.destinationPoint!!)
                )
            },
            courseInfoToggleClick = courseInfoToggleClick,
            onRegisterAlarmBtnClick = { routeId ->
                setNotification(routeId)
            }
        )
    }
}

@Preview
@Composable
fun CourseSearchScreenPreviewV2(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    CourseSearchScreenV2()
}
