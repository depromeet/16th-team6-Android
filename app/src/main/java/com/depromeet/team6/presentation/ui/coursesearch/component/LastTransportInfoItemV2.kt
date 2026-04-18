package com.depromeet.team6.presentation.ui.coursesearch.component

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.ui.itinerary.component.SummaryBarChart
import com.depromeet.team6.presentation.util.dialog.DialogController
import com.depromeet.team6.presentation.util.dialog.LocalDialogController
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import com.google.gson.Gson
import timber.log.Timber
import java.time.LocalDateTime

@Composable
fun LastTransportInfoItemV2(
    courseSearchResult: CourseInfo,
    modifier: Modifier = Modifier,
    onRegisterAlarmBtnClick: (lastRouteId: String) -> Unit = {},
    onItemClick: (String, Boolean) -> Unit = { _, _ -> }
) {
    val dialogController = LocalDialogController.current
    val context = LocalContext.current

    val departureDateTime = runCatching {
        LocalDateTime.parse(courseSearchResult.departureTime)
    }.onFailure {
        Timber.e(it, "Failed to parse departureTime in LastTransportInfoItemV2 for routeId: ${courseSearchResult.routeId}")
    }.getOrNull()

    val boardingDateTime = runCatching {
        LocalDateTime.parse(courseSearchResult.boardingTime)
    }.onFailure {
        Timber.e(it, "Failed to parse boardingTime in LastTransportInfoItemV2 for routeId: ${courseSearchResult.routeId}")
    }.getOrNull()

    Column(
        modifier = modifier
            .background(defaultTeam6Colors.gray950)
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .clickable {
                onItemClick(
                    Gson().toJson(courseSearchResult),
                    false
                )
            }
    ) {
        // 남은 시간
        val remainingHour = courseSearchResult.totalTime / 60 / 60
        val remainingMinute = courseSearchResult.totalTime / 60 % 60

        Row {
            val remainingTimeText = buildString {
                if (remainingHour > 0) {
                    append(stringResource(id = R.string.last_transport_info_remaining_hour, remainingHour))
                    if (remainingMinute > 0) append(" ") // 시간과 분 사이에 공백 추가
                }
                if (remainingMinute > 0 || remainingHour == 0) { // 분이 있거나, 0분인 경우(필요시) 표시
                    append(stringResource(id = R.string.last_transport_info_remaining_minute, remainingMinute))
                }
            }

            Text(
                text = remainingTimeText,
                style = defaultTeam6Typography.heading1_H1B22,
                color = defaultTeam6Colors.white
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )

        // 출발-탑승 상세 시각
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (departHour, departMinute) = departureDateTime?.let { it.hour to it.minute } ?: (null to null)

            RemainingTimeHHmmV2(
                hour = departHour,
                minute = departMinute,
                isDeparture = true
            )
            Text(
                style = defaultTeam6Typography.body7_B7M13,
                color = defaultTeam6Colors.gray200,
                text = "에 자리에서 출발"
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        SummaryBarChart(
            legs = courseSearchResult.legs
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )

        // 막차 경로 상세 정보
        TransportCourseInfo(
            legsInfo = courseSearchResult.legs
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        )

        // 막차 알림 받기 버튼
        SetNotificationButtonV2(
            btnClickEvent = {
                if (hasLongTerm(courseSearchResult.legs)) {
                    dialogController.showAtchaTwoButtonAlert(
                        message = context.getString(R.string.course_search_long_term_alert),
                        onConfirm = {
                            onRegisterAlarmBtnClick(courseSearchResult.routeId)
                        },
                        confirmButtonText = context.getString(R.string.last_transport_info_set_notification_dialog),
                        closeButtonText = context.getString(R.string.dialog_finish_alarm_back_text)
                    )
                } else {
                    onRegisterAlarmBtnClick(courseSearchResult.routeId)
                }
            }
        )
    }
}

@Composable
fun SetNotificationButtonV2(
    modifier: Modifier = Modifier,
    btnClickEvent: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = defaultTeam6Colors.gray910)
            .noRippleClickable {
                btnClickEvent()
            }
            .padding(vertical = 13.dp, horizontal = 28.dp)
            .fillMaxWidth(),
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
            style = defaultTeam6Typography.body6_B6R14,
            color = defaultTeam6Colors.white,
            text = stringResource(R.string.last_transport_info_set_notification)
        )
    }
}

@Composable
fun RemainingTimeHHmmV2(
    hour: Int?,
    minute: Int?,
    isDeparture: Boolean,
    modifier: Modifier = Modifier
) {
    val timeText = if (hour != null && minute != null) {
        stringResource(R.string.last_transport_info_remaining_time, hour, minute)
    } else {
        "--:--"
    }
    Text(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = defaultTeam6Colors.gray500,
                shape = RoundedCornerShape(
                    size = 8.dp
                )
            )
            .background(
                color = defaultTeam6Colors.gray930,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 4.dp, horizontal = 6.dp),
        color = defaultTeam6Colors.white,
        text = timeText,
        style = defaultTeam6Typography.body8_B8R13
    )
}

@Composable
private fun LatestCourseMarker() {
    Text(
        modifier = Modifier
            .background(
                color = defaultTeam6Colors.greenButtonOpacity.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 16.dp, horizontal = 12.dp),
        text = "가장 늦은 막차",
        color = defaultTeam6Colors.main
    )
}

private fun hasLongTerm(legs: List<LegInfo>): Boolean {
    for (leg in legs) {
        if (leg.targetBusTerm == null) continue
        if (leg.targetBusTerm > 40) return true
    }
    return false
}

@Preview(name = "more than 1 hour", showBackground = true, backgroundColor = Color.BLACK.toLong())
@Composable
fun LastTransportInfoItemPreviewV2(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    CompositionLocalProvider(LocalDialogController provides DialogController()) {
        val mockData = CourseInfo(
            routeId = "123",
            filterCategory = 0,
            totalTime = 83 * 60,
            departureTime = "2025-03-11T23:12:00",
            boardingTime = "2025-03-11T23:21:00",
            legs = courseInfo
        )
        LastTransportInfoItemV2(
            courseSearchResult = mockData
        )
    }
}

@Preview(name = "less than 1 hour", showBackground = true, backgroundColor = Color.BLACK.toLong())
@Composable
fun LastTransportInfoItemPreview2V2(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    CompositionLocalProvider(LocalDialogController provides DialogController()) {
        val mockData = CourseInfo(
            routeId = "123",
            filterCategory = 0,
            totalTime = 23 * 60,
            departureTime = "2025-03-11T23:12:00",
            boardingTime = "2025-03-11T23:21:00",
            legs = courseInfo
        )
        LastTransportInfoItemV2(
            courseSearchResult = mockData
        )
    }
}
