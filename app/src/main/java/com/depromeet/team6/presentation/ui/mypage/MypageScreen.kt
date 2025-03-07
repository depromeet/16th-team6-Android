package com.depromeet.team6.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.depromeet.team6.presentation.ui.mypage.component.MypageListItem
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun MypageScreen(
    modifier: Modifier = Modifier
) {
    val typography = LocalTeam6Typography.current
    val colors = LocalTeam6Colors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.greyWashBackground)
    ) {
        Column {
            Box(
                modifier = modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_grey),
                    contentDescription = stringResource(R.string.mypage_icon_arrow_text),
                    tint = colors.systemGrey,
                    modifier = modifier
                        .padding(vertical = 18.dp, horizontal = 16.dp)
                )

                Text(
                    text = stringResource(R.string.mypage_title_text),
                    style = typography.heading5SemiBold17,
                    color = colors.white,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(18.dp)
                )
            }

            MypageListItem(
                title = stringResource(R.string.mypage_account_title_text),
                onClick = { },
                modifier = Modifier
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(colors.black))

            MypageListItem(
                title = stringResource(R.string.mypage_change_home_title_text),
                onClick = { },
                modifier = Modifier
            )

            MypageListItem(
                title = stringResource(R.string.mypage_alarm_title_text),
                onClick = { },
                modifier = Modifier
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(colors.black))

            MypageListItem(
                title = stringResource(R.string.mypage_policy_text),
                onClick = { },
                modifier = Modifier
            )

            MypageListItem(
                title = stringResource(R.string.mypage_version_title_text),
                onClick = { },
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
fun MypageScreenPreview() {
    MypageScreen()
}
