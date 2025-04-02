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
    lottieResId: Int = R.raw.character_alarm_not_registered
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(lottieResId)
    )

    var playAnimation by remember { mutableStateOf(true) }
    var iteration by remember { mutableStateOf(0) }

    var isTopSpeechBubbleVisible by remember { mutableStateOf(false) }
    var isBottomSpeechBubbleVisible by remember { mutableStateOf(false) }

    var speechBubbleTrigger by remember { mutableStateOf(0) }

    // 말풍선 표시 로직 (화면 진입 시 또는 탭할 때마다 실행)
    LaunchedEffect(speechBubbleTrigger) {
        if (showSpeechBubble) {
            isTopSpeechBubbleVisible = false
            isBottomSpeechBubbleVisible = false

            if (lineCount == 2) {
                // 0.7초 후에 위쪽 말풍선 표시
                delay(700)
                isTopSpeechBubbleVisible = true

                // 1.5초 후에 아래쪽 말풍선 표시
                delay(1500)
                isBottomSpeechBubbleVisible = true

                // 2.5초 후에 모든 말풍선 숨김
                delay(1000)
                isTopSpeechBubbleVisible = false
                delay(1000)
                isBottomSpeechBubbleVisible = false
            } else {
                // 단일 말풍선인 경우 기존 로직
                delay(700)
                isBottomSpeechBubbleVisible = true

                delay(2500)
                isBottomSpeechBubbleVisible = false
            }
        }
    }

    LaunchedEffect(Unit) {
        speechBubbleTrigger = 1
    }

    LaunchedEffect(iteration) {
        playAnimation = false
        delay(10) // 짧은 지연 후 재시작
        playAnimation = true
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
        if (lineCount == 2) {
            // 위쪽 말풍선
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

        // 아래쪽 말풍선
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

        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
    }
}

@Preview
@Composable
fun CharacterLottieSpeechBubblePreview() {
    CharacterLottieSpeechBubble(
        prefixText = "여기서 놓치면 택시비",
        modifier = Modifier,
        emphasisText = "34,000",
        suffixText = TODO(),
        lineCount = TODO(),
        onClick = TODO(),
        showSpeechBubble = TODO(),
        lottieResId = TODO(),
        topPrefixText = TODO(),
        topEmphasisText = TODO(),
        topSuffixText = TODO()
    )
}
