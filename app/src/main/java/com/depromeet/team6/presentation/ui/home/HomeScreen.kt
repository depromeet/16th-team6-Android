package com.depromeet.team6.presentation.ui.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.data.background.ArrivalMonitorService
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.model.home.CharacterState
import com.depromeet.team6.presentation.model.home.MapFocusState
import com.depromeet.team6.presentation.model.home.SpeechBubbleData
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.common.speechbubble.AtchaSpeechCharacter
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterMap
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterSheet
import com.depromeet.team6.presentation.ui.home.component.CurrentLocationSheet
import com.depromeet.team6.presentation.ui.home.component.DeleteAlarmDialog
import com.depromeet.team6.presentation.ui.home.component.TMapViewCompose
import com.depromeet.team6.presentation.ui.main.MainViewModel
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.AppConstants
import com.depromeet.team6.presentation.util.HomeAmplitude.ALERT_END_POPUP_1
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_COURSESEARCH_ENTERED_DIRECT
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_COURSESEARCH_ENTERED_WITH_INPUT
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_DEPARTURE_TIME_CLICKED
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_DEPARTURE_TIME_SUGGESTION_CLICKED
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_DESTINATION_CLICKED
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_COURSESEARCH_ENTERED
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_ROUTE_CLICKED
import com.depromeet.team6.presentation.util.HomeAmplitude.POPUP
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.base.ApiErrorSideEffect
import com.depromeet.team6.presentation.util.dialog.LocalDialogController
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.toast.atChaToastMessage
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

