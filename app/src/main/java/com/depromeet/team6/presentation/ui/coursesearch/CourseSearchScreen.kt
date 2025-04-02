package com.depromeet.team6.presentation.ui.coursesearch

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.coursesearch.component.CourseAppBar
import com.depromeet.team6.presentation.ui.coursesearch.component.DestinationSearchBar
import com.depromeet.team6.presentation.ui.coursesearch.component.TransportTabMenu
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun CourseSearchRoute(
    padding: PaddingValues,
    departurePoint: String,
    destinationPoint: String,
    navigateToItinerary: (String) -> Unit,
    navigateToHome: () -> Unit,
    viewModel: CourseSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // SideEffect 감지 및 Toast 띄우기
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is CourseSearchContract.CourseSideEffect.ShowNotificationToast -> {
                    Toast.makeText(context, context.getString(R.string.course_set_notification_snackbar), Toast.LENGTH_SHORT).show()
                }

                is CourseSearchContract.CourseSideEffect.ShowSearchFailedToast -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setDepartureDestination(departurePoint, destinationPoint)
    }

    when (uiState.courseDataLoadState) {
        LoadState.Loading -> {
            CourseSearchScreen(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(defaultTeam6Colors.greyWashBackground)
                    .padding(padding)
            )
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        LoadState.Success -> CourseSearchScreen(
            uiState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .background(defaultTeam6Colors.greyWashBackground)
                .padding(padding),
            navigateToItinerary = navigateToItinerary,
            setNotification = {
                val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isUserLoggedIn", true) // "isUserLoggedIn" 키에 true 값을 저장
                editor.apply() // 또는 editor.commit()
                navigateToHome()
            },
            backButtonClicked = { navigateToHome() }
        )
        LoadState.Error -> {
            navigateToHome()
        }

        LoadState.Idle -> {
        }
    }
}

@Composable
fun CourseSearchScreen(
    modifier: Modifier = Modifier,
    uiState: CourseSearchContract.CourseUiState = CourseSearchContract.CourseUiState(),
    navigateToItinerary: (String) -> Unit = {},
    setNotification: () -> Unit = {},
    backButtonClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(defaultTeam6Colors.greyWashBackground)
    ) {
        CourseAppBar(backButtonClicked = backButtonClicked)
        DestinationSearchBar(
            startingPoint = uiState.startingPoint?.name ?: "",
            destination = uiState.destinationPoint?.name ?: "",
            modifier = Modifier
                .padding(top = 6.dp, start = 16.dp, end = 16.dp, bottom = 10.dp)
        )

        TransportTabMenu(
            availableCourses = uiState.courseData,
            onItemClick = navigateToItinerary,
            onRegisterAlarmBtnClick = {
                setNotification()
            }
        )
    }
}

@Preview
@Composable
fun CourseSearchScreenPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    CourseSearchScreen()
}
