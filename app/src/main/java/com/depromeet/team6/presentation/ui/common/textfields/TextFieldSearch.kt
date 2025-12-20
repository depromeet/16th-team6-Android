package com.depromeet.team6.presentation.ui.common.textfields

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchLocationTextField.MAX_LENGTH
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun TextFieldSearch(
    modifier: Modifier = Modifier,
    value: String = "",
    hintText: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit = { _ -> },
    onTextClearButtonClicked: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier
                    .weight(1f),
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
                textStyle = defaultTeam6Typography.body4_B4R15.copy(color = defaultTeam6Colors.white),
                decorationBox = { innerTextField ->
                    innerTextField()
                    if (value.isEmpty()) {
                        Text(
                            text = hintText,
                            color = defaultTeam6Colors.gray400,
                            style = defaultTeam6Typography.body4_B4R15
                        )
                    }
                }
            )

            if (value.isNotEmpty()) {
                Spacer(Modifier.width(12.dp))
                Icon(
                    modifier = Modifier.noRippleClickable { onTextClearButtonClicked() }, // ★
                    imageVector = ImageVector.vectorResource(R.drawable.ic_search_circle_close),
                    tint = defaultTeam6Colors.gray200,
                    contentDescription = stringResource(R.string.textfield_search_clear)
                )
            }
        }
    }
}

@Preview
@Composable
private fun TextFieldSearchPreview() {
    TextFieldSearch(
        hintText = "지번, 도로명, 건물명으로 검색"
    )
}
