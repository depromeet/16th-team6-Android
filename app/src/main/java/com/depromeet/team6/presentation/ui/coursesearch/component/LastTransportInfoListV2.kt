package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun LastTransportInfoListV2(
    listData: List<CourseInfo>,
    modifier: Modifier = Modifier,
    onRegisterAlarmBtnClick: (String) -> Unit = {},
    courseInfoToggleClick: () -> Unit = {},
    onItemClick: (String, Boolean) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(defaultTeam6Colors.black),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(listData.size) { index ->
            LastTransportInfoItemV2(
                modifier = Modifier,
                onItemClick = onItemClick,
                courseSearchResult = listData[index],
                onRegisterAlarmBtnClick = { routeId ->
                    onRegisterAlarmBtnClick(routeId)
                }
            )

            if (index == listData.size - 1) {
                Spacer(
                    modifier = Modifier.height(70.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun LastTransportInfoListPreview2(
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
