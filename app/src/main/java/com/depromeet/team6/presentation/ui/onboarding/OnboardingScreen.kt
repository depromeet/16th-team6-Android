package com.depromeet.team6.presentation.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSearchContainer
import com.depromeet.team6.presentation.ui.onboarding.component.common.OnboardingButton
import com.depromeet.team6.presentation.ui.onboarding.component.common.OnboardingTitle
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
    }

    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
        viewModel.sideEffect.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { pointHistorySideEffect ->
                when (pointHistorySideEffect) {
                    is OnboardingContract.OnboardingSideEffect.DummySideEffect -> Unit
                }
            }
    }

    when (uiState.loadState) {
        LoadState.Idle -> Unit

        LoadState.Loading -> Unit

        LoadState.Success -> {
            // TODO: 위치 권한 받기
            OnboardingScreen()
        }

        LoadState.Error -> Unit
    }
}

@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize().background(color = defaultTeam6Colors.black)) {
        Spacer(modifier = Modifier.height(56.dp))
        OnboardingTitle(OnboardingType.HOME)
        Spacer(modifier = Modifier.height(30.dp))
        OnboardingSearchContainer()
        Spacer(modifier = Modifier.weight(1f))
        OnboardingButton() { }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen()
}
