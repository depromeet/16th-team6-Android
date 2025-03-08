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
