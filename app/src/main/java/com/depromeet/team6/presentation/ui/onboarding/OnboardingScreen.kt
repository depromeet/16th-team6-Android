package com.depromeet.team6.presentation.ui.onboarding

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.depromeet.team6.presentation.ui.onboarding.component.AlarmTime
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingAlarmSelector
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingButton
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSearchContainer
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSearchPopup
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSelectLocationButton
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSelectedHome
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingTitle
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun OnboardingRoute(
    padding: PaddingValues,
    viewModel: OnboardingViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                Log.d("Location_Permission", "Has Granted")
            }
        }
    )

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                Log.d("Notification_Permission", "Has Granted")
            }
        }
    )

    SideEffect {
        when (uiState.onboardingType) {
            OnboardingType.HOME -> {
                Log.d(
                    "Onboarding Screen",
                    "isLocationPermissionRequested: ${
                    PermissionUtil.isLocationPermissionRequested(
                        context
                    )
                    }, hasLocationPermissions: ${
                    PermissionUtil.hasLocationPermissions(context)
                    }"
                )
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
    }

    when {
        uiState.searchPopupVisible -> {
            OnboardingSearchPopup(
                uiState = uiState,
                padding = padding,
                searchText = uiState.searchText,
                onSearchTextChange = { newText ->
                    viewModel.setEvent(
                        OnboardingContract.OnboardingEvent.UpdateSearchText(newText)
                    )
                },
                onCloseButtonClicked = {
                    viewModel.setEvent(OnboardingContract.OnboardingEvent.ClearText)
                },
                selectButtonClicked = { location ->
                    viewModel.setEvent(
                        OnboardingContract.OnboardingEvent.LocationSelectButtonClicked(
                            location
                        )
                    )
                }
            )
        }

        else -> when (uiState.loadState) {
            LoadState.Idle -> {
                OnboardingScreen(
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
                        val timeValue = alarmTime.string.replace("1시간","60분").replace("분 전", "").toInt()
                        val newSelection = if (timeValue in uiState.alertFrequencies) {
                            uiState.alertFrequencies - timeValue
                        } else {
                            uiState.alertFrequencies + timeValue
                        }
                        viewModel.setEvent(OnboardingContract.OnboardingEvent.UpdateAlertFrequencies(newSelection))
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
    uiState: OnboardingContract.OnboardingUiState = OnboardingContract.OnboardingUiState(),
    modifier: Modifier = Modifier,
    onSearchBoxClicked: () -> Unit = {},
    onNextButtonClicked: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onAlarmTimeSelected: (AlarmTime) -> Unit = {}
) {
    var selectedItems by remember { mutableStateOf(setOf<AlarmTime>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = defaultTeam6Colors.greyWashBackground)
            .padding(padding)
    ) {
        if (uiState.onboardingType == OnboardingType.HOME) {
            Spacer(modifier = Modifier.height(72.dp))
            OnboardingTitle(onboardingType = uiState.onboardingType)
            Spacer(modifier = Modifier.height(48.dp))
            if (uiState.myHome.name.isEmpty()) {
                OnboardingSearchContainer(
                    onSearchBoxClicked = onSearchBoxClicked,
                    onLocationButtonClick = {
                        // TODO: 현위치 찾기 로직 구현
                        Log.d(
                            "OnboardingScreen",
                            "Location button clicked!"
                        )
                    }
                )
            } else {
                OnboardingSelectedHome(onboardingSearchLocation = uiState.myHome)
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
                    AlarmTime.entries.find { it.string.replace("분 전", "").replace("시간 전", "").toIntOrNull() == timeValue }
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
                uiState.myHome.name.isNotEmpty()
            }
        ) { onNextButtonClicked() }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(padding = PaddingValues(0.dp), onNextButtonClicked = {})
}
