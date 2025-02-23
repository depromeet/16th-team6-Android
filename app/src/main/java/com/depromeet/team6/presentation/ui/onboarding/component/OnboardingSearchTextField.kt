package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSearchTextField.MAX_LENGTH
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

object OnboardingSearchTextField {
    const val MAX_LENGTH = 20
}

@Composable
fun OnboardingSearchTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit = { _ -> },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .roundedBackgroundWithPadding(backgroundColor = defaultTeam6Colors.systemGray6, cornerRadius = 8.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            value = value,
            onValueChange = {
                if (it.length <= MAX_LENGTH) onValueChange(it)
            },
            cursorBrush = SolidColor(defaultTeam6Colors.textFieldCursor),
            singleLine = true,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            textStyle = defaultTeam6Typography.bodyRegular17,
            decorationBox = { innerTextField ->
                innerTextField()
                if (value.isEmpty()) {
                    Text(
                        text = "지번, 도로명, 건물명으로 검색",
                        color = defaultTeam6Colors.greyQuaternaryLabel,
                        style = defaultTeam6Typography.bodyRegular17
                    )
                }
            }
        )
        Spacer(modifier = Modifier.width(12.dp))
    }
}

@Preview
@Composable
private fun OnboardingSearchTextFieldPreview() {
    OnboardingSearchTextField()
}
