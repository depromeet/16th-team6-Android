package com.depromeet.team6.presentation.ui.common.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun TextListItemCheck(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = defaultTeam6Typography.body4_B4R15,
                color = LocalTeam6Colors.current.white,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )

            if (isChecked) {
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_select_alarm),
                    contentDescription = stringResource(R.string.text_list_item_check_tv),
                    tint = LocalTeam6Colors.current.systemGreen
                )
            }
        }
    }
}

@Preview
@Composable
fun TextListItemCheckPreview() {
    TextListItemCheck(
        modifier = Modifier,
        text = "text list",
        isChecked = true
    )
}
