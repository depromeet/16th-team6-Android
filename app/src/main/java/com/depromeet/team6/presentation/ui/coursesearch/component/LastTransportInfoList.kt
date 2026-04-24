package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun LastTransportInfoList(
    listData: List<CourseInfo>,
    modifier: Modifier = Modifier,
    onRegisterAlarmBtnClick: (String) -> Unit = {},
    courseInfoToggleClick: () -> Unit = {},
    onItemClick: (String, Boolean) -> Unit = { _, _ -> }
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listData.size) {
        if (listState.firstVisibleItemIndex == 0) {
            listState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(defaultTeam6Colors.black)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(count = listData.size, key = { listData[it].routeId }) { index ->
            LastTransportInfoItem(
                modifier = Modifier.animateItem(),
                onItemClick = onItemClick,
                courseSearchResult = listData[index],
                onRegisterAlarmBtnClick = { routeId ->
                    onRegisterAlarmBtnClick(routeId)
                },
                courseInfoToggleClick = courseInfoToggleClick
            )
        }
        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Preview
@Composable
fun LastTransportInfoListPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    val mockData = CourseInfo(
        routeId = "123",
        filterCategory = 0,
        totalTime = 23 * 60,
        departureTime = "2025-03-11T23:12:00",
        boardingTime = "2025-03-11T23:21:00",
        legs = legs
    )
    val mockDataList = listOf(
        mockData,
        mockData,
        mockData,
        mockData,
        mockData,
        mockData
    )
    LastTransportInfoList(
        listData = mockDataList
    )
}
