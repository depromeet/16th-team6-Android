package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.location.Location
import com.depromeet.team6.presentation.util.modifier.pressedEffectClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun OnboardingSearchLocationItem(
    onboardingSearchLocation: Location,
    modifier: Modifier = Modifier,
    selectButtonClicked: () -> Unit = {}
) {
    val padding = if (onboardingSearchLocation.businessCategory.startsWith("지역")) {
        PaddingValues(vertical = 16.dp, horizontal = 16.dp)
    } else {
        PaddingValues(vertical = 19.dp, horizontal = 16.dp)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .pressedEffectClickable { selectButtonClicked() }
            .padding(padding)
    ) {
        Text(
            text = onboardingSearchLocation.name,
            color = defaultTeam6Colors.white,
            style = defaultTeam6Typography.bodyRegular15,
            overflow = TextOverflow.Ellipsis
        )
        if (!onboardingSearchLocation.businessCategory.startsWith("지역")) {
            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = onboardingSearchLocation.radius,
                    style = defaultTeam6Typography.bodyRegular14,
                    color = defaultTeam6Colors.greySecondaryLabel
                )
                Icon(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_all_adrress_devider),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Text(
                    text = onboardingSearchLocation.address,
                    style = defaultTeam6Typography.bodyRegular14,
                    color = defaultTeam6Colors.greySecondaryLabel,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingSearchLocationItemPreview() {
    OnboardingSearchLocationItem(
        onboardingSearchLocation = Location(
            name = "60계 치킨 강남정",
            lat = 0.0,
            lon = 0.0,
            radius = "1.9km",
            address = "강남구 테헤란로 4길 6",
            businessCategory = ""
        )
    )
}
