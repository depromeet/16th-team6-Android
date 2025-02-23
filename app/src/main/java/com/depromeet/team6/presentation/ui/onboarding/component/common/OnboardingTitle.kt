package com.depromeet.team6.presentation.ui.onboarding.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun OnboardingTitle(onboardingType: OnboardingType, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = onboardingType.titleStringRes),
            style = defaultTeam6Typography.heading2Bold26,
            color = defaultTeam6Colors.white
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = onboardingType.subTitleStringRes),
            style = defaultTeam6Typography.bodyRegular15,
            color = defaultTeam6Colors.greySecondaryLabel
        )
    }
}

@Preview
@Composable
private fun OnboardingTitlePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = defaultTeam6Colors.black)
    ) {
        OnboardingTitle(OnboardingType.HOME)
        OnboardingTitle(OnboardingType.ALARM)
    }
}
