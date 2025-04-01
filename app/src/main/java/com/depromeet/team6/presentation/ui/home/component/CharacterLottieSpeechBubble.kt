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
    onClick: () -> Unit = {},
    showSpeechBubble: Boolean = true,
    lottieResId: Int = R.raw.character_alarm_not_registered
) {
    // Lottie 애니메이션 컴포지션 불러오기
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(lottieResId)
    )

    // 애니메이션 재생 관련 상태 관리
    var playAnimation by remember { mutableStateOf(true) }
    var iteration by remember { mutableStateOf(0) }

    // 말풍선 표시 여부 상태 관리
    var isSpeechBubbleVisible by remember { mutableStateOf(false) }

    // 말풍선 표시 트리거 (화면 진입 시 또는 탭할 때)
    var speechBubbleTrigger by remember { mutableStateOf(0) }

    // 말풍선 표시 로직 (화면 진입 시 또는 탭할 때마다 실행)
    LaunchedEffect(speechBubbleTrigger) {
        if (showSpeechBubble) {
            // 이미 표시 중이라면 즉시 숨김
            isSpeechBubbleVisible = false

            // 0.7초 후에 말풍선 표시
            delay(700)
            isSpeechBubbleVisible = true

            // 2.5초 후에 말풍선 숨김
            delay(2500)
            isSpeechBubbleVisible = false
        }
    }

    // 초기 화면 진입 시 말풍선 표시 트리거
    LaunchedEffect(Unit) {
        speechBubbleTrigger = 1
    }

    // 애니메이션 재시작을 위한 효과
    LaunchedEffect(iteration) {
        playAnimation = false
        delay(10) // 짧은 지연 후 재시작
        playAnimation = true
    }

    // 클릭 시 애니메이션을 재생하고 말풍선을 표시하기 위한 핸들러
    val handleClick = {
        // 애니메이션 재시작
        iteration++

        // 말풍선 표시 트리거
        speechBubbleTrigger++

        // 사용자 정의 onClick 호출
        onClick()
    }

    // 애니메이션 진행 상태
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,  // 한 번만 재생
        isPlaying = playAnimation,
        restartOnPlay = true
    )

    Column(
        modifier = modifier.noRippleClickable { handleClick() }
    ) {
        // 말풍선 부분 (애니메이션 적용)
        AnimatedVisibility(
            visible = isSpeechBubbleVisible && showSpeechBubble,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                SpeechBubble(
                    prefix = prefixText,
                    modifier = Modifier,
                    emphasisText = emphasisText,
                    suffix = suffixText
                )

                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        // 캐릭터 애니메이션 부분
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
        emphasisText = "34,000"
    )
}