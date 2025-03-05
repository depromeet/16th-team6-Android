package com.depromeet.team6.presentation.ui.course.component

import android.util.Log
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
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.model.course.WayPoint
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
                    Log.d("expeandaenfsdf", expanded.toString())
                },
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_arrow_down_grey),
            contentDescription = "arrow down"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransportCourseInfoPreview() {
    val mockData = listOf(
        LegInfo(
            transportType = TransportType.WALK,
            sectionTime = 7,
            startPoint = WayPoint(
                name = "화서역",
                latitude = 0.1,
                longitude = 0.1
            ),
            endPoint = WayPoint(
                name = "지하철2호선방배역",
                latitude = 0.0,
                longitude = 0.0
            ),
            routeColor = defaultTeam6Colors.black,
            distance = 10
        ),
        LegInfo(
            transportType = TransportType.BUS,
            sectionTime = 27,
            startPoint = WayPoint(
                name = "수원 KT위즈파크",
                latitude = 0.1,
                longitude = 0.1
            ),
            endPoint = WayPoint(
                name = "사당역 2호선",
                latitude = 0.0,
                longitude = 0.0
            ),
            routeColor = defaultTeam6Colors.systemGreen,
            distance = 57
        ),
        LegInfo(
            transportType = TransportType.SUBWAY,
            sectionTime = 17,
            startPoint = WayPoint(
                name = "사당역 2호선",
                latitude = 0.1,
                longitude = 0.1
            ),
            endPoint = WayPoint(
                name = "강남역 신분당선",
                latitude = 0.0,
                longitude = 0.0
            ),
            routeColor = defaultTeam6Colors.primaryRed,
            distance = 37
        ),
        LegInfo(
            transportType = TransportType.WALK,
            sectionTime = 13,
            startPoint = WayPoint(
                name = "강남역 신분당선",
                latitude = 0.1,
                longitude = 0.1
            ),
            endPoint = WayPoint(
                name = "할리스 커피 강남1호점",
                latitude = 0.0,
                longitude = 0.0
            ),
            routeColor = defaultTeam6Colors.black,
            distance = 10
        )
    )

    TransportCourseInfoExpandable(
        legsInfo = mockData
    )
}
