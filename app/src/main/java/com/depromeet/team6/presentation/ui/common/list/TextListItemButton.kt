package com.depromeet.team6.presentation.ui.common.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun TextListItemButton(
    modifier: Modifier = Modifier,
    text: String,
    btnText: String,
    onBtnClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    LocalTeam6Colors.current.gray950
                )
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = defaultTeam6Typography.bodyRegular15,
                color = LocalTeam6Colors.current.white,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            Text(
                modifier = Modifier
                    .roundedBackgroundWithPadding(
                        backgroundColor = LocalTeam6Colors.current.gray910,
                        cornerRadius = 8.dp,
                        padding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                    )
                    .noRippleClickable {
                        onBtnClick()
                    },
                text = btnText,
                color = LocalTeam6Colors.current.white,
                style = defaultTeam6Typography.bodyRegular14
            )
        }
    }
}

@Preview
@Composable
fun TextListItemButtonPreview() {
    TextListItemButton(
        modifier = Modifier,
        text = "text list text list text list text list text list text list text list",
        btnText = "Button",
        onBtnClick = {}
    )
}
