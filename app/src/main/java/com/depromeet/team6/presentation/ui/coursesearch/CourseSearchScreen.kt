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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.coursesearch.component.CourseAppBar
import com.depromeet.team6.presentation.ui.coursesearch.component.DestinationSearchBar
import com.depromeet.team6.presentation.ui.coursesearch.component.TransportTabMenu
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.toast.atChaToastMessage
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.gson.Gson

@Composable
fun CourseSearchRoute(
    padding: PaddingValues,
    departurePoint: String,
    destinationPoint: String,
    navigateToItinerary: (String, String, String) -> Unit,
    navigateToHome: () -> Unit,
    viewModel: CourseSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.setEvent(CourseSearchContract.CourseEvent.OnEnter)
                Lifecycle.Event.ON_PAUSE -> viewModel.setEvent(CourseSearchContract.CourseEvent.OnExit)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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

                is CourseSearchContract.CourseSideEffect.NavigateHomeWithToast -> {
                    navigateToHome()
                    atChaToastMessage(context, R.string.course_set_notification_snackbar, Toast.LENGTH_SHORT)
                }
            }
        }
    }

    // UI state 초기화
    LaunchedEffect(Unit) {
        viewModel.setEvent(CourseSearchContract.CourseEvent.InitUiState(departurePoint, destinationPoint))
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
            setNotification = { routeId ->

                val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                val registeredCourse = uiState.courseData.find { it.routeId == routeId }

                if (registeredCourse != null) {
                    val courseJson = Gson().toJson(registeredCourse)
                    editor.putString("departurePoint", departurePoint) // 출발지
                    editor.putBoolean("alarmRegistered", true) // 알람 등록 여부
                    editor.putString("lastRouteId", routeId) // 막차 경로 Id
                    editor.putString("lastCourseInfo", courseJson) // 막차 경로
                    editor.apply()

                    viewModel.postAlarm(lastRouteId = routeId)
                } else {
                    atChaToastMessage(context, R.string.course_set_notification_failed_snackbar)
                }
            },
            backButtonClicked = { navigateToHome() },
            courseInfoToggleClick = { viewModel.setEvent(CourseSearchContract.CourseEvent.ItemCourseDetailToggleClick) },
            itemCardClick = { viewModel.setEvent(CourseSearchContract.CourseEvent.ItemCardClick(isTextClicked = it)) }
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
    navigateToItinerary: (String, String, String) -> Unit = { s: String, s1: String, s2: String -> },
    setNotification: (String) -> Unit = {},
    backButtonClicked: () -> Unit = {},
    courseInfoToggleClick: () -> Unit = {},
    itemCardClick: (Boolean) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(defaultTeam6Colors.greyWashBackground)
    ) {
        CourseAppBar(backButtonClicked = backButtonClicked)
        DestinationSearchBar(
            startingPoint = uiState.startingPoint?.name ?: uiState.startingPoint?.address!!,
            destination = "우리집",
            modifier = Modifier
                .padding(top = 6.dp, start = 16.dp, end = 16.dp, bottom = 10.dp)
        )

        TransportTabMenu(
            availableCourses = uiState.courseData,
            onItemClick = { courseInfoJson, isTextClicked ->
                itemCardClick(isTextClicked)
                navigateToItinerary(
                    courseInfoJson,
                    Gson().toJson(uiState.startingPoint!!),
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
fun CourseSearchScreenPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    CourseSearchScreen()
}
