package com.depromeet.team6.presentation.ui.common.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun LocationListItem(
    address: Address,
    modifier: Modifier = Modifier,
    selectButtonClicked: (Address) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 19.dp, horizontal = 16.dp)
            .noRippleClickable {
                selectButtonClicked(address)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = modifier.weight(1f)) {
            Text(
                text = address.name,
                color = defaultTeam6Colors.white,
                style = defaultTeam6Typography.bodyRegular15,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = address.address,
                style = defaultTeam6Typography.bodyRegular14,
                color = defaultTeam6Colors.gray200,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun LocationListItemPreview() {
    LocationListItem(
        address = Address(
            name = "60계 치킨 강남점",
            lat = 0.0,
            lon = 0.0,
            address = "강남구 테레란로 4길 6"
        )
    )
}
