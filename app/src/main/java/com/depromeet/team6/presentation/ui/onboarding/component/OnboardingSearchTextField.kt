package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSearchTextField.MAX_LENGTH
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
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
    onCloseButtonClicked: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = modifier
                .weight(1f)
                .roundedBackgroundWithPadding(
                    backgroundColor = defaultTeam6Colors.systemGray6,
                    cornerRadius = 8.dp
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp),
                value = value,
                onValueChange = {
                    if (it.codePointCount(0, it.length) <= MAX_LENGTH) {
                        onValueChange(it)
                    }
                },
                cursorBrush = SolidColor(defaultTeam6Colors.textFieldCursor),
                singleLine = true,
                keyboardActions = keyboardActions,
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                textStyle = defaultTeam6Typography.bodyRegular15.copy(color = defaultTeam6Colors.white),
                decorationBox = { innerTextField ->
                    innerTextField()
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.onboarding_search_text_field_placeholder),
                            color = defaultTeam6Colors.greyQuaternaryLabel,
                            style = defaultTeam6Typography.bodyMedium15
                        )
                    }
                }
            )
        }
        Icon(
            modifier = Modifier.padding(start = 12.dp, top = 18.dp, end = 16.dp, bottom = 18.dp)
                .noRippleClickable { onCloseButtonClicked() },
            imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_close_24),
            tint = Color.Unspecified,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun OnboardingSearchTextFieldPreview() {
    OnboardingSearchTextField()
}
