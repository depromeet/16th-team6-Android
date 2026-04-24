package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.course.CourseInfo
import java.time.LocalDateTime
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.dialog.DialogController
import com.depromeet.team6.presentation.util.dialog.LocalDialogController
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun LastTransportInfoListV2(
    listData: List<CourseInfo>,
    modifier: Modifier = Modifier,
    onRegisterAlarmBtnClick: (String) -> Unit = {},
    courseInfoToggleClick: () -> Unit = {},
    onItemClick: (String, Boolean) -> Unit = { _, _ -> }
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0,
        initialFirstVisibleItemScrollOffset = 0
    )

    LaunchedEffect(listData) {
        listState.animateScrollToItem(0)
    }

    val latestRouteId = remember(listData) {
        listData.maxByOrNull { runCatching { LocalDateTime.parse(it.departureTime) }.getOrElse { LocalDateTime.MIN } }?.routeId
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(defaultTeam6Colors.black),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(count = listData.size, key = { listData[it].routeId }) { index ->
            LastTransportInfoItemV2(
                modifier = Modifier.animateItem(),
                onItemClick = onItemClick,
                courseSearchResult = listData[index],
                isLatest = listData[index].routeId == latestRouteId,
                onRegisterAlarmBtnClick = { routeId ->
                    onRegisterAlarmBtnClick(routeId)
                }
            )
        }
    }
}

@Preview
@Composable
fun LastTransportInfoListPreview2(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    CompositionLocalProvider(
        LocalDialogController provides DialogController()
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
            mockData.copy(routeId = "124"),
            mockData.copy(routeId = "125"),
            mockData.copy(routeId = "126"),
            mockData.copy(routeId = "127")
        )
        LastTransportInfoListV2(
            listData = mockDataList
        )
    }
}
