package com.depromeet.team6.presentation.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.presentation.ui.home.component.CharacterSpeechBubble
import com.depromeet.team6.presentation.ui.home.component.CurrentLocationSheet
import com.depromeet.team6.presentation.ui.home.component.TMapViewCompose
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng

@Composable
fun HomeRoute(
    padding: PaddingValues,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
        viewModel.sideEffect.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { signInSideEffect ->
                when (signInSideEffect) {
                    is HomeContract.HomeSideEffect.NavigateToLogin -> navigateToLogin()
                }
            }
    }

    when (uiState.loadState) {
        LoadState.Idle -> HomeScreen(
            padding = padding,
            logoutClicked = { viewModel.logout() },
            withDrawClicked = { viewModel.withDraw() }
        )
        LoadState.Error -> navigateToLogin()
        else -> Unit
    }
}

@Composable
fun HomeScreen(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    logoutClicked: () -> Unit = {},
    withDrawClicked: () -> Unit = {}

) {
    Box(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        TMapViewCompose(LatLng(37.5665, 126.9780)) // Replace with your actual API key

        CurrentLocationSheet(
            currentLocation = "중앙빌딩",
            destination = "우리집",
            onSearchClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .zIndex(1f)
        )

        CharacterSpeechBubble(
            text = "여기서 놓치면 택시비 약",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 185.dp),
            taxiCost = "34,000"
        )
        Column {
            Text(
                text = "Logout Test",
                modifier = Modifier.noRippleClickable {
                    logoutClicked()
                }
            )
            Text(
                text = "WithDraw Test",
                modifier = Modifier.noRippleClickable {
                    withDrawClicked()
                }
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(padding = PaddingValues(0.dp))
}
