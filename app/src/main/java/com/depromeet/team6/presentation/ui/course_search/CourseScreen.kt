package com.depromeet.team6.presentation.ui.course_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.model.course.TransportCourseInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.ui.course_search.component.CourseAppBar
import com.depromeet.team6.presentation.ui.course_search.component.DestinationSearchBar
import com.depromeet.team6.presentation.ui.course_search.component.TransportTabMenu
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun CourseScreen(
    modifier: Modifier = Modifier,
    courseData: List<LastTransportInfo> = emptyList()
) {
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
        TransportCourseInfo(
            type = TransportType.WALK,
            subTypeIdx = 0,
            durationMinutes = 10
        ),
        TransportCourseInfo(
            type = TransportType.BUS,
            subTypeIdx = 0,
            durationMinutes = 23
        ),
        TransportCourseInfo(
            type = TransportType.SUBWAY,
            subTypeIdx = 2,
            durationMinutes = 14
        )
    )

    val mockData = LastTransportInfo(
        remainingMinutes = 23,
        departureHour = 23,
        departureMinute = 3,
        boardingHour = 23,
        boardingMinute = 15,
        transportCourseInfo = courseInfo
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
