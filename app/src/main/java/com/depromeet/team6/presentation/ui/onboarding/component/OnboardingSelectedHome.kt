package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun OnboardingSelectedHome(
    onboardingSearchLocation: Location,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 19.dp)
    ) {
        Column {
            Text(
                text = onboardingSearchLocation.name,
                style = defaultTeam6Typography.heading6Bold15,
                color = defaultTeam6Colors.white
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = onboardingSearchLocation.address,
                style = defaultTeam6Typography.bodySemiBold13,
                color = defaultTeam6Colors.greyTertiaryLabel
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingSelectedHomePReview() {
    OnboardingSelectedHome(
        onboardingSearchLocation = Location(
            name = "해지개",
            lat = 0.0,
            lon = 0.0,
            radius = "700m",
            address = "제주 제주시 애월읍 애월북서길 52",
            businessCategory = ""
        )
    )
}
