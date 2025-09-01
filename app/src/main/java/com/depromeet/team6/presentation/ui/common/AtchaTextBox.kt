package com.depromeet.team6.presentation.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun AtchaTextBox(
    hintMessage : String,
    modifier : Modifier = Modifier
) {
    val textState = rememberTextFieldState()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .height(110.dp)
            .roundedBackgroundWithPadding(
                backgroundColor = defaultTeam6Colors.gray940,
                cornerRadius = 10.dp,
                padding = PaddingValues(16.dp)
            ),
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth(),
            state = textState,
            cursorBrush = SolidColor(defaultTeam6Colors.white),
            textStyle = defaultTeam6Typography.body6_B6R14.copy(
                color = defaultTeam6Colors.white
            ),
            decorator = { innerTextField ->
                innerTextField()
                if (textState.text.isEmpty()) {
                    Text(
                        modifier = Modifier,
                        text = hintMessage,
                        style = defaultTeam6Typography.body6_B6R14,
                        color = defaultTeam6Colors.gray400
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun AtchaTextBoxPreview() {
    AtchaTextBox(
        hintMessage = "탈퇴 이유에 대해 자세히 알려주시면 서비스 개선에 큰 도움이 돼요."
    )
}