package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun TransportCourseInfoExpandable(
    legsInfo: List<LegInfo>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .clip(if (expanded) RoundedCornerShape(12.dp) else RoundedCornerShape(8.dp))
            .background(defaultTeam6Colors.systemGrey5)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        if (!expanded) {
            CourseInfoSimple(
                legs = legsInfo
            )
        } else {
            CourseInfoDetail(
                legsInfo = legsInfo
            )
        }

        Image(
            modifier = Modifier
                .size(16.dp)
                .clickable {
                    expanded = !expanded
                }
                .align(Alignment.CenterVertically),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_arrow_down_grey),
            contentDescription = "arrow down"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransportCourseInfoPreview(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    TransportCourseInfoExpandable(
        legsInfo = courseInfo
    )
}
