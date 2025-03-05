package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.HomeSearchLocation
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun SearchLocationItem(
    homeSearchLocation: HomeSearchLocation,
    modifier: Modifier = Modifier,
    selectButtonClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 19.dp, horizontal = 16.dp)
            .noRippleClickable {
                selectButtonClicked()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = modifier.weight(1f)) {
            Text(
                text = homeSearchLocation.name,
                color = defaultTeam6Colors.white,
                style = defaultTeam6Typography.bodyRegular15,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = homeSearchLocation.distance + "" + homeSearchLocation.roadAddress,
                style = defaultTeam6Typography.bodyRegular14,
                color = defaultTeam6Colors.greySecondaryLabel,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "선택",
            color = defaultTeam6Colors.white,
            style = defaultTeam6Typography.bodyMedium13,
            modifier = Modifier
                .roundedBackgroundWithPadding(
                    backgroundColor = defaultTeam6Colors.greyButton,
                    cornerRadius = 8.dp,
                    padding = PaddingValues(vertical = 8.dp, horizontal = 12.5.dp)
                )
        )
    }
}

@Preview
@Composable
private fun SearchLocationItemPreview() {
    SearchLocationItem(
        homeSearchLocation = HomeSearchLocation(
            name = "60계 치킨 강남정",
            distance = "1.9km",
            roadAddress = "강남구 테헤란로 4길 6"
        )
    )
}
