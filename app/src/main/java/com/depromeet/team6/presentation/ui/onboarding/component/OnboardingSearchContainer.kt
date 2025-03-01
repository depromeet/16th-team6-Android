package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.depromeet.team6.R
import com.depromeet.team6.presentation.type.OnboardingSelectLocationButtonType
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun OnboardingSearchContainer(
    modifier: Modifier = Modifier,
    onSearchBoxClicked: () -> Unit = {},
    onLocationButtonClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .roundedBackgroundWithPadding(
                    backgroundColor = defaultTeam6Colors.systemGray6,
                    cornerRadius = 8.dp
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .noRippleClickable {
                    onSearchBoxClicked()
                }
        ) {
            Text(
                text = stringResource(R.string.onboarding_search_text_field_placeholder),
                color = defaultTeam6Colors.greyQuaternaryLabel,
                style = defaultTeam6Typography.bodyRegular17
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 2.dp)
        ) {
            OnboardingSelectLocationButton(
                searchLocationButtonType = OnboardingSelectLocationButtonType.SEARCH,
                onClick = onLocationButtonClick
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingSearchContainerPreview() {
    OnboardingSearchContainer()
}
