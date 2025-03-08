package com.depromeet.team6.presentation.ui.searchlocation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun SearchDepartureTextField(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = defaultTeam6Colors.greyElevatedBackground
            )
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_home_location_green),
                contentDescription = stringResource(R.string.home_icon_search_text),
                tint = defaultTeam6Colors.white
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = stringResource(R.string.home_search_departure_home_text),
                color = defaultTeam6Colors.white,
                style = defaultTeam6Typography.bodyRegular17
            )
        }
    }
}

@Preview
@Composable
fun SearchDepartureTextFieldPreview() {
    SearchDepartureTextField()
}