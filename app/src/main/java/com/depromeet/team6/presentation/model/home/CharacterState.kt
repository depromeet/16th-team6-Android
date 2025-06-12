package com.depromeet.team6.presentation.model.home

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R

data class CharacterState(
    val speechTexts: List<SpeechBubbleData> = emptyList(),
    val currentSpeechIndex: Int = 0,
    val lottieResId: Int = R.raw.atcha_character_1,
    val isAnimating: Boolean = false,
    val bottomPadding: Dp = 194.dp,
    val animationTrigger: Int = 0
)

data class SpeechBubbleData(
    val prefixText: String = "",
    val emphasisText: String? = null,
    val suffixText: String? = null,
    val topPrefixText: String? = null,
    val topEmphasisText: String? = null,
    val topSuffixText: String? = null,
    val lineCount: Int = 1
)

enum class ComponentType {
    DEPARTURE_TIME_NOT_CONFIRMED_CLICKED,
    DEPARTURE_TIME_CONFIRMED_CLICKED,
    ROUTE_TEXT_CLICKED
}
