package com.depromeet.team6.presentation.ui.course.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.launch

@Composable
fun TransportTabMenu(
    availableCourses: List<LastTransportInfo>,
    modifier: Modifier = Modifier,
    onRegisterAlarmBtnClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val tabItems = context.resources.getStringArray(R.array.course_search_tab_items).toList()

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
fun PreviewTabMenu(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
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
    TransportTabMenu(
        availableCourses = mockDataList
    )
}
