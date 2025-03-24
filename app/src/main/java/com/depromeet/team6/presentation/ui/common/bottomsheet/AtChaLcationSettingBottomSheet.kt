package com.depromeet.team6.presentation.ui.common.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun AtChaLocationSettingBottomSheet(
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
                color = defaultTeam6Colors.greyElevatedBackground
            )
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        if (locationName.isNotEmpty()) {
            Text(
                text = locationName,
                modifier = Modifier.fillMaxWidth(),
                style = defaultTeam6Typography.heading5SemiBold17,
                color = defaultTeam6Colors.white
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = locationAddress,
                modifier = Modifier.fillMaxWidth(),
                style = defaultTeam6Typography.bodyRegular14,
                color = defaultTeam6Colors.greySecondaryLabel
            )
        } else {
            Text(
                text = locationAddress,
                modifier = Modifier.fillMaxWidth(),
                style = defaultTeam6Typography.heading5SemiBold17,
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
            style = defaultTeam6Typography.heading6Bold15,
            color = defaultTeam6Colors.black
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview
@Composable
private fun OnboardingPermissionBottomSheetPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        AtChaLocationSettingBottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter),
            locationName = "60계 치킨 강남점",
            locationAddress = "서울 어딘가 어디게 어딜까",
            completeButtonText = "우리집 등록",
            buttonClicked = {}
        )
    }
}
