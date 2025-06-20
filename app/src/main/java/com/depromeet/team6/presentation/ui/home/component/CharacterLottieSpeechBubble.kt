package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import kotlinx.coroutines.delay

@Composable
fun CharacterLottieSpeechBubble(
    prefixText: String,
    modifier: Modifier = Modifier,
    emphasisText: String? = null,
    suffixText: String? = null,
    topPrefixText: String? = null,
    topEmphasisText: String? = null,
    topSuffixText: String? = null,
    lineCount: Int,
    onClick: () -> Unit = {},
    showSpeechBubble: Boolean = true,
    lottieResId: Int = R.raw.character_alarm_not_registered,
    externalTrigger: Int = 0
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(lottieResId)
    )

    var playAnimation by remember { mutableStateOf(true) }
    var iteration by remember { mutableStateOf(0) }

    var isTopSpeechBubbleVisible by remember { mutableStateOf(false) }
    var isBottomSpeechBubbleVisible by remember { mutableStateOf(false) }

    var speechBubbleTrigger by remember { mutableStateOf(0) }

    val hasAnyText = !prefixText.isBlank() ||
            !emphasisText.isNullOrBlank() ||
            !suffixText.isNullOrBlank() ||
            !topPrefixText.isNullOrBlank() ||
            !topEmphasisText.isNullOrBlank() ||
            !topSuffixText.isNullOrBlank()

    LaunchedEffect(externalTrigger) {
        if (externalTrigger > 0) {
            iteration++
            speechBubbleTrigger++
        }
    }

    LaunchedEffect(speechBubbleTrigger, showSpeechBubble, hasAnyText) {
        if (showSpeechBubble && hasAnyText) {
            isTopSpeechBubbleVisible = false
            isBottomSpeechBubbleVisible = false

            if (lineCount == 2) {
                delay(700)
                if (showSpeechBubble && hasAnyText) isTopSpeechBubbleVisible = true

                delay(1500)
                if (showSpeechBubble && hasAnyText) isBottomSpeechBubbleVisible = true

                delay(1000)
                isTopSpeechBubbleVisible = false
                delay(1000)
                isBottomSpeechBubbleVisible = false
            } else {
                delay(700)
                if (showSpeechBubble && hasAnyText) isBottomSpeechBubbleVisible = true

                delay(2500)
                isBottomSpeechBubbleVisible = false
            }
        } else {
            isTopSpeechBubbleVisible = false
            isBottomSpeechBubbleVisible = false
        }
    }

    LaunchedEffect(Unit) {
        speechBubbleTrigger = 1
    }

    LaunchedEffect(iteration) {
        if (iteration > 0) {
            playAnimation = false
            delay(100)
            playAnimation = true
        }
    }

    val handleClick = {
        iteration++
        speechBubbleTrigger++
        onClick()
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = playAnimation,
        restartOnPlay = true
    )

    Column(
        modifier = modifier.noRippleClickable { handleClick() }
    ) {
        if (lineCount == 2 && hasAnyText) {
            AnimatedVisibility(
                visible = isTopSpeechBubbleVisible && showSpeechBubble,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SpeechBubble(
                    prefix = topPrefixText ?: "",
                    modifier = Modifier,
                    emphasisText = topEmphasisText,
                    suffix = topSuffixText,
                    tailExist = false
                )
            }
        }

        if (hasAnyText) {
            AnimatedVisibility(
                visible = isBottomSpeechBubbleVisible && showSpeechBubble,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                SpeechBubble(
                    prefix = prefixText,
                    modifier = Modifier,
                    emphasisText = emphasisText,
                    suffix = suffixText,
                    tailExist = true
                )
            }
        }

        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
    }
}