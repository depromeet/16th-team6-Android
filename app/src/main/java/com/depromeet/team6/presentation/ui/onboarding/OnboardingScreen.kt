package com.depromeet.team6.presentation.ui.onboarding

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.presentation.type.OnboardingSelectLocationButtonType
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.presentation.type.toPermissionType
import com.depromeet.team6.presentation.ui.onboarding.component.AlarmTime
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingAlarmSelector
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingButton
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingMapView
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingPermissionBottomSheet
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingPermissionDeniedBottomSheet
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSearchContainer
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSearchPopup
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSelectLocationButton
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSelectedHome
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingTitle
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.toast.atChaToastMessage
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

@Composable
fun OnboardingRoute(
    padding: PaddingValues,
    viewModel: OnboardingViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val hasRequestedLocationPermission = remember { mutableStateOf(false) }

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasRequestedLocationPermission.value = true

            val anyDenied = permissions.any { !it.value }

            if (anyDenied) {
                viewModel.setEvent(
                    OnboardingContract.OnboardingEvent.ChangePermissionDeniedBottomSheetVisible(
                        permissionDeniedBottomSheetVisible = true
                    )
                )
            } else {
                Timber.d("모든 권한 허용됨")
            }
        }
    )

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                Timber.d("Notification_Permission Has Granted")
            }
        }
    )

    LaunchedEffect(Unit) {
        Timber.d("Location Permission= ${PermissionUtil.hasLocationPermissions(context)}")
        viewModel.setEvent(OnboardingContract.OnboardingEvent.UpdateUserLocation(context = context))
    }

    LaunchedEffect(uiState.onboardingType) {
        when (uiState.onboardingType) {
            OnboardingType.HOME -> {
                Timber.d(
                    "isLocationPermissionRequested: ${
                    PermissionUtil.isLocationPermissionRequested(
                        context
                    )
                    }, hasLocationPermissions: ${
                    PermissionUtil.hasLocationPermissions(context)
                    }"
                )
                if (!PermissionUtil.isLocationPermissionRequested(context)) {
                    viewModel.setEvent(
                        OnboardingContract.OnboardingEvent.ChangePermissionBottomSheetVisible(
                            permissionBottomSheetVisible = true
                        )
                    )
                }
            }

            OnboardingType.ALARM -> {
                if (!PermissionUtil.isNotificationPermissionRequested(context)) {
                    viewModel.setEvent(
                        OnboardingContract.OnboardingEvent.ChangePermissionBottomSheetVisible(
                            permissionBottomSheetVisible = true
                        )
                    )
                }
            }
        }
    }

    when {
        uiState.searchPopupVisible -> {
            OnboardingSearchPopup(
                context = context,
                searchLocations = uiState.searchLocations,
                padding = padding,
                searchText = uiState.searchText,
                onSearchTextChange = { newText ->
                    viewModel.setState {
                        copy(searchText = newText)
                    }
                    viewModel.setEvent(OnboardingContract.OnboardingEvent.UpdateSearchText(text = newText))
                },
                onBackButtonClicked = {
                    viewModel.setEvent(OnboardingContract.OnboardingEvent.SearchPopUpBackPressed)
                },
                selectButtonClicked = { address ->
                    viewModel.setEvent(
                        OnboardingContract.OnboardingEvent.LocationSelectButtonClicked(
                            address
                        )
                    )
                    viewModel.setEvent(
                        OnboardingContract.OnboardingEvent.ChangeMapViewVisible(
                            true
                        )
                    )
                },
                onTextClearButtonClicked = {
                    viewModel.setEvent(OnboardingContract.OnboardingEvent.ClearText)
                }
            )
        }

        else -> when (uiState.loadState) {
            LoadState.Idle -> {
                OnboardingScreen(
                    context = context,
                    padding = padding,
                    uiState = uiState,
                    onSearchBoxClicked = {
                        viewModel.setEvent(OnboardingContract.OnboardingEvent.ShowSearchPopup)
                    },
                    onNextButtonClicked = {
                        if (uiState.onboardingType == OnboardingType.HOME) {
                            viewModel.setEvent(OnboardingContract.OnboardingEvent.ChangeOnboardingType)
                        } else {
                            viewModel.postSignUp()
                        }
                    },
                    onBackPressed = { viewModel.setEvent(OnboardingContract.OnboardingEvent.BackPressed) },
                    onAlarmTimeSelected = { alarmTime ->
                        val timeValue = alarmTime.minutes
                        if (timeValue != 1) {
                            val newSelection = if (timeValue in uiState.alertFrequencies) {
                                uiState.alertFrequencies - timeValue
                            } else {
                                uiState.alertFrequencies + timeValue
                            }
                            viewModel.setEvent(
                                OnboardingContract.OnboardingEvent.UpdateAlertFrequencies(
                                    newSelection
                                )
                            )
                        }
                    },
                    bottomSheetButtonClicked = {
                        viewModel.setEvent(
                            OnboardingContract.OnboardingEvent.ChangePermissionBottomSheetVisible(
                                permissionBottomSheetVisible = false
                            )
                        )

                        when (uiState.onboardingType) {
                            OnboardingType.HOME -> {
                                if (!PermissionUtil.isLocationPermissionRequested(context) &&
                                    !PermissionUtil.hasLocationPermissions(context)
                                ) {
                                    PermissionUtil.requestLocationPermissions(
                                        context = context,
                                        locationPermissionLauncher = locationPermissionsLauncher
                                    )
                                }
                            }

                            OnboardingType.ALARM -> {
                                if (!PermissionUtil.isNotificationPermissionRequested(context) &&
                                    !PermissionUtil.hasNotificationPermission(context)
                                ) {
                                    PermissionUtil.requestNotificationPermission(
                                        context = context,
                                        notificationPermissionLauncher = notificationPermissionLauncher
                                    )
                                }
                            }
                        }
                    },
                    onLocationButtonClicked = {
                        if (PermissionUtil.hasLocationPermissions(context = context)) {
                            viewModel.setCurrentLocationToHomeAddress(context = context) {
                                viewModel.setEvent(
                                    OnboardingContract.OnboardingEvent.ChangeMapViewVisible(
                                        true
                                    )
                                )
                            }
                        } else {
                            atChaToastMessage(
                                context = context,
                                R.string.onboarding_location_no_permission_toast,
                                length = Toast.LENGTH_SHORT
                            )
                        }
                    },
                    deniedBottomSheetCloseButtonClicked = {
                        viewModel.setEvent(
                            OnboardingContract.OnboardingEvent.ChangePermissionDeniedBottomSheetVisible(
                                permissionDeniedBottomSheetVisible = false
                            )
                        )
                    },
                    deniedBottomSheetSettingButtonClicked = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    getCenterLocation = { viewModel.getCenterLocation(it) },
                    clearAddress = {
                        viewModel.setEvent(OnboardingContract.OnboardingEvent.ClearAddress)
                        viewModel.setEvent(
                            OnboardingContract.OnboardingEvent.ChangeMapViewVisible(
                                mapViewVisible = false
                            )
                        )
                    },
                    mapViewSelectButtonClicked = {
                        viewModel.setEvent(
                            (
                                    OnboardingContract.OnboardingEvent.ChangeMapViewVisible(
                                        false
                                    )
                                    )
                        )
                    }
                )
            }

            LoadState.Loading -> Unit
            LoadState.Success -> navigateToHome()
            LoadState.Error -> Unit
        }
    }
}

