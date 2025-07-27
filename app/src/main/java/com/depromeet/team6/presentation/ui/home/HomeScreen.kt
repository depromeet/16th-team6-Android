package com.depromeet.team6.presentation.ui.home

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import com.depromeet.team6.presentation.model.home.ComponentType
import com.depromeet.team6.presentation.model.home.SpeechBubbleData
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterMap
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterSheet
import com.depromeet.team6.presentation.ui.home.component.CurrentLocationSheet
import com.depromeet.team6.presentation.ui.home.component.DeleteAlarmDialog
import com.depromeet.team6.presentation.ui.home.component.HomeGreetBottomSheet
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
import com.depromeet.team6.presentation.util.base.ApiErrorSideEffect
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.toast.atChaToastMessage
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.Team6Theme
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeRoute(
    padding: PaddingValues,
    afterOnboarding: Boolean = false,
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
        departureTimeText1 = stringResource(R.string.home_bubble_departure_time_info_text_1),
        busDepartureTaxiCostText = stringResource(R.string.home_bubble_departed_taxi_cost_text),
        trustText1 = stringResource(R.string.home_bubble_trust_text_1),
        trustText2 = stringResource(R.string.home_bubble_trust_text_2),

        userDepartureBusText = stringResource(R.string.home_bubble_user_departure_bus_text),
        userDepartureSubwayText = stringResource(R.string.home_bubble_user_departure_subway_text),
        userDepartureDownText = stringResource(R.string.home_bubble_user_departure_down_text),
        userDepartureDetailBtnText = stringResource(R.string.home_bubble_user_departure_detail_btn_text),
        userDepartureCheckDetailText = stringResource(R.string.home_bubble_user_departure_check_detail_text)
    )

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadAlarmAndCourseInfoFromPrefs()
        viewModel.loadUserDepartureState()
        viewModel.setEvent(HomeContract.HomeEvent.ChangeGreetBottomSheetVisible(afterOnboarding))
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

