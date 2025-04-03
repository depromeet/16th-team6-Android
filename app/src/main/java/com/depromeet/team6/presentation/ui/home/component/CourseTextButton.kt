package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .noRippleClickable(onClick = onClick),
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
            style = typography.bodyRegular15,
            color = colors.systemGrey1
        )

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_right_white),
            contentDescription = stringResource(R.string.home_icon_arrow_right_text),
            modifier = Modifier
                .padding(horizontal = 8.dp),
            tint = colors.systemGrey1
        )

        Text(
            text = destination,
            style = typography.bodyRegular15,
            color = colors.systemGrey1
        )
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
