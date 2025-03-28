package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.launch

@Composable
fun TransportTabMenu(
    availableCourses: List<CourseInfo>,
    modifier: Modifier = Modifier,
    onRegisterAlarmBtnClick: () -> Unit = {},
    onItemClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val tabItems = context.resources.getStringArray(R.array.course_search_tab_items).toList()

    Column(
        modifier = modifier
            .background(defaultTeam6Colors.black),
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
                    pagerState.animateScrollToPage(
                        page = tabIndex,
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 0,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        )

        // Tab Content
        HorizontalPager(state = pagerState) { page ->
            val resultItems =
                if (page == 0) availableCourses
                else availableCourses.filter { it.filterCategory == page }

            if (resultItems.isEmpty()) {
                SearchResultEmpty(
                    modifier = Modifier.padding(top = 10.dp)
                )
            } else {
                LastTransportInfoList(
                    listData = resultItems,
                    onItemClick = {
                        onItemClick()
                    },
                    onRegisterAlarmBtnClick = {
                        onRegisterAlarmBtnClick()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTabMenu(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    val mockData = CourseInfo(
        routeId = "123",
        filterCategory = 0,
        remainingTime = 23 * 60,
        departureTime = "2025-03-11 23:12:00",
        boardingTime = "2025-03-11 23:21:00",
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

@Preview(name = "empty")
@Composable
fun PreviewTabMenuEmpty() {
    TransportTabMenu(
        availableCourses = emptyList()
    )
}