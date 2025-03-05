package com.depromeet.team6.presentation.ui.course_search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.model.course.WayPoint
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun CourseInfoDetail(
    legsInfo: List<LegInfo>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        legsInfo.forEachIndexed { idx, leg ->
            if (leg.transportType == TransportType.WALK) {
                CourseInfoDetailItem(
                    transportType = TransportType.WALK,
                    duration = leg.sectionTime,
                )
            } else {
                CourseInfoDetailItem(
                    transportType = leg.transportType,
                    duration = leg.sectionTime,
                    boardingPoint = leg.startPoint.name,
                    destinationPoint = leg.endPoint.name
                )
            }
            if (idx != legsInfo.lastIndex) {
                VerticalDashedLine()
            }
        }
    }
}

@Composable
fun VerticalDashedLine(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(2.dp) // 점선의 너비
            .height(18.dp)
            .padding(start = 10.dp)
            .drawBehind {
                val dashHeight = size.height / 6f
                val dashSpacing = size.height / 6f
                var y = -(dashHeight / 2)
                while (y < size.height) {
                    drawLine(
                        color = defaultTeam6Colors.greyQuaternaryLabel,
                        start = Offset(x = size.width / 2, y = y),
                        end = Offset(x = size.width / 2, y = y + dashHeight),
                        strokeWidth = 1f
                    )
                    y += dashHeight + dashSpacing
                }
            }
    )
}

@Preview
@Composable
fun CourseInfoDetailPreview(){
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
        ),
    )
    CourseInfoDetail(
        legsInfo = mockData
    )
}