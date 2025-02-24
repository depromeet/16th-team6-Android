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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.presentation.ui.onboarding.component.AlarmTime
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingAlarmSelector
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingButton
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSearchContainer
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingTitle
import com.depromeet.team6.presentation.util.location.LocationUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun OnboardingRoute(
    padding: PaddingValues,
    viewModel: OnboardingViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                Log.d("Location_Permission","Has Granted")
            }
        }
    )

    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
        viewModel.sideEffect.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { pointHistorySideEffect ->
                when (pointHistorySideEffect) {
                    is OnboardingContract.OnboardingSideEffect.DummySideEffect -> Unit
                }
            }
    }

    SideEffect {
        if (!LocationUtil.hasLocationPermissions(context)) {
            LocationUtil.requestLocationPermissions(locationPermissionsLauncher)
        }
    }

    when (uiState.loadState) {
        LoadState.Idle -> {
            OnboardingScreen(
                padding = padding,
                uiState = uiState,
                onNextButtonClicked = {
                    if (uiState.onboardingType == OnboardingType.HOME) {
                        viewModel.setEvent(
                            OnboardingContract.OnboardingEvent.ChangeOnboardingType
                        )
                    } else {
                        navigateToHome()
                    }
                }
            )
        }

        LoadState.Loading -> Unit

        LoadState.Success -> {
            // TODO: 위치 권한 받기
        }

        LoadState.Error -> Unit
    }
}

@Composable
fun OnboardingScreen(
    padding: PaddingValues,
    uiState: OnboardingContract.OnboardingUiState = OnboardingContract.OnboardingUiState(),
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItems by remember { mutableStateOf(setOf<AlarmTime>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = defaultTeam6Colors.black)
            .padding(padding)
    ) {
        Spacer(modifier = Modifier.height(56.dp))
        OnboardingTitle(onboardingType = uiState.onboardingType)
        if (uiState.onboardingType == OnboardingType.HOME) {
            Spacer(modifier = Modifier.height(30.dp))
            OnboardingSearchContainer()
        } else {
            Spacer(modifier = Modifier.height(68.dp))
            OnboardingAlarmSelector(
                selectedItems = selectedItems,
                onItemClick = { alarmTime ->
                    selectedItems = if (alarmTime in selectedItems) {
                        selectedItems - alarmTime
                    } else {
                        selectedItems + alarmTime
                    }
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        OnboardingButton(
            isEnabled = if (uiState.onboardingType == OnboardingType.ALARM) {
                selectedItems.isNotEmpty()
            } else {
                true
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
