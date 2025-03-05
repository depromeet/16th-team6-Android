package com.depromeet.team6.presentation.util.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
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