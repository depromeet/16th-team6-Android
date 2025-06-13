package com.depromeet.team6.presentation.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.model.home.CharacterState
import com.depromeet.team6.presentation.model.home.SpeechBubbleData
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterMap
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterSheet
import com.depromeet.team6.presentation.ui.home.component.CharacterLottieSpeechBubble
import com.depromeet.team6.presentation.ui.home.component.CurrentLocationSheet
import com.depromeet.team6.presentation.ui.home.component.DeleteAlarmDialog
import com.depromeet.team6.presentation.ui.home.component.TMapViewCompose
import com.depromeet.team6.presentation.ui.home.component.UnifiedCharacterBubble
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LAT
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.HomeAmplitude.ALERT_END_POPUP_1
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_COURSESEARCH_ENTERED_DIRECT
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_COURSESEARCH_ENTERED_WITH_INPUT
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_DEPARTURE_TIME_CLICKED
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_DEPARTURE_TIME_SUGGESTION_CLICKED
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_DESTINATION_CLICKED
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_ROUTE_CLICKED
import com.depromeet.team6.presentation.util.HomeAmplitude.POPUP
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import timber.log.Timber
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// TODO : maxsize -> 백그라운드 -> padding

@Composable
fun HomeRoute(
    padding: PaddingValues,
    navigateToLogin: () -> Unit,
    navigateToCourseSearch: (String, String) -> Unit,
    navigateToMypage: () -> Unit,
    navigateToItinerary: (String, String, String, FocusedMarkerParameter?) -> Unit,
    navigateToSearchLocation: (Address) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var permissionGranted by remember { mutableStateOf(PermissionUtil.hasLocationPermissions(context)) }
    var userLocation by remember { mutableStateOf(LatLng(DEFAULT_LAT, DEFAULT_LNG)) } // 서울시 기본 위치

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionGranted = permissions.values.all { it }
            if (permissionGranted) {
                Timber.d("Location_Permission Has Granted")
            }
        }
    )

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadAlarmAndCourseInfoFromPrefs(context)
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserDepartureState(context)
    }

    // 화면이 다시 활성화될 때마다 사용자 출발 상태를 새로 로드
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                // 화면이 다시 보일 때마다 사용자 출발 상태 로드
                viewModel.loadUserDepartureState(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
        viewModel.sideEffect.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is HomeContract.HomeSideEffect.NavigateToMypage -> navigateToMypage()
                    is HomeContract.HomeSideEffect.NavigateToItinerary -> navigateToItinerary(
                        Gson().toJson(uiState.itineraryInfo),
                        Gson().toJson(uiState.departurePoint),
                        Gson().toJson(uiState.destinationPoint),
                        sideEffect.markerParameter
                    )
                }
            }
    }

    LaunchedEffect(permissionGranted) {
        if (PermissionUtil.hasLocationPermissions(context)) { // 위치 권한이 있으면
            val location = context.getUserLocation()
            userLocation = location
        }

        viewModel.getCenterLocation(LatLng(userLocation.latitude, userLocation.longitude))
    }

    LaunchedEffect(Unit) {
        if (uiState.isAlarmRegistered) {
            viewModel.registerAlarm()
        }
    }

    LaunchedEffect(uiState.isAlarmRegistered, uiState.firtTransportTation) {
        if (uiState.isAlarmRegistered && uiState.firtTransportTation == TransportType.BUS) {
            viewModel.startPollingBusStarted(routeId = uiState.lastRouteId)
        } else {
            viewModel.stopPollingBusStarted()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getTaxiCost()
    }

    SideEffect {
        if (!PermissionUtil.isLocationPermissionRequested(context) &&
            !PermissionUtil.hasLocationPermissions(context)
        ) {
            PermissionUtil.requestLocationPermissions(
                context = context,
                locationPermissionLauncher = locationPermissionsLauncher
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setEvent(HomeContract.HomeEvent.SetDestination)
    }

//    LaunchedEffect(Unit) {
//        viewModel.updateCharacterState()
//    }

    when (uiState.loadState) {
        LoadState.Idle, LoadState.Loading, LoadState.Success -> {
            Box {
                HomeScreen(
                    userLocation = LatLng(userLocation.latitude, userLocation.longitude),
                    homeUiState = uiState,
                    onCharacterClick = { viewModel.onCharacterClick() },
                    navigateToMypage = navigateToMypage,
//                    navigateToItinerary = navigateToItinerary,
                    modifier = modifier,
                    padding = padding,
                    afterRegisterMapMarkerClick = { focusedMarkerParemeter ->
                        viewModel.setEvent(HomeContract.HomeEvent.AfterRegisterMapMarkerClick)
                        navigateToItinerary(
                            Gson().toJson(uiState.itineraryInfo),
                            Gson().toJson(uiState.departurePoint),
                            Gson().toJson(uiState.destinationPoint),
                            focusedMarkerParemeter
                        )
                    },
                    courseDetailBtnClick = { key ->
                        viewModel.setEvent(HomeContract.HomeEvent.CourseDetailButtonClick(key))
                        navigateToItinerary(
                            Gson().toJson(uiState.itineraryInfo),
                            Gson().toJson(uiState.departurePoint),
                            Gson().toJson(uiState.destinationPoint),
                            null
                        )
                    },
                    onSearchClick = {
                        val currentLocationJSON = Gson().toJson(uiState.markerPoint)
                        val destinationPointJSON = Gson().toJson(uiState.destinationPoint)
                        navigateToCourseSearch(
                            currentLocationJSON,
                            destinationPointJSON
                        )

                        AmplitudeUtils.trackEventWithProperties(
                            eventName = HOME_COURSESEARCH_ENTERED_DIRECT,
                            mapOf(
                                USER_ID to viewModel.getUserId(),
                                SCREEN_NAME to HOME,
                                HOME_COURSESEARCH_ENTERED_DIRECT to 1
                            )
                        )
                    },
                    onDestinationClick = {
                        AmplitudeUtils.trackEventWithProperties(
                            eventName = HOME_DESTINATION_CLICKED,
                            mapOf(
                                USER_ID to viewModel.getUserId(),
                                SCREEN_NAME to HOME,
                                HOME_DESTINATION_CLICKED to 1
                            )
                        )
                    },
                    onFinishClick = {
                        viewModel.setEvent(HomeContract.HomeEvent.FinishAlarmClicked)
//                viewModel.finishAlarm(context)
                    },
                    deleteAlarmConfirmed = {
                        viewModel.setEvent(HomeContract.HomeEvent.DeleteAlarmConfirmed)
                        viewModel.deleteAlarm(uiState.lastRouteId, context)
                    },
                    dismissDialog = {
                        viewModel.setEvent(HomeContract.HomeEvent.DismissDialog)
                    },
                    onRefreshClick = {
                    },
                    navigateToSearchLocation = {
                        navigateToSearchLocation(
                            uiState.destinationPoint
                        )

                        AmplitudeUtils.trackEventWithProperties(
                            eventName = HOME_COURSESEARCH_ENTERED_WITH_INPUT,
                            mapOf(
                                USER_ID to viewModel.getUserId(),
                                SCREEN_NAME to HOME,
                                HOME_COURSESEARCH_ENTERED_WITH_INPUT to 1
                            )
                        )
                    },
                    onEvent = viewModel::setEvent
                )

                if (uiState.loadState == LoadState.Loading) {
                    AtChaLoadingView()
                }
            }
        }

        LoadState.Error -> navigateToLogin()
    }
}

@Composable
fun HomeScreen(
    padding: PaddingValues,
    userLocation: LatLng,
    modifier: Modifier = Modifier,
    homeUiState: HomeContract.HomeUiState = HomeContract.HomeUiState(),
    onCharacterClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onDestinationClick: () -> Unit = {},
    onFinishClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    navigateToMypage: () -> Unit = {},
    afterRegisterMapMarkerClick: (FocusedMarkerParameter?) -> Unit = { },
    courseDetailBtnClick: (String) -> Unit = {},
    deleteAlarmConfirmed: () -> Unit = {},
    dismissDialog: () -> Unit = {},
    navigateToSearchLocation: () -> Unit = {},
    onEvent: (HomeContract.HomeEvent) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel() // TODO : TmapViewCompose 변경 후 제거
) {
    val context = LocalContext.current
    val colors = LocalTeam6Colors.current

    val characterTexts = CharacterTexts(
        taxiCostText = stringResource(R.string.home_bubble_basic_text),
        aboutText = stringResource(R.string.home_bubble_least_text),
        wonText = stringResource(R.string.home_bubble_won_text),
        moveMapText = stringResource(R.string.home_bubble_map_text),

        expectDepartText = stringResource(R.string.home_bubble_alarm_emphasis_text),
        expectTaxiCostText = stringResource(R.string.home_bubble_user_departure_taxi_cost_text),
        expectBustDepartureText = stringResource(R.string.home_bubble_before_bus_departure_text),

        expectTimeClickedText1 = stringResource(R.string.home_bubble_expect_departure_time_clicked_text_1),
        expectTimeClickedText2 = stringResource(R.string.home_bubble_expect_departure_time_clicked_text_2),
        changeLocationClickedText = stringResource(R.string.home_bubble_location_clicked_text),

        busDepartureText1 = stringResource(R.string.home_bubble_bus_departure_text_1),
        busDepartureText2 = stringResource(R.string.home_bubble_bus_departure_text_2),
        subwayDepartureText1 = stringResource(R.string.home_bubble_subway_departure_text_1),
        subwayDepartureText2 = stringResource(R.string.home_bubble_subway_departure_text_2),
        timeInfoText1 = stringResource(R.string.home_bubble_time_info_text_1),
        timeInfoText2 = stringResource(R.string.home_bubble_time_info_text_2),
        busDepartureTaxiCostText = stringResource(R.string.home_bubble_departed_taxi_cost_text),
        trustText1 = stringResource(R.string.home_bubble_trust_text_1),
        trustText2 = stringResource(R.string.home_bubble_trust_text_2),

        userDepartureBusText = stringResource(R.string.home_bubble_user_departure_bus_text),
        userDepartureSubwayText = stringResource(R.string.home_bubble_user_departure_subway_text),
        userDepartureDownText = stringResource(R.string.home_bubble_user_departure_down_text),
        userDepartureDetailBtnText = stringResource(R.string.home_bubble_user_departure_detail_btn_text),
        userDepartureCheckDetailText = stringResource(R.string.home_bubble_user_departure_check_detail_text),
    )

    val baseCharacterData = remember(
        homeUiState.isAlarmRegistered,
        homeUiState.userDeparture,
        homeUiState.isBusDeparted,
        homeUiState.taxiCost,
        characterTexts
    ) {
        generateCharacterState(homeUiState, characterTexts)
    }

    val conditionKey = remember(
        homeUiState.isAlarmRegistered,
        homeUiState.userDeparture,
        homeUiState.isBusDeparted
    ) {
        "${homeUiState.isAlarmRegistered}_${homeUiState.userDeparture}_${homeUiState.isBusDeparted}"
    }

    var currentSpeechIndex by remember(conditionKey) { mutableStateOf(0) }
    var animationTrigger by remember { mutableStateOf(0) }

    val safeCurrentIndex = if (baseCharacterData.speechTexts.isNotEmpty()) {
        currentSpeechIndex.coerceIn(0, baseCharacterData.speechTexts.size - 1)
    } else {
        0
    }

    val characterState = CharacterState(
        speechTexts = baseCharacterData.speechTexts,
        lottieResId = baseCharacterData.lottieResId,
        bottomPadding = baseCharacterData.bottomPadding,
        currentSpeechIndex = safeCurrentIndex,
        animationTrigger = animationTrigger,
        isAnimating = true
    )

    var characterAnimationTrigger by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.black)
            .padding(
                top = 0.dp,
                bottom = padding.calculateBottomPadding()
            )
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_home_mypage),
            contentDescription = stringResource(R.string.mypage_icon_description),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp + padding.calculateTopPadding(), end = 16.dp)
                .clickable {
                    navigateToMypage()
                }
                .zIndex(1f)
        )

        if (homeUiState.isAlarmRegistered) {
            AfterRegisterMap(
                padding,
                currentLocation = userLocation,
                legs = homeUiState.itineraryInfo!!.legs,
                viewModel = viewModel,
                onTransportMarkerClick = { markerParameter ->
                    afterRegisterMapMarkerClick(markerParameter)
                }
            )
        } else {
            TMapViewCompose(
                padding,
                userLocation,
                viewModel = viewModel
            ) // Replace with your actual API key
        }

        var isConfirmed = false

        val firstTransportation = homeUiState.firtTransportTation
        if (firstTransportation == TransportType.SUBWAY) {
            isConfirmed = true
        }

        if (homeUiState.isBusDeparted) {
            isConfirmed = true
        }

        // 알람 등록 시 Home UI
        if (homeUiState.isAlarmRegistered) {
            AfterRegisterSheet(
                timerFinish = homeUiState.timerFinish,
                startLocation = homeUiState.departurePointName,
                isConfirmed = isConfirmed,
                afterUserDeparted = homeUiState.userDeparture,
                transportType = homeUiState.firtTransportTation,
                transportationNumber = homeUiState.firstTransportationNumber,
                transportationName = homeUiState.firstTransportationName,
                timeToLeave = formatTimeString(homeUiState.departureTime),
                boardingTime = homeUiState.boardingTime,
                homeArrivedTime = homeUiState.homeArrivedTime,
                destination = stringResource(R.string.home_my_home_text),
                onCourseTextClick = {
                    AmplitudeUtils.trackEventWithProperties(
                        HOME_ROUTE_CLICKED,
                        mapOf(
                            SCREEN_NAME to HOME,
                            USER_ID to viewModel.getUserId(),
                            HOME_ROUTE_CLICKED to 1
                        )
                    )
                },
                deleteAlarmConfirmed = deleteAlarmConfirmed,
                dismissDialog = dismissDialog,
                onFinishClick = {
                    onFinishClick()
                },
                onCourseDetailClick = courseDetailBtnClick,
                onTimerFinished = {
                    viewModel.onTimerFinished()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(1f),
                onRefreshClick = {
                    onRefreshClick()
                    if (homeUiState.firtTransportTation == TransportType.BUS) {
                        viewModel.getBusArrival()
                    }
                },
                onIconClick = {
                    characterAnimationTrigger++
                },
                onHomeDepartureTimeClick = {
                    AmplitudeUtils.trackEventWithProperties(
                        HOME_DEPARTURE_TIME_CLICKED,
                        mapOf(
                            SCREEN_NAME to HOME,
                            USER_ID to viewModel.getUserId(),
                            HOME_DEPARTURE_TIME_CLICKED to 1
                        )
                    )
                },
                onHomeExpectDepartureTimeClick = {
                    AmplitudeUtils.trackEventWithProperties(
                        HOME_DEPARTURE_TIME_CLICKED,
                        mapOf(
                            SCREEN_NAME to HOME,
                            USER_ID to viewModel.getUserId(),
                            HOME_DEPARTURE_TIME_SUGGESTION_CLICKED to 1
                        )
                    )
                },
                busStationLeft = homeUiState.busRemainingStations
            )
        } else {
            CurrentLocationSheet(
                currentLocation = homeUiState.markerPoint.name,
                onSearchLocationClick = navigateToSearchLocation,
                destination = stringResource(R.string.home_my_home_text),
                onSearchClick = {
                    onSearchClick()
                },
                onDestinationClick = { onDestinationClick() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .zIndex(1f)
            )
        }

        UnifiedCharacterBubble(
            characterState = characterState,
            onCharacterClick = {
                if (characterState.speechTexts.size > 1) {
                    currentSpeechIndex = (currentSpeechIndex + 1) % characterState.speechTexts.size
                    animationTrigger += 1
                } else {
                    animationTrigger += 1
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = characterState.bottomPadding)
        )

        if (homeUiState.deleteAlarmDialogVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = defaultTeam6Colors.black.copy(alpha = 0.76f))
                    .zIndex(2f)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(3f)
            ) {
                DeleteAlarmDialog(
                    onDismiss = {
                        dismissDialog()
                    },
                    onSuccess = {
                        deleteAlarmConfirmed()
                        AmplitudeUtils.trackEventWithProperties(
                            ALERT_END_POPUP_1,
                            mapOf(
                                SCREEN_NAME to POPUP,
                                USER_ID to viewModel.getUserId(),
                                ALERT_END_POPUP_1 to 1
                            )
                        )
                    },
                    sortType = 1
                )
            }
        }
    }
}

