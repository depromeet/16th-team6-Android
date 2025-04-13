package com.depromeet.team6.presentation.ui.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun MypageVersionItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = LocalTeam6Typography.current
    val colors = LocalTeam6Colors.current

    val currentVersion = BuildConfig.VERSION_NAME

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                colors.greyWashBackground
            )
            .noRippleClickable(onClick = onClick)
    ) {
        Row(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title + currentVersion,
                style = typography.bodyRegular15,
                color = colors.white
            )

            Row(
                modifier = modifier
                    .roundedBackgroundWithPadding(
                        backgroundColor = colors.greyDefaultButton,
                        cornerRadius = 8.dp,
                        padding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                    )
                    .noRippleClickable {
                        onClick()
                    },
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(id = R.string.mypage_version_update_text),
                    style = defaultTeam6Typography.bodyMedium13,
                    color = defaultTeam6Colors.white
                )
            }

        }
    }
}

@Preview
@Composable
fun MypageVersionItemPreview() {
    MypageVersionItem(
        title = "내 계정",
        onClick = {},
        modifier = Modifier
    )
}
