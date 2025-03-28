package com.depromeet.team6.presentation.ui.home

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.alarm.NotificationScheduler
import com.depromeet.team6.presentation.ui.alarm.NotificationTimeConstants
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterSheet
import com.depromeet.team6.presentation.ui.home.component.CharacterSpeechBubble
import com.depromeet.team6.presentation.ui.home.component.CurrentLocationSheet
import com.depromeet.team6.presentation.ui.home.component.TMapViewCompose
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNT
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import timber.log.Timber

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
                viewModel.finishAlarm(context)
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
    navigateToMypage: () -> Unit = {},
    navigateToItinerary: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel() // TODO : TmapViewCompose 변경 후 제거
) {
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val isUserLoggedIn = sharedPreferences.getBoolean("isUserLoggedIn", false) // 기본값은 false

    val notificationScheduler = NotificationScheduler(context)

    if (isUserLoggedIn) {
        viewModel.registerAlarm()
        viewModel.setBusDeparted()
    } else {
        viewModel.finishAlarm(context)
    }

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

        TMapViewCompose(
            userLocation,
            viewModel = viewModel
        ) // Replace with your actual API key

        // 알람 등록 시 Home UI
        if (homeUiState.isAlarmRegistered) {
            notificationScheduler.scheduleNotificationForTime(
                stringResource(R.string.app_name),
                stringResource(R.string.notification_content_text),
                NotificationTimeConstants.getDepartureTimeWithTodayDate()
            )

            AfterRegisterSheet(
                timeToLeave = "23:21:00",
                startLocation = homeUiState.departurePoint.name,
                destination = "우리집",
                onCourseTextClick = {},
                onFinishClick = {
                    onFinishClick()
                },
                onCourseDetailClick = {
                    val courseInfoJSON =
                        Gson().toJson(homeUiState.courseInfo)
                    navigateToItinerary(courseInfoJSON)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(1f),
                isBusDeparted = homeUiState.isBusDeparted
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
                    241.dp
                )

            homeUiState.isAlarmRegistered ->
                SpeechBubbleText(
                    stringResource(R.string.home_bubble_alarm_prefix_text),
                    stringResource(R.string.home_bubble_alarm_emphasis_text),
                    stringResource(R.string.home_bubble_alarm_suffix_text),
                    241.dp
                )

            else ->
                SpeechBubbleText(
                    stringResource(R.string.home_bubble_basic_text),
                    "34,000원",
                    null,
                    207.dp
                )
        }

        CharacterSpeechBubble(
            prefixText = prefixText,
            emphasisText = emphasisText,
            suffixText = suffixText,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = bottomPadding)
                .noRippleClickable(onClick = onCharacterClick),
            showSpeechBubble = homeUiState.showSpeechBubble
        )
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
