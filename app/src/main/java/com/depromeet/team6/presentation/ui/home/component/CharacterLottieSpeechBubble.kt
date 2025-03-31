package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable

@Composable
fun CharacterLottieSpeechBubble(
    prefixText: String,
    modifier: Modifier = Modifier,
    emphasisText: String? = null,
    suffixText: String? = null,
    onClick: () -> Unit = {},
    showSpeechBubble: Boolean = true,
    lottieResId: Int = R.raw.character_alarm_not_registered
) {

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(lottieResId)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1 // 한 번만 재생
    )

    Column(
        modifier = modifier.noRippleClickable { onClick() }
    ) {
        if (showSpeechBubble) {
            SpeechBubble(
                prefix = prefixText,
                modifier = Modifier,
                emphasisText = emphasisText,
                suffix = suffixText
            )

            Spacer(modifier = Modifier.height(2.dp))
        }

        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
//        Image(
//            imageVector = ImageVector.vectorResource(R.drawable.ic_all_acha_character),
//            contentDescription = stringResource(R.string.all_acha_character)
//        )
    }
}

@Preview
@Composable
fun CharacterLottieSpeechBubblePreview() {
    CharacterSpeechBubble(
        prefixText = "여기서 놓치면 택시비",
        modifier = Modifier,
        emphasisText = "34,000"
    )
}
