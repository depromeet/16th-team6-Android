package com.depromeet.team6.presentation.ui.itinerary

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.depromeet.team6.presentation.ui.main.MainViewModel
import com.depromeet.team6.presentation.ui.overlay.OverlayPermissionDialog
import com.depromeet.team6.presentation.ui.overlay.PermissionSnackbar
import com.depromeet.team6.presentation.util.base.ApiErrorSideEffect
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.toast.atChaToastMessage
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import com.google.android.gms.maps.model.LatLng

@Composable
fun ItineraryRoute(
    padding: PaddingValues,
    courseInfoJSON: String,
    departurePointJSON: String,
    destinationPointJSON: String,
    focusedMarkerParam: FocusedMarkerParameter?,
    navigateToBusCourse: (BusArrivalParameter) -> Unit,
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: ItineraryViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val mainViewModel: MainViewModel = hiltViewModel(LocalActivity.current as ComponentActivity)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLocation by mainViewModel.currentLocation.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (viewModel.hasShownOverlayDialogBefore() &&
                        PermissionUtil.isOverlayPermissionRequested(context)
                    ) {
                        if (!PermissionUtil.hasOverlayPermission(context)) {
                            viewModel.showPermissionSnackbar()
                        }
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // SideEffect 감지
    LaunchedEffect(Unit) {
        viewModel.initItineraryInfo(
            courseInfoJSON,
            departurePointJSON,
            destinationPointJSON,
        )
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

                ItineraryContract.ItinerarySideEffect.NavigateHomeWithToast -> {
                    navigateToHome()
                    atChaToastMessage(context, R.string.course_set_notification_snackbar, Toast.LENGTH_SHORT)
                }

                ItineraryContract.ItinerarySideEffect.ShowNotificationToastSetAlarmFailed -> {
                    atChaToastMessage(context, R.string.course_set_notification_failed_snackbar, Toast.LENGTH_SHORT)
                }
            }
        }
    }

    when (uiState.courseDataLoadState) {
        LoadState.Idle -> {}
        LoadState.Success -> {
            ItineraryScreen(
                marginTop = padding.calculateTopPadding(),
                marginBottom = padding.calculateBottomPadding(),
                uiState = uiState,
                currentLocation = currentLocation,
                focusedMarkerParam = focusedMarkerParam,
                onBackPressed = onBackPressed,
                onRefreshButtonClick = { viewModel.setEvent(ItineraryContract.ItineraryEvent.RefreshButtonClicked) },
                registerAlarmButtonClick = { routeId ->
                    if (PermissionUtil.needsOverlayPermission(context)) {
                        if (viewModel.shouldShowOverlayDialog()) {
                            viewModel.showOverlayPermissionDialog()
                        } else {
                            viewModel.showPermissionSnackbar()
                        }
                    } else {
                        val sharedPreferences =
                            context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        val registeredCourse = uiState.itineraryInfo

                        if (registeredCourse != null) {
                            viewModel.setEvent(ItineraryContract.ItineraryEvent.RegisterAlarm(routeId = routeId))
                        } else {
                            atChaToastMessage(context, R.string.course_set_notification_failed_snackbar)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
                    .background(defaultTeam6Colors.gray950),
                navigateToBusCourse = navigateToBusCourse,
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
        }
        else -> Unit
    }
}

@Composable
fun ItineraryScreen(
    marginTop: Dp,
    marginBottom: Dp,
    currentLocation: LatLng,
    modifier: Modifier = Modifier,
    uiState: ItineraryContract.ItineraryUiState = ItineraryContract.ItineraryUiState(),
    focusedMarkerParam: FocusedMarkerParameter? = null,
    navigateToBusCourse: (BusArrivalParameter) -> Unit = {},
    onRefreshButtonClick: () -> Unit = {},
    registerAlarmButtonClick: (String) -> Unit = {},
    onBackPressed: () -> Unit = {},
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
                    currentLocation = currentLocation,
                    departurePoint = uiState.departurePoint!!,
                    destinationPoint = uiState.destinationPoint!!,
                    onBackPressed = onBackPressed,
                    focusedMarkerParameter = focusedMarkerParam,
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
                        currentLocation = currentLocation,
                        courseInfo = itineraryInfo,
                        busArrivalStatus = uiState.busArrivalStatus,
                        departurePoint = uiState.departurePoint!!,
                        destinationPoint = uiState.destinationPoint!!,
                        isAlarmRegistered = uiState.isAlarmRegistered,
                        onClickBusInfo = navigateToBusCourse
                    )
                    Spacer(Modifier.height(marginBottom))
                }
            },
            sheetScrollState = sheetScrollState,
            marginBottom = marginBottom
        )

        // 막차알림 등록 되어 있으면 리프레시 버튼
        // 막차알림 등록 안되어 있으면 알림등록 버튼
        if (uiState.isAlarmRegistered) {
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
                    style = defaultTeam6Typography.heading3_H3SB17,
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
        marginBottom = 10.dp,
        currentLocation = LatLng(37.5665, 126.9780),
    )
}
