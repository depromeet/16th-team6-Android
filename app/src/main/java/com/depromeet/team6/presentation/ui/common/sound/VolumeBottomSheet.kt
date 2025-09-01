package com.depromeet.team6.presentation.ui.common.sound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun VolumeBottomSheet(
    modifier: Modifier = Modifier,
    onButtonClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = defaultTeam6Colors.gray940
            ),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.volume_bottom_sheet_title_tv),
                style = defaultTeam6Typography.heading3_H3SB17,
                color = LocalTeam6Colors.current.white
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.volume_bottom_sheet_setting_tv),
                style = defaultTeam6Typography.body6_B6R14,
                color = LocalTeam6Colors.current.gray200
            )

            Spacer(modifier = Modifier.height(24.dp))

            VolumeBar()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.volume_bottom_sheet_setting_btn_tv),
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { onButtonClicked() }
                    .roundedBackgroundWithPadding(
                        cornerRadius = 10.dp,
                        backgroundColor = defaultTeam6Colors.main,
                        padding = PaddingValues(vertical = 14.dp, horizontal = 28.dp)
                    ),
                textAlign = TextAlign.Center,
                style = defaultTeam6Typography.heading6Bold15,
                color = defaultTeam6Colors.black
            )
        }
    }
}

@Preview
@Composable
fun VolumeBottomSheetPreview() {
    VolumeBottomSheet(
        onButtonClicked = {}
    )
}
