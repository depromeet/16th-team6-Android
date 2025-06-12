package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.depromeet.team6.presentation.model.home.CharacterState
import com.depromeet.team6.presentation.model.home.SpeechBubbleData

@Composable
fun UnifiedCharacterBubble(
    characterState: CharacterState,
    onCharacterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSpeech = if (characterState.speechTexts.isNotEmpty()) {
        characterState.speechTexts[characterState.currentSpeechIndex]
    } else {
        SpeechBubbleData()
    }

    CharacterLottieSpeechBubble(
        prefixText = currentSpeech.prefixText,
        emphasisText = currentSpeech.emphasisText,
        suffixText = currentSpeech.suffixText,
        topPrefixText = currentSpeech.topPrefixText,
        topEmphasisText = currentSpeech.topEmphasisText,
        topSuffixText = currentSpeech.topSuffixText,
        lineCount = currentSpeech.lineCount,
        onClick = onCharacterClick,
        lottieResId = characterState.lottieResId,
        externalTrigger = characterState.animationTrigger, // 변경
        modifier = modifier
    )
}