@Composable
fun HomeRoute(
    padding: PaddingValues,
    navigateToLogin: () -> Unit,
    navigateToCourseSearch: (String, String) -> Unit,
    navigateToMypage: () -> Unit,
    navigateToItinerary: (String, String, String, FocusedMarkerParameter?) -> Unit,
    navigateToSearchLocation: (Address) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    afterOnboarding: Boolean = false
) {
    val mainViewModel: MainViewModel = hiltViewModel(LocalActivity.current as ComponentActivity)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLocation by mainViewModel.currentLocation.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()
    val dialogController = LocalDialogController.current

    // LockScreen에서 출발하기 클릭 시 처리
    LaunchedEffect(uiState.isAlarmRegistered && uiState.afterRegisterDataLoadState == LoadState.Success) {
        if (uiState.isAlarmRegistered && uiState.afterRegisterDataLoadState == LoadState.Success) {
            val sharedPreferences = context.getSharedPreferences("MyPreferences", android.content.Context.MODE_PRIVATE)
            val fromLockScreenDeparture = sharedPreferences.getBoolean("fromLockScreenDeparture", false)

            if (fromLockScreenDeparture) {
                // 플래그 초기화
                sharedPreferences.edit().putBoolean("fromLockScreenDeparture", false).apply()

                // 상세경로 화면으로 이동
                navigateToItinerary(
                    Gson().toJson(uiState.itineraryInfo),
                    Gson().toJson(uiState.departurePoint),
                    Gson().toJson(uiState.destinationPoint),
                    null
                )
            }
        }
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
    }

    // 화면이 다시 활성화될 때마다 사용자 출발 상태를 새로 로드
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                // 화면이 다시 보일 때마다 사용자 출발 상태 로드
                viewModel.loadUserDepartureState()
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
                    is ApiErrorSideEffect.ShowToastSideEffect -> {
                        Toast.makeText(context, sideEffect.toastMessage, Toast.LENGTH_SHORT).show()
                    }

                    is ApiErrorSideEffect.NavigateToLoginSideEffect -> navigateToLogin()
                    is HomeContract.HomeSideEffect.NavigateToMypage -> navigateToMypage()
                    is HomeContract.HomeSideEffect.NavigateToItinerary -> navigateToItinerary(
                        Gson().toJson(uiState.itineraryInfo),
                        Gson().toJson(uiState.departurePoint),
                        Gson().toJson(uiState.destinationPoint),
                        sideEffect.markerParameter
                    )

                    is HomeContract.HomeSideEffect.ShowDeleteAlarmToast ->
                        atChaToastMessage(context, R.string.home_alarm_finish_text, Toast.LENGTH_SHORT)

                    is HomeContract.HomeSideEffect.ShowUpdateRequiredDialog -> {
                        dialogController.showAtchaOneButtonAlert(
                            message = "더 좋아진 앗차를 사용하기 위해\n업데이트가 필요해요",
                            onConfirm = { openPlayStoreForUpdate(context = context) },
                            confirmButtonText = "업데이트 하기"
                        )
                    }

                    is HomeContract.HomeSideEffect.ShowUpdateOptionalDialog -> {
                        dialogController.showAtchaTwoButtonAlert(
                            message = "더 좋아진 앗차를 사용하기 위해\n업데이트가 필요해요",
                            onConfirm = { openPlayStoreForUpdate(context = context) },
                            confirmButtonText = "업데이트 하기",
                            closeButtonText = "닫기"
                        )
                    }

                    is HomeContract.HomeSideEffect.ShowTooCloseDialog -> {
                        dialogController.showAtchaBottomSheet(
                            locationName = "이동하려는 거리가 매우 가까워요",
                            locationAddress = "출발지를 확인한 후 다시 검색해 주세요.",
                            confirmButtonText = "확인"
                        )
                    }

                    is HomeContract.HomeSideEffect.ShowOutOfServiceRegionBottomSheet -> {
                        dialogController.showAtchaBottomSheet(
                            locationName = "출발지가 서비스 지역을 벗어났어요",
                            locationAddress = "출발지가 수도권을 벗어났습니다. 출발지를 다시 선택해 주세요.",
                            confirmButtonText = "확인"
                        )
                    }

                    is HomeContract.HomeSideEffect.NavigateToCourseSearch -> {
                        navigateToCourseSearch(
                            Gson().toJson(uiState.markerPoint),
                            Gson().toJson(uiState.destinationPoint)
                        )
                        AmplitudeUtils.trackEventWithProperties(
                            eventName = HOME_EVENT_COURSESEARCH_ENTERED,
                            mapOf(
                                USER_ID to viewModel.getUserId(),
                                SCREEN_NAME to HOME,
                                HOME_COURSESEARCH_ENTERED_DIRECT to 1
                            )
                        )
                    }
                }
            }
    }

    LaunchedEffect(Unit) {
        if (uiState.isAlarmRegistered) {
            viewModel.registerAlarm()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setEvent(HomeContract.HomeEvent.SetDestination)
    }

    // 현재 화면이 사라질 때 isMapReady 상태 초기화
    DisposableEffect(Unit) {
        onDispose {
            viewModel.setState {
                copy(
                    isMapReady = false
                )
            }
        }
    }

    when (uiState.loadState) {
        LoadState.Idle, LoadState.Loading -> {
            AtChaLoadingView()
        }

        LoadState.Success -> {
            Box {
                if (uiState.alarmCheckLoadState == LoadState.Loading ||
                    (uiState.isAlarmRegistered && uiState.afterRegisterDataLoadState == LoadState.Loading)
                ) {
                    AtChaLoadingView()
                } else {
                    HomeScreen(
                        homeUiState = uiState,
                        currentLocation = currentLocation,
                        getUserId = { viewModel.getUserId() },
                        getCenterLocation = { position ->
                            viewModel.getCenterLocation(position)
                        },
                        onTimerFinished = { viewModel.onTimerFinished() },
                        getDepartureTime = { viewModel.loadDepartureTime() },
                        onCharacterClick = { viewModel.setEvent(HomeContract.HomeEvent.OnCharacterClick) },
//                        characterState = characterState,
//                        showTempMessage = ::showTempMessage,
                        requestCharacterSpeech = { messages ->
                            viewModel.setEvent(HomeContract.HomeEvent.RequestCharacterSpeech(messages))
                        },
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
                            viewModel.setEvent(HomeContract.HomeEvent.OnSearchClick)
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
                            viewModel.deleteAlarm(uiState.lastRouteId)
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
                                eventName = HOME_EVENT_COURSESEARCH_ENTERED,
                                mapOf(
                                    USER_ID to viewModel.getUserId(),
                                    SCREEN_NAME to HOME,
                                    HOME_COURSESEARCH_ENTERED_WITH_INPUT to 1
                                )
                            )
                        },
                        currentLocationClicked = {
                            viewModel.setState {
                                copy(
                                    isMapFocused = MapFocusState.Current
                                )
                            }
                        },
                        mapModified = {
                            viewModel.setState {
                                copy(
                                    isMapFocused = MapFocusState.Modify
                                )
                            }
                        },
                        isMapReadyCallback = {
                            viewModel.setState {
                                copy(
                                    isMapReady = true
                                )
                            }
                        }
                    )
                }
            }
        }

        LoadState.Error -> navigateToLogin()
    }
}

