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
fun TextListItemRadio(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
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
            if (isSelected) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_mypage_radio_selected),
                    contentDescription = stringResource(R.string.text_list_item_check_tv),
                    tint = LocalTeam6Colors.current.systemGreen
                )
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_mypage_radio_unselected),
                    contentDescription = stringResource(R.string.text_list_item_radio_unselected_tv),
                    tint = LocalTeam6Colors.current.gray400
                )
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = text,
                style = defaultTeam6Typography.bodyRegular15,
                color = LocalTeam6Colors.current.white,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun TextListItemRadioPreview() {
    TextListItemRadio(
        modifier = Modifier,
        text = "text list",
        isSelected = true,
    )
}