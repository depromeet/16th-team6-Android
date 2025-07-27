package com.depromeet.team6.presentation.ui.coursesearch

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.ui.coursesearch.component.CourseAppBar
import com.depromeet.team6.presentation.ui.coursesearch.component.DestinationSearchBar
import com.depromeet.team6.presentation.ui.coursesearch.component.TransportTabMenu
import com.depromeet.team6.presentation.ui.home.component.DeleteAlarmDialog
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.ui.overlay.OverlayPermissionDialog
import com.depromeet.team6.presentation.ui.overlay.PermissionSnackbar
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.HomeAmplitude.ALERT_END_POPUP_2
import com.depromeet.team6.presentation.util.HomeAmplitude.POPUP
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.base.ApiErrorSideEffect
import com.depromeet.team6.presentation.util.permission.PermissionUtil
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
    navigateToHomeAfterAlarmRegister: () -> Unit,
    navigateToLogin: () -> Unit,
    fromLockScreen: Boolean = false,
    viewModel: CourseSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.setEvent(CourseSearchContract.CourseEvent.OnEnter)

                    viewModel.checkOverlayPermissionOnResume()
                }

                Lifecycle.Event.ON_PAUSE -> viewModel.setEvent(CourseSearchContract.CourseEvent.OnExit)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val fromLockScreen = sharedPreferences.getBoolean("fromLockScreen", false)

        if (fromLockScreen) {
            viewModel.setSortType(2)

            sharedPreferences.edit().remove("fromLockScreen").apply()
        }
    }

    // SideEffect 감지 및 Toast 띄우기
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is ApiErrorSideEffect.ShowToastSideEffect -> {
                    Toast.makeText(context, sideEffect.toastMessage, Toast.LENGTH_SHORT).show()
                }

                is ApiErrorSideEffect.NavigateToHomeSideEffect -> {
                    navigateToHome()
                }

                is ApiErrorSideEffect.NavigateToLoginSideEffect -> {
                    navigateToLogin()
                }

                is CourseSearchContract.CourseSideEffect.ShowNotificationToast -> {
                    Toast.makeText(context, context.getString(R.string.course_set_notification_snackbar), Toast.LENGTH_SHORT).show()
                }

                is CourseSearchContract.CourseSideEffect.ShowSearchFailedToast -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }

                is CourseSearchContract.CourseSideEffect.NavigateHomeWithToast -> {
                    navigateToHomeAfterAlarmRegister()
                    atChaToastMessage(context, R.string.course_set_notification_snackbar, Toast.LENGTH_SHORT)
                }
            }
        }
    }

    when (uiState.courseDataLoadState) {
        LoadState.Loading -> {
            CourseSearchScreen(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(defaultTeam6Colors.gray950)
                    .padding(padding)
            )
            Box(modifier = Modifier.fillMaxSize()) {
                AtChaLoadingView()
            }
        }

        LoadState.Success -> {
            CourseSearchScreen(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(defaultTeam6Colors.gray950)
                    .padding(padding),
                navigateToItinerary = navigateToItinerary,
                setNotification = { routeId ->
                    if (PermissionUtil.needsOverlayPermission(context)) {
                        if (viewModel.shouldShowOverlayDialog()) {
                            viewModel.showOverlayPermissionDialog()
                        } else {
                            viewModel.showPermissionSnackbar()
                        }
                    } else {
                        val registeredCourse = uiState.courseData.find { it.routeId == routeId }

                        if (registeredCourse != null) {
                            viewModel.postAlarm(
                                departurePoint = departurePoint,
                                destinationPoint = destinationPoint,
                                lastRouteId = routeId,
                                alarmTimeStamp = registeredCourse.departureTime
                            )
                        } else {
                            atChaToastMessage(context, R.string.course_set_notification_failed_snackbar)
                        }
                    }
                },
                backButtonClicked = { navigateToHome() },
                courseInfoToggleClick = { viewModel.setEvent(CourseSearchContract.CourseEvent.ItemCourseDetailToggleClick) },
                itemCardClick = {
                    viewModel.setEvent(
                        CourseSearchContract.CourseEvent.ItemCardClick(
                            isTextClicked = it
                        )
                    )
                }
            )
            if (uiState.showPermissionSnackbar) {
                Box(modifier = Modifier.fillMaxSize()) {
                    PermissionSnackbar(
                        onSettingsClick = {
                            viewModel.dismissPermissionSnackbar()
                            PermissionUtil.openOverlayPermissionSettings(context)
                        },
                        onDismiss = {
                            viewModel.dismissPermissionSnackbar()
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 40.dp)
                    )
                }
            }

            if (uiState.showOverlayPermissionDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = defaultTeam6Colors.black.copy(alpha = 0.76f))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        OverlayPermissionDialog(
                            onDismiss = {
                                viewModel.dismissOverlayPermissionDialog()
                                viewModel.markOverlayDialogAsShow()
                                viewModel.showPermissionSnackbar()
                            },
                            onSettingClicked = {
                                viewModel.dismissOverlayPermissionDialog()
                                viewModel.markOverlayDialogAsShow()
                                PermissionUtil.openOverlayPermissionSettings(context)
                            }
                        )
                    }
                }
            }

            // 다이얼로그 표시
            if (uiState.showDeleteAlarmDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = defaultTeam6Colors.black.copy(alpha = 0.76f))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        DeleteAlarmDialog(
                            onDismiss = {
                                viewModel.dismissDeleteAlarmDialog()
                            },
                            onSuccess = {
                                AmplitudeUtils.trackEventWithProperties(
                                    ALERT_END_POPUP_2,
                                    mapOf(
                                        SCREEN_NAME to POPUP,
                                        USER_ID to viewModel.getUserId(),
                                        ALERT_END_POPUP_2 to 1
                                    )
                                )

                                // 알림 등록 로직 실행
                                val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()

                                val registeredCourse = uiState.courseData.find { it.routeId == uiState.selectedRouteId }

                                editor.remove("departurePoint")
                                editor.remove("lastCourseInfo")
                                editor.remove("lastRouteId")
                                editor.remove("alarmRegistered")
                                editor.remove("userDeparture")

                                if (registeredCourse != null) {
                                    val courseJson = Gson().toJson(registeredCourse)
                                    editor.remove("userDeparture")
                                    editor.putString("departurePoint", departurePoint) // 출발지
                                    editor.putString("destinationPoint", destinationPoint) // 도착지
                                    editor.putBoolean("alarmRegistered", true) // 알람 등록 여부
                                    editor.putString("lastRouteId", uiState.selectedRouteId) // 막차 경로 Id
                                    editor.putString("lastCourseInfo", courseJson) // 막차 경로
                                    editor.apply()

                                    viewModel.updateAlarm(
                                        departurePoint = departurePoint,
                                        destinationPoint = destinationPoint,
                                        lastRouteId = uiState.selectedRouteId,
                                        alarmTimeStamp = registeredCourse.departureTime
                                    )
                                    viewModel.dismissDeleteAlarmDialog()
                                } else {
                                    atChaToastMessage(context, R.string.course_set_notification_failed_snackbar)
                                    viewModel.dismissDeleteAlarmDialog()
                                }
                            },
                            sortType = uiState.sortType
                        )
                    }
                }
            }
        }
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
            .background(defaultTeam6Colors.gray950)
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
            isLoaded = uiState.courseDataLoadState == LoadState.Success,
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