@Composable
fun HomeScreen(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    homeUiState: HomeContract.HomeUiState = HomeContract.HomeUiState(),
    currentLocation: LatLng,
    getUserId: () -> Int,
    getCenterLocation: (LatLng) -> Unit = {},
    onTimerFinished: () -> Unit = {},
    getDepartureTime: () -> Unit = {},
    onCharacterClick: () -> Unit = {},
//    characterState: CharacterState,
    requestCharacterSpeech: (List<String>) -> Unit = {},
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
    currentLocationClicked: () -> Unit = {},
    mapModified: () -> Unit = {},
    isMapReadyCallback: () -> Unit = {}
) {
    val colors = LocalTeam6Colors.current
    var bottomSheetHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.black)
            .padding(
                bottom = padding.calculateBottomPadding()
            )
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_home_mypage),
            contentDescription = stringResource(R.string.mypage_icon_description),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 16.dp)
                .offset(y = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .clickable {
                    navigateToMypage()
                }
                .zIndex(1f)
        )

        if (homeUiState.isAlarmRegistered) {
            val context = LocalContext.current
            val destination = homeUiState.destinationPoint

            Intent(context, ArrivalMonitorService::class.java).apply {
                putExtra(ArrivalMonitorService.EXTRA_DEST_LAT, destination.lat)
                putExtra(ArrivalMonitorService.EXTRA_DEST_LNG, destination.lon)
            }.also {
                ContextCompat.startForegroundService(context, it)
            }

            AfterRegisterMap(
                padding = padding,
                currentLocation = currentLocation,
                legs = homeUiState.itineraryInfo!!.legs,
                isAlarmRegistered = homeUiState.isAlarmRegistered,
                isMapFocused = homeUiState.isMapFocused,
                initialMapFocus = MapFocusState.Departure,
                mapModified = mapModified,
                getCenterLocation = {
                    getCenterLocation(it)
                },
                onTransportMarkerClick = { markerParameter ->
                    afterRegisterMapMarkerClick(markerParameter)
                },
                isMapReadyCallback = isMapReadyCallback
            )
        } else {
            TMapViewCompose(
                padding = padding,
                currentLocation = currentLocation,
                isAlarmRegistered = homeUiState.isAlarmRegistered,
                userId = getUserId(),
                isMapFocused = homeUiState.isMapFocused,
                getCenterLocation = {
                    getCenterLocation(it)
                },
                mapModified = mapModified,
                isMapReadyCallback = isMapReadyCallback
            ) // Replace with your actual API key
        }

        when {
            homeUiState.alarmCheckLoadState == LoadState.Loading ||
                (homeUiState.isAlarmRegistered && homeUiState.afterRegisterDataLoadState == LoadState.Loading) -> {
            }

            // 알림이 등록된 경우
            homeUiState.isAlarmRegistered && homeUiState.afterRegisterDataLoadState == LoadState.Success -> {
                AfterRegisterSheet(
                    timerFinish = homeUiState.timerFinish,
                    startLocation = homeUiState.departurePointName,
//                    isConfirmed = isConfirmed,
                    afterUserDeparted = homeUiState.userDeparture,
                    transportType = homeUiState.firtTransportTation,
                    transportationNumber = homeUiState.firstTransportationNumber,
                    transportationName = homeUiState.firstTransportationName,
                    timeToLeave = formatTimeString(homeUiState.departureTime),
                    boardingTime = homeUiState.boardingTime,
                    homeArrivedTime = homeUiState.homeArrivedTime,
                    destination = stringResource(R.string.home_my_home_text),
                    onCourseTextClick = {
                        requestCharacterSpeech(
                            listOf(
                                "위치를 변경하려면 알림을 종료해야 해요"
                            )
                        )
                        AmplitudeUtils.trackEventWithProperties(
                            HOME_ROUTE_CLICKED,
                            mapOf(
                                SCREEN_NAME to HOME,
                                USER_ID to getUserId(),
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
                        onTimerFinished()
                    },

                    onRefreshClick = {
                        onRefreshClick()
                        if (homeUiState.firtTransportTation == TransportType.BUS) {
                            getDepartureTime()
                        }
                    },
                    onHomeDepartureTimeClick = {
//                        showTempMessage(ComponentType.DEPARTURE_TIME_CONFIRMED_CLICKED)
                        requestCharacterSpeech(
                            listOf(
                                "이때 자리에서 출발하면 돼요",
                                "교통 상황에 따라 시간이 달라질 수 있어요"
                            )
                        )
                        AmplitudeUtils.trackEventWithProperties(
                            HOME_DEPARTURE_TIME_CLICKED,
                            mapOf(
                                SCREEN_NAME to HOME,
                                USER_ID to getUserId(),
                                HOME_DEPARTURE_TIME_CLICKED to 1
                            )
                        )
                    },
                    onHomeExpectDepartureTimeClick = {
                        AmplitudeUtils.trackEventWithProperties(
                            HOME_DEPARTURE_TIME_CLICKED,
                            mapOf(
                                SCREEN_NAME to HOME,
                                USER_ID to getUserId(),
                                HOME_DEPARTURE_TIME_SUGGESTION_CLICKED to 1
                            )
                        )
                    },
                    busStationLeft = homeUiState.busRemainingStations,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                        .onGloballyPositioned { coordinates ->
                            // coordinates.size.height는 픽셀 단위이므로 dp로 변환해야 합니다.
                            bottomSheetHeight = with(density) { coordinates.size.height.toDp() }
                        }
                )
            }

            // 알림이 등록되지 않은 경우
            !homeUiState.isAlarmRegistered && homeUiState.alarmCheckLoadState == LoadState.Success -> {
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
                        .zIndex(1f)
                        .onGloballyPositioned { coordinates ->
                            // coordinates.size.height는 픽셀 단위이므로 dp로 변환해야 합니다.
                            bottomSheetHeight = with(density) { coordinates.size.height.toDp() }
                        }
                )
            }
        }

        Timber.d("isMapReady : ${homeUiState.isMapReady}")
        if (homeUiState.isMapReady) {
            val bottomOffset = if (homeUiState.isAlarmRegistered) {
                224.dp
            } else {
                194.dp
            }
            AtchaSpeechCharacter(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = bottomOffset),
                speechRequest = homeUiState.characterMessages,
                onCharacterClick = onCharacterClick
            )
        }

        // 현위치 버튼
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_current_location),
            contentDescription = stringResource(R.string.home_current_location_btn),
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = bottomSheetHeight + 16.dp,
                    end = 16.dp
                )
                .noRippleClickable(onClick = currentLocationClicked)
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
                                USER_ID to getUserId(),
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

