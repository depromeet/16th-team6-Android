package com.depromeet.team6.presentation.ui.course.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun DestinationSearchBar(
    startingPoint: String,
    destination: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = defaultTeam6Colors.greyElevatedCard,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO : typography 디자인 시스템 적용
        Text(
            text = startingPoint,
            style = defaultTeam6Typography.bodyRegular15,
            color = defaultTeam6Colors.white,
        )

        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_course_search_arrow_right_white),
            contentDescription = "arrow right",
            modifier = Modifier.size(12.dp)
        )

        // TODO : typography 디자인 시스템 적용
        Text(
            text = destination,
            style = defaultTeam6Typography.bodyRegular15,
            color = defaultTeam6Colors.white,
        )
    }
}

@Preview
@Composable
fun PreviewDestinationSearchBar() {
    DestinationSearchBar(
        startingPoint = "마루 180",
        destination = "집"
    )
}
