package com.depromeet.team6.presentation.ui.coursesearch.component

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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
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
                    subtypeIndex = leg.subTypeIdx,
                    duration = leg.sectionTime
                )
            } else {
                CourseInfoDetailItem(
                    transportType = leg.transportType,
                    subtypeIndex = leg.subTypeIdx,
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
                        strokeWidth = 1.dp.toPx()
                    )
                    y += dashHeight + dashSpacing
                }
            }
    )
}

@Preview
@Composable
fun CourseInfoDetailPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    CourseInfoDetail(
        legsInfo = legs
    )
}
