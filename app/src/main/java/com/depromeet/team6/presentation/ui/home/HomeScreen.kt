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
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.alarm.NotificationScheduler
import com.depromeet.team6.presentation.ui.alarm.NotificationTimeConstants
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterMap
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterSheet
import com.depromeet.team6.presentation.ui.home.component.CharacterLottieSpeechBubble
import com.depromeet.team6.presentation.ui.home.component.CurrentLocationSheet
import com.depromeet.team6.presentation.ui.home.component.DeleteAlarmDialog
import com.depromeet.team6.presentation.ui.home.component.TMapViewCompose
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNT
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeRoute(
    padding: PaddingValues,
    navigateToLogin: () -> Unit,
    navigateToCourseSearch: (String, String) -> Unit,
    navigateToMypage: () -> Unit,
    navigateToItinerary: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var permissionGranted by remember { mutableStateOf(PermissionUtil.hasLocationPermissions(context)) }
    var userLocation by remember { mutableStateOf(LatLng(DEFAULT_LNT, DEFAULT_LNG)) } // 서울시 기본 위치

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionGranted = permissions.values.all { it }
            if (permissionGranted) {
                Timber.d("Location_Permission Has Granted")
            }
        }
    )

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
                        Gson().toJson(uiState.courseInfo)
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

    when (uiState.loadState) {
        LoadState.Idle -> HomeScreen(
            userLocation = LatLng(userLocation.latitude, userLocation.longitude),
            homeUiState = uiState,
            onCharacterClick = { viewModel.onCharacterClick() },
            navigateToMypage = navigateToMypage,
            navigateToItinerary = navigateToItinerary,
            modifier = modifier,
            padding = padding,
            onSearchClick = {
                val departurePointJSON = Gson().toJson(uiState.departurePoint)
                val destinationPointJSON = Gson().toJson(uiState.destinationPoint)
                navigateToCourseSearch(
                    departurePointJSON,
                    destinationPointJSON
                )
            },
            onFinishClick = {
                viewModel.setEvent(HomeContract.HomeEvent.FinishAlarmClicked)
            },
            deleteAlarmConfirmed = {
                viewModel.setEvent(HomeContract.HomeEvent.DeleteAlarmConfirmed)
                viewModel.deleteAlarm(uiState.lastRouteId, context)
            },
            dismissDialog = {
                viewModel.setEvent(HomeContract.HomeEvent.DismissDialog)
            },
            onRefreshClick = {
            }
        )
        LoadState.Error -> navigateToLogin()

        else -> Unit
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
    onFinishClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    navigateToMypage: () -> Unit = {},
    navigateToItinerary: (String) -> Unit = {},
    deleteAlarmConfirmed: () -> Unit = {},
    dismissDialog: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel() // TODO : TmapViewCompose 변경 후 제거
) {
    val context = LocalContext.current

    val notificationScheduler = NotificationScheduler(context)

    Box(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
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
                currentLocation = userLocation,
                legs = homeUiState.itineraryInfo!!.legs,
                viewModel = viewModel
            )
        } else {
            TMapViewCompose(
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
            notificationScheduler.scheduleNotificationForTime(
                stringResource(R.string.app_name),
                stringResource(R.string.notification_content_text),
                NotificationTimeConstants.getDepartureTimeWithTodayDate()
            )

            AfterRegisterSheet(
                timerFinish = homeUiState.timerFinish,
                startLocation = homeUiState.departurePointName,
                isConfirmed = isConfirmed,
                afterUserDeparted = homeUiState.userDeparture,
                transportType = homeUiState.firtTransportTation,
                transportationNumber = homeUiState.firstTransportationNumber,
                transportationName = homeUiState.firstTransportationName,
                timeToLeave = formatTimeString(homeUiState.departureTime),
                boardingTime = formatTimeString(homeUiState.boardingTime),
                destination = "우리집",
                onCourseTextClick = {},
                deleteAlarmConfirmed = deleteAlarmConfirmed,
                dismissDialog = dismissDialog,
                onFinishClick = {
                    onFinishClick()
                },
                onCourseDetailClick = {
                    val courseInfoJSON =
                        Gson().toJson(homeUiState.itineraryInfo)
                    navigateToItinerary(courseInfoJSON)
                },
                onTimerFinished = {
                    viewModel.onTimerFinished()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(1f),
                onRefreshClick = {
                    onRefreshClick()
                }
            )
        } else {
            notificationScheduler.cancelAllNotifications()

            CurrentLocationSheet(
                currentLocation = homeUiState.departurePoint.name,
                destination = "우리집",
                onSearchClick = {
                    onSearchClick()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .zIndex(1f)
            )
        }

        val (prefixText, emphasisText, suffixText, bottomPadding) = when {
            homeUiState.isAlarmRegistered && homeUiState.isBusDeparted ->
                SpeechBubbleText(
                    stringResource(R.string.home_bubble_alarm_departed_text),
                    null,
                    null,
                    231.dp
                )

            homeUiState.isAlarmRegistered ->
                SpeechBubbleText(
                    "",
                    stringResource(R.string.home_bubble_alarm_emphasis_text),
                    "",
                    231.dp
                )

            else ->
                SpeechBubbleText(
                    stringResource(R.string.home_bubble_basic_text),
                    "약 " + homeUiState.taxiCost.toString() + "원",
                    null,
                    207.dp
                )
        }

        var speechBubbleFlag by remember { mutableStateOf(true) }

        val handleCharacterClick = {
            speechBubbleFlag = !speechBubbleFlag
            onCharacterClick()
        }

        if (!homeUiState.isAlarmRegistered) { // 첫 화면
            if (speechBubbleFlag) {
                CharacterLottieSpeechBubble(
                    prefixText = prefixText,
                    emphasisText = emphasisText,
                    suffixText = suffixText,
                    topPrefixText = "",
                    topEmphasisText = "지도를 움직여 출발지를 설정해 봐요",
                    topSuffixText = "",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = bottomPadding)
                        .noRippleClickable(onClick = onCharacterClick),
                    onClick = handleCharacterClick,
                    lottieResId = R.raw.atcha_character_1,
                    lineCount = 2
                )
            } else {
                CharacterLottieSpeechBubble(
                    prefixText = prefixText,
                    emphasisText = emphasisText,
                    suffixText = suffixText,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = bottomPadding)
                        .noRippleClickable(onClick = onCharacterClick),
                    onClick = handleCharacterClick,
                    lottieResId = R.raw.atcha_character_2,
                    lineCount = 1
                )
            }
        }
        if (homeUiState.isAlarmRegistered && !homeUiState.isBusDeparted) { // 알림 등록 후 예상 출발 시간 화면
            CharacterLottieSpeechBubble(
                prefixText = prefixText,
                emphasisText = emphasisText,
                suffixText = suffixText,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = bottomPadding)
                    .noRippleClickable(onClick = onCharacterClick),
                onClick = {},
                lottieResId = R.raw.atcha_chararcter_3,
                lineCount = 1
            )
        }

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
                    }
                )
            }
        }
    }
}

private fun formatTimeString(timeString: String): String {
    return try {
        if (timeString.contains("T")) {
            val dateTime = LocalDateTime.parse(timeString)
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            dateTime.format(formatter)
        } else if (timeString.contains(":") && timeString.split(":").size == 3) {
            val timeParts = timeString.split(":")
            "${timeParts[0]}:${timeParts[1]}"
        } else {
            timeString
        }
    } catch (e: Exception) {
        Timber.e("시간 형식 변환 오류: $timeString")
        timeString
    }
}

private data class SpeechBubbleText(
    val prefix: String,
    val emphasis: String? = null,
    val suffix: String? = null,
    val bottomPadding: Dp
)

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        padding = PaddingValues(0.dp),
        userLocation = LatLng(37.5665, 126.9780)
    )
}
