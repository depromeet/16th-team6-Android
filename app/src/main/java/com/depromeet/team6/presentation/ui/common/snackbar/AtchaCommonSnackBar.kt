package com.depromeet.team6.presentation.ui.common.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.Team6Theme

@Composable
fun AtchaCommonSnackBar(
    modifier: Modifier = Modifier,
    text: String,
    buttonText: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable(onClick)
            .roundedBackgroundWithPadding(
                backgroundColor = Team6Theme.colors.gray930,
                cornerRadius = 12.dp,
                padding = PaddingValues(18.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = Color.White,
            style = Team6Theme.typography.bodySemiBold14
        )

        Spacer(modifier = Modifier.weight(1f))

        if (buttonText != null) {
            Text(
                text = buttonText,
                color = Team6Theme.colors.primaryMain,
                style = Team6Theme.typography.bodySemiBold14
            )
        }

    }
}

@Preview
@Composable
private fun AtchaCommonSnackBarPreview() {

    Column (modifier = Modifier.fillMaxSize().background(Color.White)){
        AtchaCommonSnackBar(
            text = "텍스트",
            buttonText = "텍스트",
            onClick = {}
        )
    }
}