//    LaunchedEffect(uiState.isAlarmRegistered, uiState.firtTransportTation) {
//        if (uiState.isAlarmRegistered && uiState.firtTransportTation == TransportType.BUS) {
//            viewModel.startPollingBusStarted(routeId = uiState.lastRouteId)
//        } else {
//            viewModel.stopPollingBusStarted()
//        }
//    }

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

    // 애니메이션, 말풍선
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val currentCount = prefs.getInt("app_launch_count", 0)
        val newCount = currentCount + 1

        prefs.edit().putInt("app_launch_count", newCount).apply()
    }

    var previousConditionKey by remember { mutableStateOf("") }

    val baseCharacterData = remember(
        uiState.isAlarmRegistered,
        uiState.userDeparture,
        uiState.isBusDeparted,
        uiState.taxiCost,
        characterTexts
    ) {
        generateCharacterStateWithLaunchCount(uiState, characterTexts, context)
    }

    val conditionKey = remember(
        uiState.isAlarmRegistered,
        uiState.userDeparture,
        uiState.isBusDeparted
    ) {
        "${uiState.isAlarmRegistered}_${uiState.userDeparture}_${uiState.isBusDeparted}"
    }

    var currentSpeechIndex by remember(conditionKey) { mutableStateOf(0) }
    var animationTrigger by remember { mutableStateOf(0) }

    val safeCurrentIndex = if (baseCharacterData.speechTexts.isNotEmpty()) {
        currentSpeechIndex.coerceIn(0, baseCharacterData.speechTexts.size - 1)
    } else {
        0
    }

    var tempSpeechBubble by remember { mutableStateOf<SpeechBubbleData?>(null) }
    var isShowingTempMessage by remember { mutableStateOf(false) }
    var hideBubbleAfterComponentClick by remember { mutableStateOf(false) }

    var hideDefaultBubbleAfterFirstMessage by remember { mutableStateOf(false) }
    var isShowingFirstTimeMessage by remember { mutableStateOf(false) }

    val currentConditionKey = remember(
        uiState.isAlarmRegistered,
        uiState.userDeparture,
        uiState.isBusDeparted,
        uiState.firtTransportTation
    ) {
        when {
            uiState.isAlarmRegistered && !uiState.userDeparture && !uiState.isBusDeparted -> {
                "before_bus_arrived"
            }
            uiState.isAlarmRegistered && !uiState.userDeparture -> {
                if (uiState.firtTransportTation == TransportType.SUBWAY) {
                    "after_subway_arrived"
                } else {
                    "after_bus_arrived"
                }
            }
            uiState.isAlarmRegistered && !uiState.timerFinish && uiState.firtTransportTation == TransportType.BUS ->
                "after_user_departure_bus"
            uiState.isAlarmRegistered && !uiState.timerFinish && uiState.firtTransportTation == TransportType.SUBWAY ->
                "after_user_departure_subway"
            else -> "none"
        }
    }

    LaunchedEffect(currentConditionKey) {
        val prefs = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        if (previousConditionKey.isNotEmpty() &&
            previousConditionKey != currentConditionKey &&
            previousConditionKey != "none"
        ) {
            val previousPrefsKey = "first_$previousConditionKey"
            prefs.edit().remove(previousPrefsKey).apply()
        }

        previousConditionKey = currentConditionKey

        if (currentConditionKey == "none") {
            hideDefaultBubbleAfterFirstMessage = false
            return@LaunchedEffect
        }

        val prefsKey = "first_$currentConditionKey"

        if (!prefs.getBoolean(prefsKey, false)) {
            hideDefaultBubbleAfterFirstMessage = true
        }

        if (prefs.getBoolean(prefsKey, false)) {
            return@LaunchedEffect
        }

        val speechBubble = when (currentConditionKey) {
            "before_bus_arrived" -> SpeechBubbleData(
                prefixText = "",
                emphasisText = characterTexts.subwayDepartureText1,
                suffixText = "",
                lineCount = 1
            )

            "after_bus_arrived" -> SpeechBubbleData(
                prefixText = "",
                emphasisText = characterTexts.busDepartureText2,
                suffixText = "",
                topEmphasisText = characterTexts.busDepartureText1,
                lineCount = 2
            )

            "after_subway_arrived" -> SpeechBubbleData(
                prefixText = "",
                emphasisText = characterTexts.subwayDepartureText2,
                suffixText = "",
                topEmphasisText = characterTexts.subwayDepartureText1,
                lineCount = 2
            )

            "after_user_departure_bus" -> SpeechBubbleData(
                prefixText = "",
                emphasisText = characterTexts.userDepartureBusText,
                suffixText = "",
                lineCount = 1
            )

            "after_user_departure_subway" -> SpeechBubbleData(
                prefixText = "",
                emphasisText = characterTexts.userDepartureSubwayText,
                suffixText = "",
                lineCount = 1
            )

            else -> return@LaunchedEffect
        }

        while (isShowingTempMessage) {
            delay(100)
        }

        delay(100)

        isShowingTempMessage = true
        isShowingFirstTimeMessage = true
        tempSpeechBubble = speechBubble

        delay(if (speechBubble.lineCount == 2) 4000 else 3200)

        tempSpeechBubble = null
        isShowingTempMessage = false
        isShowingFirstTimeMessage = false

        prefs.edit().putBoolean(prefsKey, true).apply()
    }

    fun showTempMessage(componentType: ComponentType) {
        val speechBubble = when (componentType) {
            ComponentType.DEPARTURE_TIME_NOT_CONFIRMED_CLICKED -> SpeechBubbleData(
                prefixText = "",
                emphasisText = characterTexts.expectTimeClickedText2,
                suffixText = "",
                topEmphasisText = characterTexts.expectTimeClickedText1,
                lineCount = 2
            )

            ComponentType.DEPARTURE_TIME_CONFIRMED_CLICKED -> SpeechBubbleData(
                prefixText = "",
                emphasisText = characterTexts.timeInfoText1,
                suffixText = "",
                topEmphasisText = characterTexts.departureTimeText1,
                lineCount = 2
            )

            ComponentType.ROUTE_TEXT_CLICKED -> SpeechBubbleData(
                prefixText = "",
                emphasisText = characterTexts.changeLocationClickedText,
                suffixText = "",
                lineCount = 1
            )
        }

        isShowingTempMessage = true
        tempSpeechBubble = speechBubble
        animationTrigger++

        CoroutineScope(Dispatchers.Main).launch {
            if (speechBubble.lineCount == 2) {
                delay(4000)
            } else {
                delay(3200)
            }
            tempSpeechBubble = null
            isShowingTempMessage = false
            hideBubbleAfterComponentClick = true
        }
    }

    val finalSpeechTexts = when {
        tempSpeechBubble != null -> listOf(tempSpeechBubble!!)
        hideBubbleAfterComponentClick -> emptyList()
        hideDefaultBubbleAfterFirstMessage -> emptyList()
        isShowingFirstTimeMessage -> emptyList()
        else -> baseCharacterData.speechTexts
    }

    val characterState = CharacterState(
        speechTexts = finalSpeechTexts,
        lottieResId = baseCharacterData.lottieResId,
        bottomPadding = baseCharacterData.bottomPadding,
        currentSpeechIndex = if (tempSpeechBubble != null) 0 else safeCurrentIndex,
        animationTrigger = animationTrigger,
        isAnimating = true
    )

    // 캐릭터 클릭 핸들러
    val onCharacterClick = {
        viewModel.setEvent(HomeContract.HomeEvent.OnCharacterClick)
        when {
            isShowingFirstTimeMessage -> {
                // 첫 번째 메시지 표시 중이면 아무것도 하지 않음
            }
            hideBubbleAfterComponentClick -> {
                hideBubbleAfterComponentClick = false
                animationTrigger += 1
            }
            hideDefaultBubbleAfterFirstMessage -> {
                hideDefaultBubbleAfterFirstMessage = false
                animationTrigger += 1
            }
            tempSpeechBubble == null && characterState.speechTexts.size > 1 -> {
                currentSpeechIndex = (currentSpeechIndex + 1) % characterState.speechTexts.size
                animationTrigger += 1
            }
            tempSpeechBubble == null -> {
                animationTrigger += 1
            }
        }
    }

    when (uiState.loadState) {
        LoadState.Idle, LoadState.Loading, LoadState.Success -> {
            Box {
                HomeScreen(
                    userLocation = LatLng(userLocation.latitude, userLocation.longitude),
                    homeUiState = uiState,
                    getUserId = { viewModel.getUserId() },
                    getCenterLocation = { position ->
                        viewModel.getCenterLocation(position)
                    },
                    updateCurrentLocation = { newLocation ->
                        viewModel.updateCurrentLocation(newLocation)
                    },
                    onTimerFinished = { viewModel.onTimerFinished() },
                    getDepartureTime = { viewModel.loadDepartureTime() },
                    onCharacterClick = onCharacterClick,
                    characterState = characterState,
                    showTempMessage = ::showTempMessage,
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
                            eventName = HOME_COURSESEARCH_ENTERED_WITH_INPUT,
                            mapOf(
                                USER_ID to viewModel.getUserId(),
                                SCREEN_NAME to HOME,
                                HOME_COURSESEARCH_ENTERED_WITH_INPUT to 1
                            )
                        )
                    },
                    greetBottomSheetButtonClicked = {
                        viewModel.setEvent(
                            HomeContract.HomeEvent.ChangeGreetBottomSheetVisible(
                                false
                            )
                        )
                    }
                )

                if (uiState.loadState == LoadState.Loading ||
                    uiState.alarmCheckLoadState == LoadState.Loading ||
                    (uiState.isAlarmRegistered && uiState.afterRegisterDataLoadState == LoadState.Loading)
                ) {
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
    getUserId: () -> Int,
    getCenterLocation: (LatLng) -> Unit = {},
    updateCurrentLocation: (LatLng) -> Unit = {},
    onTimerFinished: () -> Unit = {},
    getDepartureTime: () -> Unit = {},
    onCharacterClick: () -> Unit = {},
    characterState: CharacterState,
    showTempMessage: (ComponentType) -> Unit = {},
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
    greetBottomSheetButtonClicked: () -> Unit = {}
) {
    val colors = LocalTeam6Colors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.black)
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            )
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_home_mypage),
            contentDescription = stringResource(R.string.mypage_icon_description),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 16.dp)
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
                isAlarmRegistered = homeUiState.isAlarmRegistered,
                getCenterLocation = {
                    getCenterLocation(it)
                },
                updateCurrentLocation = {
                    updateCurrentLocation(it)
                },
                onTransportMarkerClick = { markerParameter ->
                    afterRegisterMapMarkerClick(markerParameter)
                }
            )
        } else {
            TMapViewCompose(
                padding,
                userLocation,
                isAlarmRegistered = homeUiState.isAlarmRegistered,
                userId = getUserId(),
                getCenterLocation = {
                    getCenterLocation(it)
                }
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

        when {
            homeUiState.alarmCheckLoadState == LoadState.Loading ||
                (homeUiState.isAlarmRegistered && homeUiState.afterRegisterDataLoadState == LoadState.Loading) -> {
            }

            // 알림이 등록된 경우
            homeUiState.isAlarmRegistered && homeUiState.afterRegisterDataLoadState == LoadState.Success -> {
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
                        showTempMessage(ComponentType.ROUTE_TEXT_CLICKED)
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
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f),
                    onRefreshClick = {
                        onRefreshClick()
                        if (homeUiState.firtTransportTation == TransportType.BUS) {
                            getDepartureTime()
                        }
                    },
                    onIconClick = {
                        if (homeUiState.isBusDeparted) {
                            showTempMessage(ComponentType.DEPARTURE_TIME_CONFIRMED_CLICKED)
                        } else {
                            showTempMessage(ComponentType.DEPARTURE_TIME_NOT_CONFIRMED_CLICKED)
                        }
                    },
                    onHomeDepartureTimeClick = {
                        showTempMessage(ComponentType.DEPARTURE_TIME_CONFIRMED_CLICKED)
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
                        showTempMessage(ComponentType.DEPARTURE_TIME_NOT_CONFIRMED_CLICKED)
                        AmplitudeUtils.trackEventWithProperties(
                            HOME_DEPARTURE_TIME_CLICKED,
                            mapOf(
                                SCREEN_NAME to HOME,
                                USER_ID to getUserId(),
                                HOME_DEPARTURE_TIME_SUGGESTION_CLICKED to 1
                            )
                        )
                    },
                    busStationLeft = homeUiState.busRemainingStations
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
                        .fillMaxWidth()
                        .zIndex(1f)
                )
            }
        }

        UnifiedCharacterBubble(
            characterState = characterState,
            onCharacterClick = onCharacterClick,
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
                                USER_ID to getUserId(),
                                ALERT_END_POPUP_1 to 1
                            )
                        )
                    },
                    sortType = 1
                )
            }
        }
        if (homeUiState.greetBottomSheetVisible) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = Team6Theme.colors.black.copy(alpha = 0.5f))
                    .zIndex(Float.MAX_VALUE)
            ) {
                HomeGreetBottomSheet(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    buttonClicked = { greetBottomSheetButtonClicked() }
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
        userLocation = LatLng(37.5665, 126.9780),
        getUserId = { 1 },
        characterState = CharacterState()
    )
}
