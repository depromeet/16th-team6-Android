package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun CourseTextButton(
    startLocation: String,
    destination: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = LocalTeam6Typography.current
    val colors = LocalTeam6Colors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    colors.systemGray5,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = colors.systemGray,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_all_location_grey),
                contentDescription = stringResource(R.string.home_icon_location_text),
                modifier = Modifier
                    .padding(end = 6.dp),
                tint = colors.white
            )

            Text(
                text = startLocation,
                style = typography.bodyRegular14,
                color = colors.white
            )

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_right_white),
                contentDescription = stringResource(R.string.home_icon_arrow_right_text),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp),
                tint = colors.white
            )

            Text(
                text = destination,
                style = typography.bodyRegular14,
                color = colors.white
            )

        }
    }
}

@Preview
@Composable
fun CourseTextButtonPreview() {
    CourseTextButton(
        startLocation = "중앙빌딩",
        destination = "우리집",
        onClick = { }
    )
}
