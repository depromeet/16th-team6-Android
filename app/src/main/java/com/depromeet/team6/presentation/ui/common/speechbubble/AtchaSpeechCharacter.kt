package com.depromeet.team6.presentation.ui.common.speechbubble

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
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
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.home.HomeContract
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
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
 * @param onCharacterClick 뷰모델에서 필요한 API 호출 (여기에 throttling 걸어줘야 합니다)
 */
@Composable
fun AtchaSpeechCharacter(
    speechRequest: HomeContract.SpeechRequest,
    modifier: Modifier = Modifier,
    onCharacterClick: () -> Unit = {}
) {
    val bubbles = remember { mutableStateListOf<BubbleMessage>() }
    val scope = rememberCoroutineScope()
    var debouncing by remember { mutableStateOf(false) }
    val lottieResId = R.raw.character_alarm_not_registered
    val lottie = rememberLottieAnimatable()

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(lottieResId)
    )

    var speechJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        onCharacterClick()
    }

    LaunchedEffect(composition) {
        if (composition != null) {
            lottie.animate(
                composition = composition,
                iterations = 1,
                initialProgress = 0f
            )
        }
    }

    LaunchedEffect(speechRequest) {
        val messagesToAdd = speechRequest.messages

        // request가 null이거나 메시지가 비어있으면 아무것도 하지 않습니다.
        if (messagesToAdd == null || messagesToAdd.isEmpty()) {
            return@LaunchedEffect
        }

        // 이미 말풍선 생성중이면 SpeechRequest가 들어와도 말풍선 만들지 않음
        if (speechJob != null && speechJob!!.isActive) return@LaunchedEffect

        // 말풍선 만들때 캐릭터 통통 튀기
        scope.launch {
            // 매 클릭마다 0에서 1까지 1회 재생
            lottie.animate(
                composition = composition,
                iterations = 1,
                initialProgress = 0f
            )
        }
        speechJob = scope.launch {
            val removalJobs = mutableListOf<Job>()
            // 말풍선 생성 로직
            messagesToAdd.forEach { messageText ->
                // 부모 Job(speechJob)이 취소되면 delay에서 예외가 발생하며 중단됩니다.
                delay(700L)
                val newBubble = BubbleMessage(
                    id = System.nanoTime(),
                    text = messageText,
                    isVisible = true
                )
                bubbles.add(newBubble)

                // 개별 말풍선 제거 타이머
                val removalJob = scope.launch {
                    delay(2500L)
                    val idx = bubbles.indexOfFirst { it.id == newBubble.id }
                    if (idx != -1) {
                        bubbles[idx] = bubbles[idx].copy(isVisible = false)
                    }
                    delay(350)
                    val removeIdx = bubbles.indexOfFirst { it.id == newBubble.id }
                    if (removeIdx != -1) {
                        bubbles.removeAt(removeIdx)
                    }
                }
                removalJobs.add(removalJob)
            }
            // --- 모든 말풍선이 제거되면 speechJob 완료 -> 해당 라이프사이클 기준으로 쓰로틀링 ---
            removalJobs.joinAll()
        }
    }

    Column(
        modifier = modifier
    ) {
        // 말풍선들을 아래에서 위로 쌓아 올리는 UI
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.Start
        ) {
            bubbles.forEachIndexed { index, bubble ->
                key(bubble.id) {
                    BubbleItem(
                        modifier = Modifier
                            .offset(y = 10.dp),
                        text = bubble.text,
                        showTail = index == bubbles.lastIndex,
                        isVisible = bubble.isVisible // 👈 4. 부모의 상태를 자식에게 전달
                        // onRemove 콜백은 이제 필요 없음
                    )
                }
            }
        }
        LottieAnimation(
            composition = composition,
            progress = { lottie.progress },
            modifier = Modifier.noRippleClickable {
                // 캐릭터 클릭시 애니메이션은 무조건 재생하되, API 호출만 throttling 처리
                if (speechJob?.isActive == false && !debouncing) {
                    debouncing = true
                    onCharacterClick()
                    scope.launch {
                        delay(1000)
                        debouncing = false
                    }
                }
                scope.launch {
                    // 매 클릭마다 0에서 1까지 1회 재생
                    lottie.animate(
                        composition = composition,
                        iterations = 1,
                        initialProgress = 0f
                    )
                }
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
    isVisible: Boolean, // 👈 1. 부모로부터 가시성 상태를 받음
    modifier: Modifier = Modifier
) {
    // 2. 내부 상태 (isVisible), LaunchedEffect, onRemove 콜백 모두 제거
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible, // 👈 3. 전달받은 isVisible 상태를 직접 사용
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        AtchaSpeechBubble(
            message = text,
            tailExist = showTail
        )
    }
}
