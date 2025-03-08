package com.depromeet.team6.presentation.ui.course

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.model.course.WayPoint
import com.depromeet.team6.presentation.ui.course.component.CourseAppBar
import com.depromeet.team6.presentation.ui.course.component.DestinationSearchBar
import com.depromeet.team6.presentation.ui.course.component.TransportTabMenu
import com.depromeet.team6.presentation.util.view.SnackbarController
import com.depromeet.team6.presentation.util.view.SnackbarEvent
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.launch

@Composable
fun CourseScreen(
    modifier: Modifier = Modifier,
    courseData: List<LastTransportInfo> = emptyList()
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<CourseViewModel>()
    // SideEffect 감지 및 Toast 띄우기
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is CourseContract.CourseSideEffect.ShowNotificationToast -> {
                    Toast.makeText(context, context.getString(R.string.course_set_notification_snackbar), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = modifier
            .background(defaultTeam6Colors.greyWashBackground)
    ) {
        CourseAppBar()
        DestinationSearchBar(
            startingPoint = "서울역",
            destination = "강남역",
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )
        val coroutineScope = rememberCoroutineScope()
        Button(onClick = {
            // 스낵바를 보여주는 로직
            coroutineScope.launch {
                SnackbarController.sendEvent(SnackbarEvent(message = "뽀잉 내려왔다가 올라가는 애니메이션!"))
            }
        }) {
            Text("Show Custom Snackbar")
        }

        TransportTabMenu(
            availableCourses = courseData
        )
    }
}

@Preview
@Composable
fun CourseScreenPreview() {
    // TODO: mocking 없애고 실제 데이터 들어가야함
    val courseInfo = listOf(
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
            distance = 10,
            passShape = "127.02481,37.504562 127.024666,37.50452"
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
            distance = 57,
            passShape = "127.025675,37.501708 127.026528,37.499994 127.026856,37.499408 127.027367,37.498353 127.027525,37.498025 127.027589,37.497892 127.027717,37.497525 127.028253,37.496306 127.028436,37.495922 127.029794,37.493072 127.029858,37.492939 127.030497,37.491664 127.031522,37.489619 127.031586,37.489483 127.032458,37.487656 127.033683,37.485086 127.033819,37.484811 127.033928,37.484633 127.034036,37.484503 127.034319,37.484181 127.034628,37.483844 127.034800,37.483678 127.037200,37.481392 127.037383,37.481169 127.037661,37.480753 127.038047,37.480053 127.038289,37.479453 127.038389,37.479136"
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
            distance = 37,
            passShape = "127.0381,37.479004 127.03806,37.4791 127.03799,37.479313"
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
            distance = 10,
            passShape = "127.03799,37.479313 127.037636,37.479263 127.037445,37.47924"
        )
    )

    val mockData = LastTransportInfo(
        remainingMinutes = 23,
        departureHour = 23,
        departureMinute = 3,
        boardingHour = 23,
        boardingMinute = 15,
        legs = courseInfo
    )
    val mockDataList = listOf(
        mockData,
        mockData,
        mockData,
        mockData
    )

    CourseScreen(
        courseData = mockDataList
    )
}
