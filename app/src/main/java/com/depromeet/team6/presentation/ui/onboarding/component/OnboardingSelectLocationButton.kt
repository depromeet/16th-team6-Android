package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.type.OnboardingSelectLocationButtonType
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun OnboardingSelectLocationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    searchLocationButtonType: OnboardingSelectLocationButtonType
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = defaultTeam6Colors.greyDefaultButton,
                shape = RoundedCornerShape(8.dp)
            )
            .roundedBackgroundWithPadding(
                cornerRadius = 8.dp,
                padding = PaddingValues(vertical = 11.dp)
            )
            .noRippleClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (searchLocationButtonType == OnboardingSelectLocationButtonType.SEARCH) {
            Icon(
                modifier = Modifier.size(12.dp),
                painter = painterResource(id = R.drawable.ic_all_location_white),
                tint = Color.Unspecified,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = stringResource(
                id =
                if (searchLocationButtonType == OnboardingSelectLocationButtonType.SEARCH) {
                    R.string.onboarding_select_location_button_current_region
                } else {
                    R.string.onboarding_edit_location_button_current_region
                }
            ),
            style = defaultTeam6Typography.bodyRegular14,
            color = defaultTeam6Colors.white
        )
    }
}

@Preview
@Composable
private fun OnboardingSelectLocationButtonPreview() {
    Row {
        OnboardingSelectLocationButton(
            onClick = {},
            searchLocationButtonType = OnboardingSelectLocationButtonType.SEARCH
        )
    }
}
