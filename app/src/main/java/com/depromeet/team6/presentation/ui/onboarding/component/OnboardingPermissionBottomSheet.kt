package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.type.OnboardingPermissionType
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun OnboardingPermissionBottomSheet(
    onboardingPermissionType: OnboardingPermissionType,
    modifier: Modifier = Modifier,
    bottomSheetVisible: Boolean = false,
    buttonClicked: () -> Unit = {}
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
                text = stringResource(onboardingPermissionType.titleStringRes),
                modifier = Modifier.fillMaxWidth(),
                style = defaultTeam6Typography.heading4Bold20,
                color = defaultTeam6Colors.white
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = ImageVector.vectorResource(onboardingPermissionType.iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(onboardingPermissionType.typeStringRes),
                        style = defaultTeam6Typography.heading6SemiBold15,
                        color = defaultTeam6Colors.white
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(onboardingPermissionType.subTitleStringRes),
                        style = defaultTeam6Typography.bodyRegular13,
                        color = defaultTeam6Colors.greySecondaryLabel
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "확인",
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
}

@Preview
@Composable
private fun OnboardingPermissionBottomSheetPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingPermissionBottomSheet(
            onboardingPermissionType = OnboardingPermissionType.LOCATION,
            bottomSheetVisible = true,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
