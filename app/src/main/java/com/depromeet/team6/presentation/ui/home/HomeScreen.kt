package com.depromeet.team6.presentation.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.R
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
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
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
        } else {
            CurrentLocationSheet(
                currentLocation = "중앙빌딩",
                destination = "우리집",
                onSearchClick = {},
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .zIndex(1f)
            )
        }

        val (prefixText, emphasisText, suffixText, bottomPadding) = when {
            uiState.isAlarmRegistered && uiState.isBusDeparted ->
                SpeechBubbleText(
                    stringResource(R.string.home_bubble_alarm_departed_text),
                    null,
                    null,
                    241.dp
                )

            uiState.isAlarmRegistered ->
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
                .padding(start = 8.dp, bottom = bottomPadding),
            onClick = { viewModel.onCharacterClick() },
            showSpeechBubble = uiState.showSpeechBubble
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
fun HomeScreenPreview() {
    HomeScreen()
}
