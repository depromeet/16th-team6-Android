package com.depromeet.team6.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

/**
 * Compose에서 기본 제공되는 Image 컴포넌트는 UnStable 하기 때문에 불필요한 리컴포지션을 유발합니다.
 * 비트맵 이미지를 로드하게 된다면 StableImage 컴포넌트를 사용해주세요
 * Unstable 하게 여겨지는 Painter 객체를 파라미터로 받지 않아 Stable한 이미지 컴포넌트입니다.
 */
@Composable
fun StableImage(
    drawableResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val painter = painterResource(id = drawableResId)
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
    )
}
