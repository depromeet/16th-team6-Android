package com.depromeet.team6.presentation.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.depromeet.team6.presentation.ui.home.component.CharacterSpeechBubble
import com.depromeet.team6.presentation.ui.home.component.CurrentLocationSheet
import com.depromeet.team6.presentation.ui.home.component.TMapViewCompose
import com.google.android.gms.maps.model.LatLng

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Box(
        modifier = modifier
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
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
