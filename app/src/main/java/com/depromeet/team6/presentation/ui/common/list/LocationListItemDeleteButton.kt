package com.depromeet.team6.presentation.ui.common.list

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
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun LocationListItemDeleteButton(
    location: Location,
    modifier: Modifier = Modifier,
    deleteButtonClicked: (Location) -> Unit = {},
    selectItemClicked: (Location) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 19.dp, horizontal = 16.dp)
            .noRippleClickable {
                selectItemClicked(location)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = modifier.weight(1f)) {
            Text(
                text = location.name,
                color = defaultTeam6Colors.white,
                style = defaultTeam6Typography.bodyRegular15,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = location.radius,
                    style = defaultTeam6Typography.bodyRegular14,
                    color = defaultTeam6Colors.gray200,
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
                    text = location.address,
                    style = defaultTeam6Typography.bodyRegular14,
                    color = defaultTeam6Colors.gray200,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_search_list_close_grey),
            contentDescription = stringResource(R.string.home_icon_search_text),
            tint = defaultTeam6Colors.gray400,
            modifier = Modifier.noRippleClickable {
                deleteButtonClicked(location)
            }
        )
    }
}

@Preview
@Composable
fun LocationListItemDeleteButtonPreview() {
    LocationListItemDeleteButton(
        location = Location(
            name = "60계 치킨 강남점",
            lat = 0.0,
            lon = 0.0,
            radius = "1.9km",
            address = "강남구 테헤란로 4길 6",
            businessCategory = ""
        )
    )
}
