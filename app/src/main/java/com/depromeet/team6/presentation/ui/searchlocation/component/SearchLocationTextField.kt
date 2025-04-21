package com.depromeet.team6.presentation.ui.searchlocation.component

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
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

object SearchLocationTextField {
    const val MAX_LENGTH = 20
}

@Composable
fun SearchLocationTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit = { _ -> },
    onTextClearButtonClicked: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = modifier
                .weight(1f)
                .roundedBackgroundWithPadding(
                    backgroundColor = defaultTeam6Colors.greyQuaternaryLabel,
                    cornerRadius = 8.dp
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_home_location_green),
                contentDescription = stringResource(R.string.home_icon_search_text),
                tint = defaultTeam6Colors.systemGreen
            )

            Spacer(Modifier.width(10.dp))

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
                cursorBrush = SolidColor(defaultTeam6Colors.greyTertiaryLabel),
                singleLine = true,
                keyboardActions = keyboardActions,
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                textStyle = defaultTeam6Typography.bodyRegular17.copy(color = defaultTeam6Colors.white),
                decorationBox = { innerTextField ->
                    innerTextField()
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.home_search_location_search_text),
                            color = defaultTeam6Colors.greySecondaryLabel,
                            style = defaultTeam6Typography.bodyRegular17
                        )
                    }
                }
            )

            Spacer(Modifier.width(12.dp))

            Icon(
                modifier = Modifier.noRippleClickable { onTextClearButtonClicked() },
                imageVector = ImageVector.vectorResource(R.drawable.ic_search_circle_close),
                tint = defaultTeam6Colors.greySecondaryLabel,
                contentDescription = null
            )
        }

        Spacer(Modifier.width(6.dp))

//        Icon(
//            modifier = Modifier
//                .noRippleClickable { },
//            imageVector = ImageVector.vectorResource(R.drawable.ic_search_list_map_28dp),
//            tint = defaultTeam6Colors.greySecondaryLabel,
//            contentDescription = stringResource(R.string.home_search_map_icon)
//        )
    }
}

@Preview
@Composable
private fun SearchLocationTextFieldPreview() {
    SearchLocationTextField()
}
