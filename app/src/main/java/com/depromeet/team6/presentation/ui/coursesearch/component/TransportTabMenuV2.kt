package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.common.AtchaTabRowV2
import com.depromeet.team6.presentation.ui.coursesearch.CourseSearchContract
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun TransportTabMenuV2(
    availableCourses: List<CourseInfo>,
    isLoaded: Boolean,
    courseSearchDataState: CourseSearchContract.CourseSearchDataState,
    modifier: Modifier = Modifier,
    onRegisterAlarmBtnClick: (String) -> Unit = {},
    courseInfoToggleClick: () -> Unit = {},
    onItemClick: (String, Boolean) -> Unit = { _, _ -> }
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(defaultTeam6Colors.gray950)
        ) {
            // TabRow
            AtchaTabRowV2(
                modifier = Modifier
                    .weight(1f),
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
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp),
                text = "늦은 출발순",
                style = defaultTeam6Typography.body8_B8R13,
                color = defaultTeam6Colors.white
            )
        }

        // Tab Content
        HorizontalPager(state = pagerState) { page ->
            val resultItems =
                if (page == 0) {
                    availableCourses
                } else {
                    availableCourses.filter { it.filterCategory == page }
                }
            if (isLoaded) {
                if (courseSearchDataState == CourseSearchContract.CourseSearchDataState.Success &&
                    !isMidNight()
                ) {
                    LastTransportInfoListV2(
                        listData = resultItems,
                        onItemClick = onItemClick,
                        courseInfoToggleClick = courseInfoToggleClick,
                        onRegisterAlarmBtnClick = { routeId ->
                            onRegisterAlarmBtnClick(routeId)
                        }
                    )
                } else {
                    SearchResultEmpty(
                        modifier = Modifier.padding(top = 10.dp),
                        dataLoadState = courseSearchDataState,
                        isMidNight = isMidNight()
                    )
                }
//                if (resultItems.isEmpty() || isMidNight()) {
//                    SearchResultEmpty(
//                        modifier = Modifier.padding(top = 10.dp)
//                    )
//                } else {
//                    LastTransportInfoList(
//                        listData = resultItems,
//                        onItemClick = onItemClick,
//                        courseInfoToggleClick = courseInfoToggleClick,
//                        onRegisterAlarmBtnClick = { routeId ->
//                            onRegisterAlarmBtnClick(routeId)
//                        }
//                    )
//                }
            }
        }
    }
}

private fun isMidNight(): Boolean {
    if (BuildConfig.DEBUG) return false
    val currentTime = LocalTime.now(ZoneId.systemDefault())
    val midnight = LocalTime.MIDNIGHT // 00:00:00
    val fiveAM = LocalTime.of(5, 0, 0) // 05:00:00

    return !currentTime.isBefore(midnight) && currentTime.isBefore(fiveAM)
}

@Preview
@Composable
fun PreviewTabMenu2(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    val mockData = CourseInfo(
        routeId = "123",
        filterCategory = 0,
        totalTime = 23 * 60,
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
    TransportTabMenuV2(
        availableCourses = mockDataList,
        isLoaded = true,
        courseSearchDataState = CourseSearchContract.CourseSearchDataState.Success
    )
}

@Preview(name = "empty")
@Composable
fun PreviewTabMenuEmpty2() {
    TransportTabMenuV2(
        availableCourses = emptyList(),
        isLoaded = false,
        courseSearchDataState = CourseSearchContract.CourseSearchDataState.Success
    )
}