private fun formatTimeString(timeString: String): String {
    return try {
        if (timeString.contains("T")) {
            // ISO 날짜 형식 (2023-01-01T12:30:00) 처리
            val dateTime = LocalDateTime.parse(timeString)
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            dateTime.format(formatter)
        } else if (timeString.contains(":") && timeString.split(":").size >= 2) {
            // 이미 시간 형식이면 그대로 반환 (HH:mm:ss 또는 HH:mm)
            if (timeString.split(":").size == 2) {
                // HH:mm 형식인 경우 HH:mm:00으로 변환
                "$timeString:00"
            } else {
                timeString
            }
        } else if (timeString.matches(Regex("\\d+"))) {
            // 초 단위 값을 HH:mm:ss 형식으로 변환
            val totalSeconds = timeString.toLong()
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            timeString
        }
    } catch (e: Exception) {
        Timber.e("formatTimeString 오류: ${e.message}")
        timeString
    }
}

private fun generateCharacterState(
    homeUiState: HomeContract.HomeUiState,
    texts: CharacterTexts
): CharacterState {
    return when {
        // 알림 등록 전
        !homeUiState.isAlarmRegistered -> {
            CharacterState(
                speechTexts = listOf(
                    SpeechBubbleData(
                        prefixText = texts.taxiCostText,
                        emphasisText = texts.aboutText + NumberFormat.getNumberInstance(Locale.US)
                            .format(homeUiState.taxiCost) + texts.wonText,
                        suffixText = null,
                        topPrefixText = texts.moveMapText,
                        lineCount = 2
                    ),
                    SpeechBubbleData(
                        prefixText = texts.taxiCostText,
                        emphasisText = texts.aboutText + NumberFormat.getNumberInstance(Locale.US)
                            .format(homeUiState.taxiCost) + texts.wonText,
                        suffixText = null,
                        lineCount = 1
                    )
                ),
                lottieResId = R.raw.atcha_character_1,
                bottomPadding = 194.dp
            )
        }

        // 알림 등록 후 & 사용자 출발 전 & 차고지 출발 전
        homeUiState.isAlarmRegistered && !homeUiState.userDeparture && !homeUiState.isBusDeparted -> {
            CharacterState(
                speechTexts = listOf(
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.expectDepartText,
                        suffixText = "",
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = texts.expectTaxiCostText,
                        emphasisText = texts.aboutText + NumberFormat.getNumberInstance(Locale.US)
                            .format(homeUiState.taxiCost) + texts.wonText,
                        suffixText = null,
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.expectBustDepartureText,
                        suffixText = "",
                        lineCount = 1
                    ),
                ),
                lottieResId = R.raw.atcha_chararcter_3,
                bottomPadding = 218.dp
            )
        }

        // 알림 등록 후 & 사용자 출발 전 & 차고지 출발 후
        homeUiState.isAlarmRegistered && !homeUiState.userDeparture && homeUiState.isBusDeparted -> {
            CharacterState(
                speechTexts = listOf(
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.timeInfoText2,
                        suffixText = "",
                        topPrefixText = null,
                        topEmphasisText = texts.timeInfoText1,
                        topSuffixText = null,
                        lineCount = 2
                    ),
                    SpeechBubbleData(
                        prefixText = texts.busDepartureTaxiCostText,
                        emphasisText = texts.aboutText + NumberFormat.getNumberInstance(Locale.US)
                            .format(homeUiState.taxiCost) + texts.wonText,
                        suffixText = null,
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.trustText1 ,
                        suffixText = texts.trustText2,
                        lineCount = 1
                    ),
                ),
                lottieResId = R.raw.atcha_character_4,
                bottomPadding = 218.dp
            )
        }

        // 알림 등록 후 & 사용자 출발 후 & 타이머 종료 전 & 버스
        !homeUiState.timerFinish && homeUiState.firtTransportTation == TransportType.BUS -> {
            CharacterState(
                speechTexts = listOf(
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.userDepartureBusText,
                        suffixText = "",
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = texts.userDepartureDownText,
                        emphasisText = texts.userDepartureDetailBtnText,
                        suffixText = texts.userDepartureCheckDetailText,
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.trustText1 ,
                        suffixText = texts.trustText2,
                        lineCount = 1
                    )
                ),
                lottieResId = R.raw.atcha_character_4, // TODO : atcha_character_5 로 변경
                bottomPadding = 218.dp
            )
        }

        // 알림 등록 후 & 사용자 출발 후 & 타이머 종료 전 & 지하철
        !homeUiState.timerFinish && homeUiState.firtTransportTation == TransportType.SUBWAY -> {
            CharacterState(
                speechTexts = listOf(
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.userDepartureSubwayText,
                        suffixText = "",
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = texts.userDepartureDownText,
                        emphasisText = texts.userDepartureDetailBtnText,
                        suffixText = texts.userDepartureCheckDetailText,
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.trustText1 ,
                        suffixText = texts.trustText2,
                        lineCount = 1
                    )
                ),
                lottieResId = R.raw.atcha_character_4, // TODO : atcha_character_5 로 변경
                bottomPadding = 218.dp
            )
        }

        // 알림 등록 후 & 사용자 출발 후 & 타이머 종료
        homeUiState.timerFinish -> {
         CharacterState(

         )
        }

        else -> {
            CharacterState(
                speechTexts = listOf(
                    SpeechBubbleData(
                        prefixText = texts.taxiCostText,
                        emphasisText = texts.aboutText + NumberFormat.getNumberInstance(Locale.US)
                            .format(homeUiState.taxiCost) + texts.wonText,
                        suffixText = null,
                        lineCount = 1
                    )
                ),
                lottieResId = R.raw.atcha_character_2,
                bottomPadding = 194.dp
            )
        }
    }
}

data class CharacterTexts(
    // 알림 등록 전
    val taxiCostText: String,
    val aboutText: String,
    val wonText: String,
    val moveMapText: String,

    // 알림 등록 후
    // 사용자 출발 전 & 차고지 출발 전 (예상 출발 시간)
    val expectDepartText: String,
    val expectTaxiCostText: String,
    val expectBustDepartureText: String,

    val expectTimeClickedText1: String,
    val expectTimeClickedText2: String,
    val changeLocationClickedText: String,

    // 사용자 출발 전 & 차고지 출발 후 (출발 시간)
    val busDepartureText1: String,
    val busDepartureText2: String,
    val subwayDepartureText1: String,
    val subwayDepartureText2: String,
    val timeInfoText1: String,
    val timeInfoText2: String,
    val busDepartureTaxiCostText: String,
    val trustText1: String,
    val trustText2: String,

    // 사용자 출발 후
    val userDepartureBusText: String,
    val userDepartureSubwayText: String,
    val userDepartureDownText: String,
    val userDepartureDetailBtnText: String,
    val userDepartureCheckDetailText: String,
)

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        padding = PaddingValues(0.dp),
        userLocation = LatLng(37.5665, 126.9780)
    )
}
