package com.depromeet.team6.presentation.ui.onboarding.component

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun OnboardingPermissionDeniedBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetVisible: Boolean = false,
    settingButtonClicked: () -> Unit = {},
    completeButtonClicked: () -> Unit = {}
) {
    if (bottomSheetVisible) {
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
            Text(
                text = stringResource(R.string.onboarding_location_permission_denied_bottom_sheet_title),
                modifier = Modifier.fillMaxWidth(),
                style = defaultTeam6Typography.heading4Bold20,
                color = defaultTeam6Colors.white
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.onboarding_location_permission_denied_bottom_sheet_description),
                style = defaultTeam6Typography.bodyRegular15,
                color = defaultTeam6Colors.white
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "설정열기",
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { settingButtonClicked() }
                    .roundedBackgroundWithPadding(
                        cornerRadius = 9.dp,
                        backgroundColor = defaultTeam6Colors.main,
                        padding = PaddingValues(vertical = 14.dp, horizontal = 28.dp)
                    ),
                textAlign = TextAlign.Center,
                style = defaultTeam6Typography.heading6Bold15,
                color = defaultTeam6Colors.black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "완료",
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { completeButtonClicked() }
                    .padding(vertical = 14.dp),
                textAlign = TextAlign.Center,
                style = defaultTeam6Typography.bodyRegular15,
                color = defaultTeam6Colors.white
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
private fun OnboardingPermissionBottomSheetPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingPermissionDeniedBottomSheet(
            bottomSheetVisible = true
        )
    }
}
