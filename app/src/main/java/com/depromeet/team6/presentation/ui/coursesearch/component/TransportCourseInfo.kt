package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun TransportCourseInfo(
    legsInfo: List<LegInfo>,
    modifier: Modifier = Modifier
) {
    // 대중교통 타입 + 마지막 종착역 정보만 필터링 해서 표시
    val displayableLegs = legsInfo.filter { it.transportType != TransportType.WALK }
    Column {
        displayableLegs.forEachIndexed { index, legInfo ->
            if (legInfo.transportType != TransportType.WALK) {
                TimelineItem(
                    courseInfo = legInfo,
                    isFirst = (index == 0)
                )
            }
        }
        TimelineItem(
            courseInfo = legsInfo.last(),
            isLast = true
        )
    }
}

@Composable
private fun TimelineItem(
    courseInfo: LegInfo,
    isLast: Boolean = false, // 마지막 항목은 아래로 가는 선을 그리지 않음
    isFirst: Boolean = false // 첫 항목은 위로 가는 선을 그리지 않음
) {
    val title = if (isLast) courseInfo.endPoint.name else courseInfo.startPoint.name
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min), // 💡 중요: Row의 높이를 내부 콘텐츠(텍스트)에 맞춤
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 왼쪽 아이콘 및 세로선 영역
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val color = defaultTeam6Colors.gray910
                    val centerX = size.width / 2

//                    // 위쪽 선 (첫 번째 아이템이 아닐 때만)
                    if (!isFirst) {
                        drawLine(
                            color = color,
                            start = Offset(centerX, 0f),
                            end = Offset(centerX, centerX), // 아이콘 중앙 전까지
                            strokeWidth = strokeWidth
                        )
                    }

                    // 아래쪽 선 (마지막 아이템이 아닐 때만)
                    if (!isLast) {
                        drawLine(
                            color = color,
                            start = Offset(centerX, centerX), // 아이콘 중앙 이후부터
                            end = Offset(centerX, size.height),
                            strokeWidth = strokeWidth
                        )
                    }
                },
            contentAlignment = Alignment.TopCenter
        ) {
            if (isLast) {
                Spacer(
                    modifier = Modifier
                        .size(22.dp)
                        .padding(8.dp)
                        .background(
                            color = defaultTeam6Colors.gray910,
                            shape = RoundedCornerShape(999.dp)
                        )
                )
            } else {
                Image(
                    imageVector = ImageVector.vectorResource(id = TransportTypeUiMapper.getIconResId(courseInfo.transportType, courseInfo.subTypeIdx)),
                    contentDescription = "Transport Icon",
                    modifier = Modifier
                        .size(22.dp)
                )
            }
        }

        // 오른쪽 텍스트 영역
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 10.dp, top = 2.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = title,
                style = defaultTeam6Typography.detail1_R12,
                color = defaultTeam6Colors.white
            )

            if (courseInfo.transportType == TransportType.BUS) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier
                        .background(
                            color = TransportTypeUiMapper.getColor(TransportType.BUS, courseInfo.subTypeIdx),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = courseInfo.routeName.toString().split(":")[1].trim(),
                    style = defaultTeam6Typography.body2_B2SB15.copy(fontSize = 12.sp),
                    color = defaultTeam6Colors.white
                )
            }
        }
    }
}

@Preview
@Composable
fun TimeLineItemPreview() {
    TimelineItem(
        courseInfo =
        LegInfo(
            transportType = TransportType.SUBWAY,
            subTypeIdx = 109,
            routeName = "경기 : 302",
            sectionTime = 7,
            departureDateTime = "2023-06-06T23:17:00",
            startPoint = Address(
                name = "신논현역",
                lat = 0.1,
                lon = 0.1,
                address = ""
            ),
            endPoint = Address(
                name = "지하철2호선방배역",
                lat = 0.0,
                lon = 0.0,
                address = ""
            ),
            distance = 10,
            passShape = "127.02481,37.504562 127.024666,37.50452"
        ),
        isFirst = true
    )
}

@Preview
@Composable
fun TimeLineItemPreview2() {
    TimelineItem(
        courseInfo =
        LegInfo(
            transportType = TransportType.BUS,
            subTypeIdx = 11,
            routeName = "경기 : 302",
            sectionTime = 7,
            departureDateTime = "2023-06-06T23:17:00",
            startPoint = Address(
                name = "중간 역",
                lat = 0.1,
                lon = 0.1,
                address = ""
            ),
            endPoint = Address(
                name = "지하철2호선방배역",
                lat = 0.0,
                lon = 0.0,
                address = ""
            ),
            distance = 10,
            passShape = "127.02481,37.504562 127.024666,37.50452"
        ),
        isFirst = false
    )
}

@Preview
@Composable
fun TimeLineItemPreview3() {
    TimelineItem(
        courseInfo =
        LegInfo(
            transportType = TransportType.BUS,
            subTypeIdx = 11,
            routeName = "경기 : 302",
            sectionTime = 7,
            departureDateTime = "2023-06-06T23:17:00",
            startPoint = Address(
                name = "종착 역",
                lat = 0.1,
                lon = 0.1,
                address = ""
            ),
            endPoint = Address(
                name = "지하철2호선방배역",
                lat = 0.0,
                lon = 0.0,
                address = ""
            ),
            distance = 10,
            passShape = "127.02481,37.504562 127.024666,37.50452"
        ),
        isFirst = false,
        isLast = true
    )
}

@Preview
@Composable
fun TransportCourseInfoPreview2(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    TransportCourseInfo(
        legsInfo = courseInfo
    )
}
