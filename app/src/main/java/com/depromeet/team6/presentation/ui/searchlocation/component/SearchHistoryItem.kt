package com.depromeet.team6.presentation.ui.searchlocation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.HomeSearchLocation
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun SearchHistoryItem(
    homeSearchLocation: HomeSearchLocation,
    modifier: Modifier = Modifier,
    deleteButtonClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 19.dp, horizontal = 16.dp),
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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = homeSearchLocation.distance,
                    style = defaultTeam6Typography.bodyRegular14,
                    color = defaultTeam6Colors.greySecondaryLabel,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(6.dp))

                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_home_location_grey_3dp),
                    contentDescription = stringResource(R.string.home_search_dot_icon),
                    tint = defaultTeam6Colors.white
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = homeSearchLocation.roadAddress,
                    style = defaultTeam6Typography.bodyRegular14,
                    color = defaultTeam6Colors.greySecondaryLabel,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_search_list_close_grey),
            contentDescription = stringResource(R.string.home_icon_search_text),
            tint = defaultTeam6Colors.greyTertiaryLabel,
            modifier = Modifier.noRippleClickable {
                deleteButtonClicked()
            }
        )
    }
}

@Preview
@Composable
fun SearchHistoryItemPreview() {
    SearchHistoryItem(
        homeSearchLocation = HomeSearchLocation(
            name = "60계 치킨 강남점",
            distance = "1.9km",
            roadAddress = "강남구 테헤란로 4길 6"
        )
    )
}
