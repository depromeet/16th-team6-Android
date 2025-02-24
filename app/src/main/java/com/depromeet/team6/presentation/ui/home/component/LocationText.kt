package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun LocationText(
    locationTitle: String,
    location: String,
    textColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = LocalTeam6Typography.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    backgroundColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_home_location_green),
                contentDescription = stringResource(R.string.home_icon_search_text),
                tint = textColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = locationTitle,
                style = typography.bodyMedium15,
                color = textColor
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = location,
                style = typography.bodyMedium15,
                color = textColor
            )
        }
    }
}

@Preview
@Composable
fun LocationTextPreview() {
    LocationText(
        locationTitle = "현위치:",
        location = "중앙빌딩",
        textColor = LocalTeam6Colors.current.systemGreen,
        backgroundColor = LocalTeam6Colors.current.greyButton,
        onClick = {},
        modifier = Modifier
    )
}
