package com.depromeet.team6.presentation.ui.mypage.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun MypageSelectedHome(
    homeLocation: Address,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .noRippleClickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 23.dp)
        ) {
            if (homeLocation.name.isNotEmpty()) {
                Text(
                    text = homeLocation.name,
                    style = defaultTeam6Typography.heading6Bold15,
                    color = defaultTeam6Colors.white
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = homeLocation.address,
                    style = defaultTeam6Typography.bodySemiBold13,
                    color = defaultTeam6Colors.greyTertiaryLabel
                )
            } else {
                Text(
                    text = homeLocation.address,
                    style = defaultTeam6Typography.heading6Bold15,
                    color = defaultTeam6Colors.white
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = defaultTeam6Colors.greyDefaultButton,
                    shape = RoundedCornerShape(8.dp)
                )
                .roundedBackgroundWithPadding(
                    cornerRadius = 8.dp,
                    padding = PaddingValues(vertical = 11.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.mypage_change_home_button_text),
                style = defaultTeam6Typography.bodyRegular14,
                color = defaultTeam6Colors.white
            )
        }
    }
}

@Preview
@Composable
private fun MypageSelectedHomePreview() {
    MypageSelectedHome(
        homeLocation = Address(
            name = "압구정 토끼굴",
            lat = 0.0,
            lon = 0.0,
            address = "서울시 강남구 논현로 339 1층"
        ),
        onClick = {}
    )
}