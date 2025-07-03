package com.depromeet.team6.presentation.ui.common.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.Team6Theme

@Composable
fun AtChaTreeDotsLoadingView(
    isLoading: Boolean,
    loadingText: String = "로딩 중",
    interval: Int = 600,
    modifier: Modifier = Modifier
) {
    val dotCount = 3
    val activeDotIndex =
        rememberInfiniteTransition(label = "")
            .animateValue(
                initialValue = 0,
                targetValue = dotCount,
                typeConverter = Int.VectorConverter,
                animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = interval, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = ""
            )

    Column(
        modifier =
        modifier
            .fillMaxWidth()
            .roundedBackgroundWithPadding(
                cornerRadius = 20.dp,
                backgroundColor = Team6Theme.colors.gray940,
                padding = PaddingValues(horizontal = 24.dp, vertical = 30.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = loadingText,
            color = Team6Theme.colors.white,
            style = Team6Theme.typography.heading5Bold17
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally)
        ) {
            repeat(dotCount) { index ->
                val isActive = isLoading && index == activeDotIndex.value % dotCount
                val offsetY by animateDpAsState(
                    targetValue = if (isActive) (-6).dp else 0.dp,
                    animationSpec = tween(300),
                    label = ""
                )
                val color by animateColorAsState(
                    targetValue = if (isActive) Team6Theme.colors.main else Team6Theme.colors.gray400,
                    animationSpec = tween(300),
                    label = ""
                )

                Spacer(
                    modifier =
                    Modifier
                        .offset(y = offsetY)
                        .size(8.dp)
                        .background(color = color, shape = CircleShape)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AtChaTreeDotsLoadingViewPreview() {
    Team6Theme {
        AtChaTreeDotsLoadingView(
            isLoading = true
        )
    }
}
