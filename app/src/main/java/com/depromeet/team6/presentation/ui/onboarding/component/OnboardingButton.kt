package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun OnboardingButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .roundedBackgroundWithPadding(
                backgroundColor = if (isEnabled) defaultTeam6Colors.main else defaultTeam6Colors.greyDefaultButton,
                cornerRadius = 8.dp,
                padding = PaddingValues(vertical = 14.dp)
            )
            .noRippleClickable { if (isEnabled) onClick() },
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_next_button),
            style = defaultTeam6Typography.heading5SemiBold17,
            color = if (isEnabled) defaultTeam6Colors.black else defaultTeam6Colors.greyQuaternaryLabel
        )
    }
}

@Preview
@Composable
private fun OnboardingButtonPreview() {
    Row {
        OnboardingButton(onClick = {})
    }
}
