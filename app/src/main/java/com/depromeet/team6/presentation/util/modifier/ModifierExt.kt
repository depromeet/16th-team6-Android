package com.depromeet.team6.presentation.util.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit = {}
): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

fun Modifier.roundedBackgroundWithPadding(
    backgroundColor: Color = Color.Unspecified,
    cornerRadius: Dp = 0.dp,
    padding: PaddingValues = PaddingValues(0.dp)
): Modifier {
    return this
        .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
        .padding(padding)
}
