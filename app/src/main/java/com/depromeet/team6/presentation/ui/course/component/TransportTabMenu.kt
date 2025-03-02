package com.depromeet.team6.presentation.ui.course.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.model.course.TransportCourseInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.launch

@Composable
fun TransportTabMenu(
    availableCourses: List<LastTransportInfo>,
    modifier: Modifier = Modifier
) {
    val tabItems = listOf("전체", "버스", "지하철")

    Column(
        modifier = modifier
            .background(defaultTeam6Colors.greyElevatedBackground),
        verticalArrangement = Arrangement.Center
    ) {
        val pagerState = rememberPagerState {
            tabItems.size
        }
        val coroutineScope = rememberCoroutineScope()

        // TabRow
        TransportTabRow(
            tabs = tabItems,
            selectedTabIndex = pagerState.currentPage,
            onTabClick = { tabIndex ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(tabIndex)
                }
            }
        )

        // Tab Content
        HorizontalPager(state = pagerState) { page ->
            // TODO : 버스/지하철 정렬 기능 추가해야함
            LastTransportInfoList(
                listData = availableCourses
            )
        }
    }
}

@Preview
@Composable
fun PreviewTabMenu() {
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
    TransportTabMenu(
        availableCourses = mockDataList
    )
}
