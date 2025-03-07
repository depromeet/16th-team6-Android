package com.depromeet.team6.presentation.ui.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun MypageListItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = LocalTeam6Typography.current
    val colors = LocalTeam6Colors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                colors.greyWashBackground
            )
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = typography.bodyRegular15,
                color = colors.white
            )

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_right_grey),
                contentDescription = stringResource(R.string.home_icon_search_text),
                tint = colors.greyTertiaryLabel,
                modifier = modifier
            )

        }
    }
}

@Preview
@Composable
fun MypageListItemPreview() {
    MypageListItem(
        title = "내 계정",
        onClick = {},
        modifier = Modifier
    )
}
