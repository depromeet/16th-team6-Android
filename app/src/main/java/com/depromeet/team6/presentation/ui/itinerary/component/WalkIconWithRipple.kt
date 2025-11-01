package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.common.TransportVectorIconComposable
import com.depromeet.team6.presentation.util.Dimens.WalkIconWithRippleSize
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun WalkIconWithRipple(
    modifier: Modifier = Modifier,
    iconVisible: Boolean = true
) {
    val maxRippleSize: Dp = WalkIconWithRippleSize
    // 가장 큰 크기를 기준으로 Box를 설정합니다.
    Box(
        modifier = modifier.size(maxRippleSize), // 파장이 퍼질 공간 확보
        contentAlignment = Alignment.Center
    ) {
        // 물결 파장 애니메이션 (배경 원 바깥으로 퍼짐)
        RippleWaveAnimation(
            modifier = Modifier.fillMaxSize(),
            rippleColor = Color.White.copy(alpha = 0.8f), // 파장 색상 (흰색)
            maxRadiusDp = maxRippleSize / 2, // 파장이 퍼져나가는 최대 반경
            waveDurationMillis = 2500,
            waveIntervalMillis = 2000,
            initialRadius = maxRippleSize / 4 // 파장이 시작할 때 배경 원 밖에서 시작하도록 설정
        )

        if (iconVisible) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .roundedBackgroundWithPadding(
                        defaultTeam6Colors.gray600,
                        cornerRadius = 100.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                TransportVectorIconComposable(
                    type = TransportType.WALK,
                    color = Color.White,
                    isMarker = false,
                    modifier = Modifier
                        .size(18.dp)
                )
            }
        }
    }
}

/**
 * @Composable
 * 동그란 이미지를 중심으로 물결 파장이 퍼져나가는 애니메이션을 그립니다.
 *
 * @param modifier Modifier
 * @param rippleColor 물결의 색상
 * @param maxRadiusDp 물결이 최대로 퍼져나갈 반경
 * @param waveCount 동시에 보여질 물결 파장 개수
 * @param waveDurationMillis 각 물결이 퍼져나가는 총 시간 (밀리초)
 * @param waveIntervalMillis 물결이 새로 생성되는 간격 (밀리초)
 * @param initialRadius 파장이 시작할 때의 초기 반경 (배경 원의 크기)
 */
@Composable
fun RippleWaveAnimation(
    modifier: Modifier = Modifier,
    rippleColor: Color,
    maxRadiusDp: Dp,
    waveDurationMillis: Int,
    waveIntervalMillis: Int,
    initialRadius: Dp = 0.dp
) {
    // 픽셀 값 변환을 위한 LocalDensity
    val density = LocalDensity.current
    val maxRadiusPx = with(density) { maxRadiusDp.toPx() }
    val initialRadiusPx = with(density) { initialRadius.toPx() }

    // 각 물결의 애니메이션 상태를 관리하는 리스트
    val ripples = remember { mutableStateListOf<RippleState>() }

    // 코루틴 스코프 기억 (LaunchedEffect 내부에서 RippleState 애니메이션 시작에 사용)
    val coroutineScope = rememberCoroutineScope()

    // 새로운 물결을 주기적으로 추가하는 LaunchedEffect
    LaunchedEffect(Unit) {
        while (isActive) {
            ripples.add(
                RippleState(
                    coroutineScope,
                    initialRadiusPx,
                    0.5f,
                    maxRadiusPx,
                    waveDurationMillis,
                )
            )
            delay(waveIntervalMillis.toLong())
        }
    }

    // Canvas에 물결 파장들을 그립니다.
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)

        // 각 물결 상태를 반복하며 그리기 및 애니메이션 추적
        ripples.toList().forEachIndexed { index, rippleState ->
            // animate*AsState 대신 Animatable을 사용하여 제어를 단순화
            val currentRadius = rippleState.currentRadius.value
            val currentAlpha = rippleState.currentAlpha.value

            // 애니메이션이 끝나면 리스트에서 제거
            if (currentRadius >= maxRadiusPx) {
                ripples.remove(rippleState)
                return@forEachIndexed // 다음 물결로 이동
            }

            // 초기 반경보다 클 때만 그립니다 (배경 원 밖에서 시작)
            if (currentRadius > initialRadiusPx) {
                drawCircle(
                    color = rippleColor.copy(alpha = currentAlpha),
                    radius = currentRadius,
                    center = center,
                    style = Fill
                )
            }
        }
    }
}

/**
 * 각 물결 파장의 상태와 애니메이션을 관리하는 클래스입니다.
 */
class RippleState(
    coroutineScope: CoroutineScope,
    initialRadiusPx: Float,
    initialAlpha: Float,
    maxRadiusPx: Float,
    durationMillis: Int
) {
    // Animatable을 사용하여 애니메이션 값을 관리합니다.
    val currentRadius = Animatable(initialRadiusPx)
    val currentAlpha = Animatable(initialAlpha) // 1f에서 0f로 투명도 감소

    init {
        coroutineScope.launch {
            // 반경 애니메이션 (initialRadiusPx -> maxRadiusPx)
            currentRadius.animateTo(
                targetValue = maxRadiusPx,
                animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
            )
        }
        coroutineScope.launch {
            // 투명도 애니메이션
            currentAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
            )
        }
    }
}

@Composable
@Preview
fun WalkIconWithRipplePreview() {
    WalkIconWithRipple()
}