package com.depromeet.team6.presentation.ui.common.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.type.ButtonSize
import com.depromeet.team6.presentation.type.ButtonType
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.Team6Theme

@Composable
fun AtchaCommonButton(
    buttonType: ButtonType,
    buttonSize: ButtonSize,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = "버튼"
) {
    Box(
        modifier = modifier
            .then(
                if (buttonType.stroke > 0.dp && buttonType.strokeColor != null) {
                    Modifier.border(
                        width = buttonType.stroke,
                        color = buttonType.strokeColor,
                        shape = RoundedCornerShape(buttonSize.roundPadding)
                    )
                } else {
                    Modifier
                }
            )
            .roundedBackgroundWithPadding(
                backgroundColor = buttonType.backGroundColor.copy(alpha = buttonType.backgroundAlpha),
                cornerRadius = buttonSize.roundPadding,
                padding = PaddingValues(vertical = buttonSize.verticalPadding)
            )
            .noRippleClickable(onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buttonText,
            color = buttonType.textColor,
            style = buttonSize.textStyle
        )
    }
}

@Preview
@Composable
private fun AtchaCommonButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Team6Theme.colors.gray930),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AtchaCommonButton(
                modifier = Modifier.weight(1f),
                buttonType = ButtonType.PRIMARY,
                buttonSize = ButtonSize.LARGE,
                buttonText = "버튼",
                onClick = {}
            )
            AtchaCommonButton(
                modifier = Modifier.weight(1f),
                buttonType = ButtonType.WHITE,
                buttonSize = ButtonSize.LARGE,
                buttonText = "버튼",
                onClick = {}
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AtchaCommonButton(
                modifier = Modifier.weight(1f),
                buttonType = ButtonType.GRAY,
                buttonSize = ButtonSize.LARGE,
                buttonText = "버튼",
                onClick = {}
            )
            AtchaCommonButton(
                modifier = Modifier.weight(1f),
                buttonType = ButtonType.TONAL,
                buttonSize = ButtonSize.LARGE,
                buttonText = "버튼",
                onClick = {}
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AtchaCommonButton(
                modifier = Modifier.weight(1f),
                buttonType = ButtonType.DISABLED,
                buttonSize = ButtonSize.LARGE,
                buttonText = "버튼",
                onClick = {}
            )
            AtchaCommonButton(
                modifier = Modifier.weight(1f),
                buttonType = ButtonType.OUTLINE,
                buttonSize = ButtonSize.LARGE,
                buttonText = "버튼",
                onClick = {}
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AtchaCommonButton(
                modifier = Modifier.weight(1f),
                buttonType = ButtonType.OUTLINE_DISABLED,
                buttonSize = ButtonSize.LARGE,
                buttonText = "버튼",
                onClick = {}
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
