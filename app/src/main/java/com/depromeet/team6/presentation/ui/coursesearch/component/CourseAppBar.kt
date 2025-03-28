package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun CourseAppBar(
    modifier: Modifier = Modifier,
    backButtonClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(vertical = 18.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_grey),
            contentDescription = "appbar back",
            tint = defaultTeam6Colors.systemGrey1,
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable {
                    backButtonClicked()
                }
        )

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_close_24),
            contentDescription = "appbar home",
            tint = defaultTeam6Colors.systemGrey1,
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable {
                    backButtonClicked()
                }
        )
    }
}

@Preview
@Composable
fun PreviewAppBar() {
    CourseAppBar()
}