@Composable
fun OnboardingScreen(
    padding: PaddingValues,
    context: Context = LocalContext.current,
    uiState: OnboardingContract.OnboardingUiState = OnboardingContract.OnboardingUiState(),
    modifier: Modifier = Modifier,
    onSearchBoxClicked: () -> Unit = {},
    onNextButtonClicked: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onAlarmTimeSelected: (AlarmTime) -> Unit = {},
    onLocationButtonClicked: () -> Unit = {},
    bottomSheetButtonClicked: () -> Unit = {},
    deniedBottomSheetSettingButtonClicked: () -> Unit = {},
    deniedBottomSheetCloseButtonClicked: () -> Unit = {},
    getCenterLocation: (LatLng) -> Unit = {},
    clearAddress: () -> Unit = {},
    mapViewSelectButtonClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = defaultTeam6Colors.greyWashBackground)
        ) {
            if (uiState.onboardingType == OnboardingType.HOME) {
                Spacer(modifier = Modifier.height(72.dp))
                OnboardingTitle(onboardingType = uiState.onboardingType)
                Spacer(modifier = Modifier.height(48.dp))
                if (uiState.myAddress.address.isEmpty()) {
                    OnboardingSearchContainer(
                        onSearchBoxClicked = onSearchBoxClicked,
                        onLocationButtonClick = onLocationButtonClicked
                    )
                } else {
                    OnboardingSelectedHome(onboardingSearchLocation = uiState.myAddress)
                    Spacer(modifier = Modifier.height(31.dp))

                    OnboardingSelectLocationButton(
                        searchLocationButtonType = OnboardingSelectLocationButtonType.EDIT,
                        onClick = onSearchBoxClicked
                    )
                }
            } else {
                Icon(
                    modifier = Modifier
                        .padding(vertical = 18.dp, horizontal = 16.dp)
                        .noRippleClickable { onBackPressed() },
                    imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_white),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.height(12.dp))
                OnboardingTitle(onboardingType = uiState.onboardingType)
                Spacer(modifier = Modifier.height(68.dp))
                OnboardingAlarmSelector(
                    selectedItems = uiState.alertFrequencies.mapNotNull { timeValue ->
                        AlarmTime.entries.find { it.minutes == timeValue }
                    }.toSet(),
                    onItemClick = onAlarmTimeSelected
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            OnboardingButton(
                isEnabled =
                if (uiState.onboardingType == OnboardingType.ALARM) {
                    uiState.alertFrequencies.isNotEmpty()
                } else {
                    uiState.myAddress.name.isNotEmpty()
                }
            ) { onNextButtonClicked() }
            Spacer(modifier = Modifier.height(20.dp))
        }
        if (uiState.mapViewVisible) {
            val currentLocation by remember { mutableStateOf(uiState.myAddress) }
            OnboardingMapView(
                context = context,
                myAddress = uiState.myAddress,
                getCenterLocation = getCenterLocation,
                currentLocation = currentLocation,
                buttonClicked = mapViewSelectButtonClicked,
                backButtonClicked = clearAddress
            )
        }
        OnboardingPermissionBottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter),
            onboardingPermissionType = uiState.onboardingType.toPermissionType(),
            bottomSheetVisible = uiState.permissionBottomSheetVisible,
            buttonClicked = { bottomSheetButtonClicked() }
        )
        OnboardingPermissionDeniedBottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter),
            bottomSheetVisible = uiState.permissionDeniedBottomSheetVisible,
            completeButtonClicked = deniedBottomSheetCloseButtonClicked,
            settingButtonClicked = deniedBottomSheetSettingButtonClicked
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(padding = PaddingValues(0.dp), onNextButtonClicked = {})
}