package com.depromeet.team6.presentation.ui.common.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun AtChaLoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(defaultTeam6Colors.greyWashBackground.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.atcha_loading)
        )
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = Int.MAX_VALUE
        )

        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
    }
}

@Preview
@Composable
fun AtChaLoadingViewPreview() {
    AtChaLoadingView()
}