private fun generateCharacterStateWithLaunchCount(
    homeUiState: HomeContract.HomeUiState,
    texts: CharacterTexts,
    context: Context
): CharacterState {
    val prefs = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val launchCount = prefs.getInt("app_launch_count", 0)

    return when {
        // 알림 등록 전
        !homeUiState.isAlarmRegistered -> {
            if (launchCount <= 2) {
                CharacterState(
                    speechTexts = listOf(
                        SpeechBubbleData(
                            prefixText = texts.taxiCostText,
                            emphasisText = texts.aboutText + NumberFormat.getNumberInstance(Locale.US)
                                .format(homeUiState.taxiCost) + texts.wonText,
                            suffixText = null,
                            topPrefixText = texts.moveMapText,
                            lineCount = 2
                        )
                    ),
                    lottieResId = R.raw.atcha_character_1,
                    bottomPadding = 194.dp
                )
            } else {
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
                    lottieResId = R.raw.atcha_character_1,
                    bottomPadding = 194.dp
                )
            }
        }

        // 알림 등록 후 & 사용자 출발 전 & 차고지 출발 전
        homeUiState.isAlarmRegistered && !homeUiState.userDeparture && !homeUiState.isBusDeparted -> {
            CharacterState(
                speechTexts = listOf(
                    SpeechBubbleData(
                        prefixText = texts.expectTaxiCostText,
                        emphasisText = texts.aboutText + NumberFormat.getNumberInstance(Locale.US)
                            .format(homeUiState.taxiCost) + texts.wonText,
                        suffixText = null,
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.trustText1,
                        suffixText = texts.trustText2,
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.expectBustDepartureText,
                        suffixText = "",
                        lineCount = 1
                    )
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
                        emphasisText = texts.trustText1,
                        suffixText = texts.trustText2,
                        lineCount = 1
                    )
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
                        prefixText = texts.userDepartureDownText,
                        emphasisText = texts.userDepartureDetailBtnText,
                        suffixText = texts.userDepartureCheckDetailText,
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.trustText1,
                        suffixText = texts.trustText2,
                        lineCount = 1
                    )
                ),
                lottieResId = R.raw.atcha_character_5,
                bottomPadding = 218.dp
            )
        }

        // 알림 등록 후 & 사용자 출발 후 & 타이머 종료 전 & 지하철
        !homeUiState.timerFinish && homeUiState.firtTransportTation == TransportType.SUBWAY -> {
            CharacterState(
                speechTexts = listOf(
                    SpeechBubbleData(
                        prefixText = texts.userDepartureDownText,
                        emphasisText = texts.userDepartureDetailBtnText,
                        suffixText = texts.userDepartureCheckDetailText,
                        lineCount = 1
                    ),
                    SpeechBubbleData(
                        prefixText = "",
                        emphasisText = texts.trustText1,
                        suffixText = texts.trustText2,
                        lineCount = 1
                    )
                ),
                lottieResId = R.raw.atcha_character_5,
                bottomPadding = 218.dp
            )
        }

        // 알림 등록 후 & 사용자 출발 후 & 타이머 종료
        homeUiState.timerFinish -> {
            CharacterState()
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

private fun openPlayStoreForUpdate(context: Context) {
    val packageName = AppConstants.PLAY_STORE_PACKAGE_NAME

    try {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=$packageName".toUri()
        ).apply {
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            AppConstants.PLAY_STORE_URL.toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(webIntent)
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
    val departureTimeText1: String,
    val busDepartureTaxiCostText: String,
    val trustText1: String,
    val trustText2: String,

    // 사용자 출발 후
    val userDepartureBusText: String,
    val userDepartureSubwayText: String,
    val userDepartureDownText: String,
    val userDepartureDetailBtnText: String,
    val userDepartureCheckDetailText: String
)

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        padding = PaddingValues(0.dp),
        currentLocation = LatLng(0.0, 0.0),
        getUserId = { 1 }
    )
}
