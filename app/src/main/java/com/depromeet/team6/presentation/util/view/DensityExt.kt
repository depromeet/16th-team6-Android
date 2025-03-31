package com.depromeet.team6.presentation.util.view

import android.content.Context
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
fun Float.toDp(): Dp {
    val density = LocalDensity.current.density
    return Dp(this / density)
}

@Composable
fun Dp.toPx(): Float {
    val density = LocalDensity.current.density
    return this.value * density
}

fun Dp.toPxWidthContext(context : Context): Float {
    val densityValue = context.resources.displayMetrics.density
    val density = Density(density = densityValue)
    return with(density) { toPx() }
}

fun Path.scaleFromBaseSize(baseSize: Float, targetSize: Int): Path {
    val bounds = RectF()
    this.computeBounds(bounds, true)

    val actualSize = maxOf(bounds.width(), bounds.height())
    if (actualSize == 0f) return this

    val designScale = baseSize / actualSize               // Path 자체의 상대 크기 보정
    val densityScale = targetSize / baseSize              // 비트맵 해상도 대응 배율
    val finalScale = designScale * densityScale

    val matrix = Matrix().apply {
        setScale(finalScale, finalScale)

        // 중앙 정렬
        val dx = (targetSize - bounds.width() * finalScale) / 2f - bounds.left * finalScale
        val dy = (targetSize - bounds.height() * finalScale) / 2f - bounds.top * finalScale
        postTranslate(dx, dy)
    }

    return Path().apply {
        this@scaleFromBaseSize.transform(matrix, this)
    }

}

