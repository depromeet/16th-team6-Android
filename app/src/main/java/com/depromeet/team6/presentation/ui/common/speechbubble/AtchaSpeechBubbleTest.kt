package com.depromeet.team6.presentation.ui.common.speechbubble

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.depromeet.team6.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 말풍선 데이터를 위한 데이터 클래스
data class BubbleMessage(
    val id: Long,
    val text: String,
    val isVisible: Boolean = true // AnimatedVisibility를 위한 상태
)

/**
 * 말풍선 스택을 표시하고 관리하는 메인 Composable.
 * 새로운 문자열이 주어지면 말풍선을 추가하고, 일정 시간 후 순차적으로 제거합니다.
 *
 * @param messagesToAdd 시뮬레이션을 위해 순서대로 추가할 문자열 목록
 */
@Composable
fun AtchaSpeechCharacter(
    messagesToAdd: List<String>,
    modifier : Modifier = Modifier,
) {
    val bubbles = remember { mutableStateListOf<BubbleMessage>() }
    val scope = rememberCoroutineScope()
    val lottieResId = R.raw.character_alarm_not_registered

    var trigger by remember { mutableStateOf(0) }
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(lottieResId)
    )
    val progress by key(trigger) {
        animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
            isPlaying = true,
            restartOnPlay = true
        )
    }

    // 이 LaunchedEffect 로직은 이미 훌륭합니다. (수정 불필요)
    LaunchedEffect(messagesToAdd) {
        messagesToAdd.forEach { messageText ->
            delay(700L)
            val newBubble = BubbleMessage(
                id = System.nanoTime(),
                text = messageText,
                isVisible = true
            )

            bubbles.add(newBubble)

            // 2초 후 말풍선을 사라지게 하고 목록에서 제거하는 작업
            scope.launch {
                delay(2000L) // 2초 유지

                // 1. isVisible = false로 변경 (애니메이션 시작 트리거)
                val idx = bubbles.indexOfFirst { it.id == newBubble.id }
                if (idx != -1) {
                    bubbles[idx] = bubbles[idx].copy(isVisible = false)
                }

                delay(350) // exit 애니메이션 시간

                // 2. 리스트에서 완전히 제거 (UI에서 사라짐)
                val removeIdx = bubbles.indexOfFirst { it.id == newBubble.id }
                if (removeIdx != -1) {
                    bubbles.removeAt(removeIdx)
                }
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        // 말풍선들을 아래에서 위로 쌓아 올리는 UI
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            bubbles.forEachIndexed { index, bubble ->
                key(bubble.id) {
                    BubbleItem(
                        text = bubble.text,
                        showTail = index == bubbles.lastIndex,
                        isVisible = bubble.isVisible // 👈 4. 부모의 상태를 자식에게 전달
                        // onRemove 콜백은 이제 필요 없음
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.clickable {
                trigger++ // 클릭 시 trigger 값을 변경!
            }
        )
    }
}

/**
 * 사용자 정의 말풍선 Composable. (내부 상태 제거)
 *
 * @param text 표시할 메시지 데이터
 * @param showTail 말풍선 꼬리(tail)를 표시할지 여부.
 * @param isVisible 부모로부터 전달받는 가시성 상태.
 */
@Composable
fun BubbleItem(
    text: String,
    showTail: Boolean,
    isVisible: Boolean // 👈 1. 부모로부터 가시성 상태를 받음
) {
    // 2. 내부 상태 (isVisible), LaunchedEffect, onRemove 콜백 모두 제거
    AnimatedVisibility(
        visible = isVisible, // 👈 3. 전달받은 isVisible 상태를 직접 사용
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            AtchaSpeechBubble(
                message = text,
                tailExist = showTail
            )
        }
    }
}