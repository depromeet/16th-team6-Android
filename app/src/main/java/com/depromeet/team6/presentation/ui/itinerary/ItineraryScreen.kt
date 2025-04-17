package com.depromeet.team6.presentation.ui.itinerary

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.common.AtchaCommonBottomSheet
import com.depromeet.team6.presentation.ui.home.component.RefreshLottieButton
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryDetail
import com.depromeet.team6.presentation.ui.itinerary.component.ItineraryMap
import com.depromeet.team6.presentation.ui.itinerary.component.ItinerarySummary
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LAT
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.toast.atChaToastMessage
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.launch

@Composable
fun ItineraryRoute(
    padding: PaddingValues,
    courseInfoJSON: String,
    departurePointJSON: String,
    destinationPointJSON: String,
    focusedMarkerParam: FocusedMarkerParameter?,
    navigateToBusCourse: (BusArrivalParameter) -> Unit,
    navigateToHome: () -> Unit,
    viewModel: ItineraryViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val getCurrentLocation: () -> Unit = {
        coroutineScope.launch {
            if (PermissionUtil.hasLocationPermissions(context)) { // 위치 권한이 있으면
                val location = context.getUserLocation()
                viewModel.setEvent(ItineraryContract.ItineraryEvent.CurrentLocationClicked(location))
            }
        }
    }
    // SideEffect 감지
    LaunchedEffect(Unit) {
        val location = if (PermissionUtil.hasLocationPermissions(context)) {
            context.getUserLocation()
        } else {
            LatLng(
                DEFAULT_LAT,
                DEFAULT_LNG
            )
        }
        viewModel.initItineraryInfo(
            courseInfoJSON,
            departurePointJSON,
            destinationPointJSON,
            location
        )
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                ItineraryContract.ItinerarySideEffect.NavigateHomeWithToast -> {
                    navigateToHome()
                    atChaToastMessage(context, R.string.course_set_notification_snackbar, Toast.LENGTH_SHORT)
                }
                ItineraryContract.ItinerarySideEffect.ShowNotificationToastSetAlarm -> {
                    Toast.makeText(context, context.getString(R.string.course_set_notification_snackbar), Toast.LENGTH_SHORT).show()
                }

                ItineraryContract.ItinerarySideEffect.ShowNotificationToastSetAlarmFailed -> {
                    Toast.makeText(context, context.getString(R.string.course_set_notification_failed_snackbar), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when (uiState.courseDataLoadState) {
        LoadState.Idle -> {}
        LoadState.Success -> ItineraryScreen(
            marginTop = padding.calculateTopPadding(),
            marginBottom = padding.calculateBottomPadding(),
            uiState = uiState,
            focusedMarkerParam = focusedMarkerParam,
            onBackPressed = onBackPressed,
            onRefreshButtonClick = { viewModel.setEvent(ItineraryContract.ItineraryEvent.RefreshButtonClicked) },
            registerAlarmButtonClick = { routeId ->
                val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                val registeredCourse = uiState.itineraryInfo

                if (registeredCourse != null) {
                    val courseJson = Gson().toJson(registeredCourse)
                    editor.putString("departurePoint", departurePointJSON) // 출발지
                    editor.putString("destinationPoint", destinationPointJSON) // 도착지
                    editor.putBoolean("alarmRegistered", true) // 알람 등록 여부
                    editor.putString("lastRouteId", routeId) // 막차 경로 Id
                    editor.putString("lastCourseInfo", courseJson) // 막차 경로
                    editor.apply()

                    viewModel.setEvent(ItineraryContract.ItineraryEvent.RegisterAlarm(routeId = routeId))
                } else {
                    atChaToastMessage(context, R.string.course_set_notification_failed_snackbar)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .background(defaultTeam6Colors.greyWashBackground),
            navigateToBusCourse = navigateToBusCourse,
            currentLocationBtnClick = getCurrentLocation
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
    onRefreshButtonClick: () -> Unit = {},
    registerAlarmButtonClick: (String) -> Unit = {},
    onBackPressed: () -> Unit = {},
    currentLocationBtnClick: () -> Unit = {}
) {
    val sheetScrollState = rememberScrollState()
    val itineraryInfo = uiState.itineraryInfo!!
    val context = LocalContext.current

    Box() {
        AtchaCommonBottomSheet(
            modifier = Modifier,
            mainContent = {
                ItineraryMap(
                    marginTop = marginTop,
                    legs = itineraryInfo.legs,
                    currentLocation = uiState.currentLocation,
                    departurePoint = uiState.departurePoint!!,
                    destinationPoint = uiState.destinationPoint!!,
                    onBackPressed = onBackPressed,
                    focusedMarkerParameter = focusedMarkerParam,
                    currentLocationBtnClick = currentLocationBtnClick
                )
            },
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                        busArrivalStatus = uiState.busArrivalStatus,
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

        val prefs = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        // 막차알림 등록 되어 있으면 리프레시 버튼
        // 막차알림 등록 안되어 있으면 알림등록 버튼
        if (prefs.getBoolean("alarmRegistered", false)) {
            RefreshLottieButton(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.BottomEnd)
                    // TODO : 레이아웃 수정
                    .offset(x = -16.dp, y = -50.dp)
                    .background(shape = CircleShape, color = Color(0xff48484B))
                    .padding(7.5.dp),
                onClick = onRefreshButtonClick
            )
        } else {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -60.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .roundedBackgroundWithPadding(
                        backgroundColor = defaultTeam6Colors.main,
                        cornerRadius = 12.dp,
                        padding = PaddingValues(vertical = 14.dp)
                    )
                    .noRippleClickable {
                        registerAlarmButtonClick(itineraryInfo.routeId)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_bottom_sheet_bell_16),
                    colorFilter = ColorFilter.tint(defaultTeam6Colors.black),
                    contentDescription = "set alarm icon"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.last_transport_info_set_notification),
                    style = defaultTeam6Typography.heading5Bold17,
                    color = defaultTeam6Colors.black
                )
            }
        }
    }
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
