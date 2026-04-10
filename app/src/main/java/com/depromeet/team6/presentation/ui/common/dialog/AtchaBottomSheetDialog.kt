package com.depromeet.team6.presentation.ui.common.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun AtchaBottomSheetDialog(
    modifier: Modifier = Modifier,
    locationName: String,
    locationAddress: String,
    completeButtonText: String,
    buttonClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = defaultTeam6Colors.gray940
            )
            .padding(horizontal = 24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        if (locationName.isNotEmpty()) {
            Text(
                text = locationName,
                modifier = Modifier.fillMaxWidth(),
                style = defaultTeam6Typography.heading3_H3SB17,
                color = defaultTeam6Colors.white
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = locationAddress,
                modifier = Modifier.fillMaxWidth(),
                style = defaultTeam6Typography.body6_B6R14,
                color = defaultTeam6Colors.gray200
            )
        } else {
            Text(
                text = locationAddress,
                modifier = Modifier.fillMaxWidth(),
                style = defaultTeam6Typography.heading3_H3SB17,
                color = defaultTeam6Colors.white
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = completeButtonText,
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { buttonClicked() }
                .roundedBackgroundWithPadding(
                    cornerRadius = 9.dp,
                    backgroundColor = defaultTeam6Colors.main,
                    padding = PaddingValues(vertical = 14.dp, horizontal = 28.dp)
                ),
            textAlign = TextAlign.Center,
            style = defaultTeam6Typography.body2_B2SB15,
            color = defaultTeam6Colors.black
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview
@Composable
private fun AtchaBottomSheetDialogPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        AtchaBottomSheetDialog(
            modifier = Modifier.align(Alignment.BottomCenter),
            locationName = "이동하려는 거리가 매우 가까워요",
            locationAddress = "출발지를 확인한 후 다시 검색해 주세요.",
            completeButtonText = "확인",
            buttonClicked = {}
        )
    }
}