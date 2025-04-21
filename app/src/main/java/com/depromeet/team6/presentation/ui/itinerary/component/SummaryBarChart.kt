package com.depromeet.team6.presentation.ui.itinerary.component

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.presentation.util.view.toDp
import com.depromeet.team6.ui.theme.Team6Theme
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun SummaryBarChart(
    modifier: Modifier = Modifier,
    legs: List<LegInfo>
) {
    val total = legs.sumOf { it.sectionTime }.toFloat()
    var rowWidthPx by remember { mutableStateOf(0f) } // Row의 너비를 저장할 변수
    val minBarWidth = 25.dp

    val density = LocalDensity.current
    var finalWidths by remember { mutableStateOf(emptyList<Dp>()) }

    LaunchedEffect(rowWidthPx) {
        val rowWidthDp = with(density) { rowWidthPx.toDp() }

        // 모든 아이템의 최종 너비를 계산
        finalWidths = calculateFinalWidths(rowWidthDp, legs, minBarWidth)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .roundedBackgroundWithPadding(
                backgroundColor = defaultTeam6Colors.greyButtonOutline,
                padding = PaddingValues(horizontal = 4.dp)
            )
            .onGloballyPositioned { layoutCoordinates ->
                rowWidthPx = layoutCoordinates.size.width.toFloat() // Row의 너비를 저장
            }
    ) {
        legs.forEachIndexed { index, leg ->
            val fraction = leg.sectionTime / total // 비율 계산
            val barWidth = rowWidthPx.toDp() * fraction // Row의 너비에 비례하여 바의 너비 계산
//            val barWidth = finalWidths.getOrElse(index) { 0.dp }

            if (leg.transportType == TransportType.WALK) {
                Box(
                    modifier = Modifier
                        .width(barWidth) // 바의 너비 설정
                        .fillMaxHeight()
                        .background(defaultTeam6Colors.greyButtonOutline)
                        .align(Alignment.CenterVertically)
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${leg.sectionTime / 60}",
                            color = defaultTeam6Colors.greySecondaryLabel,
                            style = defaultTeam6Typography.bodySemiBold10
                        )
                        Text(
                            text = "분",
                            color = defaultTeam6Colors.greySecondaryLabel,
                            style = defaultTeam6Typography.bodyMedium10,
                            fontSize = 9.sp
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(barWidth) // 바의 너비 설정
                        .fillMaxHeight()
                        .background(
                            color = TransportTypeUiMapper.getColor(
                                type = leg.transportType,
                                subtypeIndex = leg.subTypeIdx
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .align(Alignment.CenterVertically)
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${leg.sectionTime / 60}",
                            color = defaultTeam6Colors.white,
                            style = defaultTeam6Typography.bodySemiBold10
                        )
                        Text(
                            text = "분",
                            color = defaultTeam6Colors.white,
                            style = defaultTeam6Typography.bodyMedium10,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

// TODO : 아이템 최소 너비 구하는 로직 (계산이 잘 안맞아서 수정해야 합니다)
/**
 * 전체 Row 너비(totalWidth)와 legs의 정보를 바탕으로 각 아이템의 최종 너비 리스트를 계산합니다.
 *
 * 알고리즘:
 * 1. 우선 모든 아이템에 대해 전체 너비에 대한 비율로 계산한 "이론상의" 너비를 구합니다.
 * 2. 이론상의 너비가 최소 너비보다 작은 아이템은 고정(최소 너비 할당)하고,
 *    남은 너비와 남은 시간에 대해 다시 비율 분배를 수행합니다.
 * 3. 반복문을 통해 더 이상 최소 너비 적용 대상이 없을 때까지 재계산합니다.
 */
private fun calculateFinalWidths(
    totalWidth: Dp,
    legs: List<LegInfo>,
    minBarWidth: Dp
): List<Dp> {
    // 임시 데이터 구조: (인덱스, 해당 leg의 sectionTime)
    val remainingLegs = legs.mapIndexed { index, leg ->
        index to leg.sectionTime
    }.toMutableList()
    val finalWidths = MutableList(legs.size) { 0.dp }
    var remainingWidth = totalWidth.value // dp 단위의 Float 값으로 사용

    // 남은 leg들에 대해 이론상 너비를 계산하고, 최소 너비보다 작은 경우 고정 처리 후 다시 분배
    var shouldRecalculate = true
    while (shouldRecalculate && remainingLegs.isNotEmpty()) {
        shouldRecalculate = false
        val sumTime = remainingLegs.sumOf { it.second }
        // 남은 너비를 기준으로 이론상 너비 계산
        val theoreticalWidths = remainingLegs.map { (_, time) ->
            remainingWidth * (time / sumTime)
        }
        // 최소 너비보다 작은 아이템이 있는지 확인
        for ((i, pair) in remainingLegs.withIndex()) {
            val (index, _) = pair
            val theoryWidth = theoreticalWidths[i]
            if (theoryWidth < minBarWidth.value) {
                finalWidths[index] = minBarWidth
                remainingWidth -= minBarWidth.value
                // 현재 아이템은 고정했으므로 리스트에서 제거
                remainingLegs.removeAt(i)
                shouldRecalculate = true
                break // 리스트가 변경되었으므로 for문 중단 후 while문 재실행
            }
        }
    }

    // 남은 아이템에 대해 남은 너비를 비율로 분배
    val remainingTimeSum = remainingLegs.sumOf { it.second }
    remainingLegs.forEach { (index, time) ->
        val allocated = if (remainingTimeSum > 0) remainingWidth * (time / remainingTimeSum) else 0f
        finalWidths[index] = allocated.dp
    }

    return finalWidths
}

@Preview(name = "normal", showBackground = true, backgroundColor = Color.BLACK.toLong())
@Composable
fun BarChartPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    val legs_ = listOf(
        LegInfo(
            transportType = TransportType.WALK,
            sectionTime = 57,
            startPoint = Address(
                name = "화서역",
                lat = 0.1,
                lon = 0.1,
                address = ""
            ),
            endPoint = Address(
                name = "지하철2호선방배역",
                lat = 0.0,
                lon = 0.0,
                address = ""
            ),
            distance = 10,
            passShape = "127.02481,37.504562 127.024666,37.50452"
        ),
        LegInfo(
            transportType = TransportType.BUS,
            subTypeIdx = 2,
            sectionTime = 1,
            startPoint = Address(
                name = "수원 KT위즈파크",
                lat = 0.1,
                lon = 0.1,
                address = ""
            ),
            endPoint = Address(
                name = "사당역 2호선",
                lat = 0.0,
                lon = 0.0,
                address = ""
            ),
            distance = 57,
            passShape = "127.025675,37.501708 127.026528,37.499994 127.026856,37.499408 127.027367,37.498353 127.027525,37.498025 127.027589,37.497892 127.027717,37.497525 127.028253,37.496306 127.028436,37.495922 127.029794,37.493072 127.029858,37.492939 127.030497,37.491664 127.031522,37.489619 127.031586,37.489483 127.032458,37.487656 127.033683,37.485086 127.033819,37.484811 127.033928,37.484633 127.034036,37.484503 127.034319,37.484181 127.034628,37.483844 127.034800,37.483678 127.037200,37.481392 127.037383,37.481169 127.037661,37.480753 127.038047,37.480053 127.038289,37.479453 127.038389,37.479136"
        ),
        LegInfo(
            transportType = TransportType.SUBWAY,
            subTypeIdx = 2,
            sectionTime = 129,
            startPoint = Address(
                name = "사당역 2호선",
                lat = 0.1,
                lon = 0.1,
                address = ""
            ),
            endPoint = Address(
                name = "강남역 신분당선",
                lat = 0.0,
                lon = 0.0,
                address = ""
            ),
            distance = 37,
            passShape = "127.0381,37.479004 127.03806,37.4791 127.03799,37.479313"
        ),
        LegInfo(
            transportType = TransportType.WALK,
            sectionTime = 13,
            startPoint = Address(
                name = "강남역 신분당선",
                lat = 0.1,
                lon = 0.1,
                address = ""
            ),
            endPoint = Address(
                name = "할리스 커피 강남1호점",
                lat = 0.0,
                lon = 0.0,
                address = ""
            ),
            distance = 10,
            passShape = "127.03799,37.479313 127.037636,37.479263 127.037445,37.47924"
        )
    )

    Team6Theme() {
        Column() {
            SummaryBarChart(
                modifier = Modifier
                    .fillMaxWidth(),
                legs = legs
            )

            SummaryBarChart(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                legs = legs_
            )
        }
    }
}
