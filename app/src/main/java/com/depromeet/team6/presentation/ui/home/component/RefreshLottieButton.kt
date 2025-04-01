package com.depromeet.team6.presentation.ui.home.component

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable

@Composable
fun RefreshLottieButton(
    onClick: () -> Unit,
    @RawRes lottieResId: Int = R.raw.refresh_spin,
    tint: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieResId))

    var isPlaying by remember { mutableStateOf(false) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = 1
    )

    LaunchedEffect(progress) {
        if (progress == 1f && isPlaying) {
            isPlaying = false
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .size(24.dp)
            .noRippleClickable {
                isPlaying = true
                onClick()
            }
    )
}
