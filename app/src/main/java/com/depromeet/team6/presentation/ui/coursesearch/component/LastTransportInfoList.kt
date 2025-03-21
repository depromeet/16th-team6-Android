package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun LastTransportInfoList(
    listData: List<LastTransportInfo>,
    modifier: Modifier = Modifier,
    onRegisterAlarmBtnClick: () -> Unit = {},
    onItemClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(defaultTeam6Colors.black)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(listData.size) { index ->
            LastTransportInfoItem(
                modifier = Modifier
                    .noRippleClickable {
                        onItemClick()
                    },
                lastTransportInfo = listData[index],
                onRegisterAlarmBtnClick = {
                    onRegisterAlarmBtnClick()
                }
            )
        }
    }
}

@Preview
@Composable
fun LastTransportInfoListPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    val mockData = LastTransportInfo(
        remainingMinutes = 23,
        departureTime = "2025-03-11 23:12:00",
        boardingTime = "2025-03-11 23:21:00",
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
