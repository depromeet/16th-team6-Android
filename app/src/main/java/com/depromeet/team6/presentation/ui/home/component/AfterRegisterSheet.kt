package com.depromeet.team6.presentation.ui.home.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun AfterRegisterSheet(
    timerFinish: Boolean,
    isConfirmed: Boolean,
    afterUserDeparted: Boolean,
    transportType: TransportType,
    transportationNumber: Int,
    transportationName: String,
    timeToLeave: String,
    boardingTime: String,
    startLocation: String,
    destination: String,
    deleteAlarmConfirmed: () -> Unit = {},
    dismissDialog: () -> Unit = {},
    onCourseTextClick: () -> Unit,
    onFinishClick: () -> Unit,
    onCourseDetailClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onTimerFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    var timeTextColor = colors.systemGrey1
    if (isConfirmed) timeTextColor = colors.white
    if (afterUserDeparted) timeTextColor = colors.systemRed

    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(248.dp)
            .zIndex(1f),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    colors.greyWashBackground,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // 절대적 위치 지정을 위한 Box 사용
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp) // 헤더 영역 높이 지정
            ) {
                // 기본 텍스트 내용 (항상 왼쪽에 표시)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    if (afterUserDeparted && !timerFinish) { // 사용자 출발 후
                        TransportStatus(
                            transportationType = transportType,
                            transportationNumber = transportationNumber,
                            transportationName = transportationName,
                            stopLeft = 6
                        )
                    } else if (afterUserDeparted && timerFinish) {
                        Text(
                            text = stringResource(R.string.home_final_departure_time_text),
                            style = typography.bodyMedium13,
                            color = colors.white
                        )
                    } else if (isConfirmed) {
                        Text(
                            text = stringResource(R.string.home_start_time_text),
                            style = typography.bodyMedium13,
                            color = colors.white
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.home_expect_start_time_text),
                            style = typography.bodyMedium13,
                            color = colors.white
                        )
                    }

                    if (!afterUserDeparted) {
                        var iconClicked by remember { mutableStateOf(false) }

                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_all_info_grey),
                            contentDescription = stringResource(R.string.home_icon_info),
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clickable {
                                    iconClicked = !iconClicked
                                    Log.d("클릭", "클릭하다.")
                                },
                            tint = colors.systemGrey1
                        )
                    }
                }

                // 새로고침 버튼 (조건부로 오른쪽에 표시)
                if ((isConfirmed || afterUserDeparted) && !timerFinish) {
                    RefreshLottieButton(
                        onClick = onRefreshClick,
                        tint = colors.white,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterEnd) // Box 내부 오른쪽에 정렬
                            .padding(end = 5.dp)
                    )
                }
            }

            // 사용자가 잠금화면 출발하기 버튼 눌러서 출발했을 때
            if (afterUserDeparted) {
                if (!timerFinish) {
                    LastTimer(
                        departureTime = boardingTime,
                        textColor = colors.systemRed,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        onTimerFinished = onTimerFinished
                    )
                } else {
                    TimeText(
                        timeToLeave = timeToLeave,
                        textColor = colors.white,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
            } else {
                TimeText(
                    timeToLeave = timeToLeave,
                    textColor = timeTextColor,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            CourseTextButton(
                startLocation = startLocation,
                destination = destination,
                onClick = onCourseTextClick,
                modifier = modifier
            )

            Spacer(modifier = modifier.height(26.dp))

            FinishCourseDetailButton(
                onFinishClick = {
                    onFinishClick()
                },
                onCourseDetailClick = onCourseDetailClick,
                modifier = modifier
            )
        }
    }
}

@Preview
@Composable
fun AfterRegisterSheetPreview() {
    AfterRegisterSheet(
        timerFinish = false,  // 새로고침 버튼이 보이도록 false로 설정
        afterUserDeparted = true,  // 새로고침 버튼이 보이도록 true로 설정
        transportType = TransportType.BUS,
        transportationNumber = 0,
        transportationName = "잠실새내역",
        timeToLeave = "23:30:00",
        startLocation = "중앙빌딩",
        destination = "우리집",
        onCourseTextClick = { },
        onFinishClick = { },
        onCourseDetailClick = { },
        modifier = Modifier,
        onRefreshClick = { },
        isConfirmed = false,
        boardingTime = "15:30:00",
        deleteAlarmConfirmed = {},
        dismissDialog = {},
        onTimerFinished = {},
    )
}