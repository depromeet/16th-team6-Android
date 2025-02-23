package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R

@Composable
fun CharacterSpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
    taxiCost: String? = null
) {
    Column(
        modifier = modifier
    ) {
        SpeechBubble(
            text = text,
            modifier = Modifier,
            taxiCost = taxiCost
        )

        Spacer(modifier = Modifier.height(2.dp))

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_acha_character),
            contentDescription = stringResource(R.string.all_acha_character)
        )
    }
}

@Preview
@Composable
fun CharacterSpeechBubblePreview() {
    CharacterSpeechBubble(
        text = "여기서 놓치면 택시비",
        modifier = Modifier,
        taxiCost = "34,000"
    )
}