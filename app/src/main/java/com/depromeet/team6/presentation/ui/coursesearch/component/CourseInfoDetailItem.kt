package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun CourseInfoDetailItem(
    transportType: TransportType,
    subtypeIndex: Int,
    duration: Int,
    modifier: Modifier = Modifier,
    boardingPoint: String = "",
    destinationPoint: String = "",
    eta: Int = 0
) {
    val context = LocalContext.current
    if (transportType == TransportType.WALK) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(26.dp),
                imageVector = ImageVector.vectorResource(TransportTypeUiMapper.getCourseInfoIconId(transportType, subtypeIndex)),
                contentDescription = "transport course icon"
            )
            Text(
                text = context.resources.getString(R.string.course_detail_info_walk),
                style = defaultTeam6Typography.bodyRegular13,
                color = defaultTeam6Colors.white
            )
            Text(
                text = context.resources.getString(R.string.course_detail_info_duration, duration),
                style = defaultTeam6Typography.bodyRegular12,
                color = defaultTeam6Colors.greySecondaryLabel
            )
        }
    } else {
        Row(
            modifier = Modifier.padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(26.dp),
                    imageVector = ImageVector.vectorResource(TransportTypeUiMapper.getCourseInfoIconId(transportType, subtypeIndex)),
                    contentDescription = "transport course icon"
                )
                VerticalLine()
                GetOffMark(
                    modifier = Modifier
                        .size(14.dp),
                    color = TransportTypeUiMapper.getColor(transportType, subtypeIndex)
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = 2.dp)
            ) {
                Text(
                    text = context.resources.getString(R.string.course_detail_info_boarding_point, boardingPoint),
                    style = defaultTeam6Typography.bodyRegular13,
                    color = defaultTeam6Colors.white
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = context.resources.getString(R.string.course_detail_info_duration, duration),
                    style = defaultTeam6Typography.bodyRegular12,
                    color = defaultTeam6Colors.greyTertiaryLabel
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = context.resources.getString(R.string.course_detail_info_destination_point, destinationPoint),
                    style = defaultTeam6Typography.bodyRegular13,
                    color = defaultTeam6Colors.white
                )
            }
        }
    }
}

@Composable
fun VerticalLine(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(2.dp) // 점선의 너비
            .height(18.dp)
            .drawBehind {
                val y = 0f
                drawLine(
                    color = defaultTeam6Colors.greyQuaternaryLabel,
                    start = Offset(x = size.width / 2, y = y),
                    end = Offset(x = size.width / 2, y = y + size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
    )
}

@Composable
fun GetOffMark(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        // 내부 흰색 채우기
        drawCircle(
            color = Color.White,
            radius = 5.5.dp.toPx(), // 동일한 반지름 사용
            center = center,
            style = Fill
        )
        // 테두리 그리기
        drawCircle(
            color = color,
            radius = 5.5.dp.toPx(),
            center = center,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Preview(name = "walk")
@Composable
fun CourseInfoDetailWalk() {
    CourseInfoDetailItem(
        transportType = TransportType.WALK,
        subtypeIndex = 0,
        duration = 5
    )
}

@Preview(name = "bus")
@Composable
fun CourseInfoDetailNonWalk() {
    CourseInfoDetailItem(
        transportType = TransportType.BUS,
        subtypeIndex = 4,
        duration = 23,
        boardingPoint = "총지사",
        destinationPoint = "지하철2호선방배역"
    )
}
