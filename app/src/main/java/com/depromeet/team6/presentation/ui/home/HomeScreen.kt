package com.depromeet.team6.presentation.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.depromeet.team6.presentation.ui.home.component.AfterRegisterSheet
import com.depromeet.team6.presentation.ui.home.component.CharacterSpeechBubble
import com.depromeet.team6.presentation.ui.home.component.CurrentLocationSheet
import com.depromeet.team6.presentation.ui.home.component.TMapViewCompose
import com.google.android.gms.maps.model.LatLng

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(uiState.isAlarmRegistered) {
        if (uiState.isAlarmRegistered) {
            Toast.makeText(
                context,
                "알림이 등록되었어요",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        TMapViewCompose(
            LatLng(37.5665, 126.9780),
            viewModel = viewModel
        ) // Replace with your actual API key

        // 알람 등록 시 Home UI
        if (uiState.isAlarmRegistered) {
            AfterRegisterSheet(
                timeToLeave = "22:30:00",
                startLocation = "중앙빌딩",
                destination = "우리집",
                onCourseTextClick = {},
                onFinishClick = {},
                onCourseDetailClick = {},
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(1f),
                isBusDeparted = uiState.isBusDeparted
            )

            if (uiState.isBusDeparted) {
                CharacterSpeechBubble(
                    prefixText = "이때 출발해야 막차를 탈 수 있어요",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 241.dp),
                    onClick = { viewModel.onCharacterClick() },
                    showSpeechBubble = uiState.showSpeechBubble
                )
            } else {
                CharacterSpeechBubble(
                    prefixText = "차고지에서 출발하면",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 241.dp),
                    emphasisText = "더 정확하게",
                    suffixText = "알려드려요",
                    onClick = { viewModel.onCharacterClick() },
                    showSpeechBubble = uiState.showSpeechBubble
                )
            }
        } else { // 기본 Home UI
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
                prefixText = "여기서 놓치면 택시비 약",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 207.dp),
                emphasisText = "34,000원",
                onClick = { viewModel.onCharacterClick() },
                showSpeechBubble = uiState.showSpeechBubble
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
