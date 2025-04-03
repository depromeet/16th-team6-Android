package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import java.time.LocalDateTime

@Composable
fun LastTransportInfoItem(
    courseSearchResult: CourseInfo,
    modifier: Modifier = Modifier,
    onRegisterAlarmBtnClick: (lastRouteId: String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(defaultTeam6Colors.greyCard)
            .padding(16.dp)
    ) {
        // 남은 시간
        val remainingHour = courseSearchResult.remainingTime / 60 / 60
        val remainingMinute = courseSearchResult.remainingTime / 60 % 60

        Row {
            if (remainingHour > 0) {
                Text(
                    text = stringResource(
                        id = R.string.last_transport_info_remaining_hour,
                        remainingHour
                    ),
                    style = defaultTeam6Typography.heading3Bold22,
                    color = defaultTeam6Colors.white
                )
                Spacer(
                    modifier = Modifier.width(2.dp)
                )
            }
            if (remainingMinute > 0) {
                Text(
                    text = stringResource(
                        id = R.string.last_transport_info_remaining_minute,
                        remainingMinute
                    ),
                    style = defaultTeam6Typography.heading3Bold22,
                    color = defaultTeam6Colors.white
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(
                    id = R.string.course_detail_description
                ),
                style = defaultTeam6Typography.bodyRegular12,
                color = defaultTeam6Colors.greySecondaryLabel
            )
            Image(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.CenterVertically),
                imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_right_grey),
                contentDescription = "arrow right"
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )

        // 출발-탑승 상세 시각
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val departureDateTime = LocalDateTime
                .parse(courseSearchResult.departureTime)
            val (departHour, departMinute) = departureDateTime.let { it.hour to it.minute }
            val boardingDateTime = LocalDateTime
                .parse(courseSearchResult.boardingTime)
            val (boardingHour, boardingMinute) = boardingDateTime.let { it.hour to it.minute }

            RemainingTimeHHmm(
                hour = departHour,
                minute = departMinute,
                isDeparture = true
            )
            Text(
                style = defaultTeam6Typography.bodyRegular13,
                color = defaultTeam6Colors.greySecondaryLabel,
                text = stringResource(R.string.last_transport_info_departure_time)
            )
            RemainingTimeHHmm(
                hour = boardingHour,
                minute = boardingMinute,
                isDeparture = false
            )
            Text(
                style = defaultTeam6Typography.bodyRegular13,
                color = defaultTeam6Colors.greySecondaryLabel,
                text = stringResource(R.string.last_transport_info_boarding_time)
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )

        // 막차 경로 상세 정보
        TransportCourseInfoExpandable(
            legsInfo = courseSearchResult.legs
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        )

        // 막차 알림 받기 버튼
        SetNotificationButton(
            btnClickEvent = {
                onRegisterAlarmBtnClick(courseSearchResult.routeId)
            }
        )
    }
}

@Composable
fun SetNotificationButton(
    modifier: Modifier = Modifier,
    btnClickEvent: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = defaultTeam6Colors.greyDefaultButton)
            .padding(vertical = 13.dp, horizontal = 28.dp)
            .fillMaxWidth()
            .noRippleClickable {
                btnClickEvent()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(16.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_bottom_sheet_bell_16),
            colorFilter = ColorFilter.tint(defaultTeam6Colors.white),
            contentDescription = "set alarm icon"
        )
        Spacer(
            modifier = Modifier.width(4.dp)
        )
        Text(
            style = defaultTeam6Typography.bodyMedium14,
            color = defaultTeam6Colors.white,
            text = stringResource(R.string.last_transport_info_set_notification)
        )
    }
}

@Composable
fun RemainingTimeHHmm(
    hour: Int,
    minute: Int,
    isDeparture: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isDeparture) {
        defaultTeam6Colors.primaryMain
    } else {
        defaultTeam6Colors.white
    }

    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(defaultTeam6Colors.systemGrey5)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        color = color,
        text = stringResource(R.string.last_transport_info_remaining_time, hour, minute),
        style = defaultTeam6Typography.bodySemiBold12
    )
}

@Preview(name = "more than 1 hour", showBackground = true, backgroundColor = android.graphics.Color.BLACK.toLong())
@Composable
fun LastTransportInfoItemPreview(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    val mockData = CourseInfo(
        routeId = "123",
        filterCategory = 0,
        remainingTime = 83 * 60,
        departureTime = "2025-03-11T23:12:00",
        boardingTime = "2025-03-11T23:21:00",
        legs = courseInfo
    )
    LastTransportInfoItem(
        courseSearchResult = mockData
    )
}

@Preview(name = "less than 1 hour", showBackground = true, backgroundColor = android.graphics.Color.BLACK.toLong())
@Composable
fun LastTransportInfoItemPreview2(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    val mockData = CourseInfo(
        routeId = "123",
        filterCategory = 0,
        remainingTime = 23 * 60,
        departureTime = "2025-03-11T23:12:00",
        boardingTime = "2025-03-11T23:21:00",
        legs = courseInfo
    )
    LastTransportInfoItem(
        courseSearchResult = mockData
    )
}
