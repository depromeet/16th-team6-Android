package com.depromeet.team6.presentation.ui.itinerary.component

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.view.toDp
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun SummaryBarChart(
    modifier: Modifier = Modifier,
    legs: List<LegInfo>,
) {
    val total = legs.sumOf { it.sectionTime }.toFloat()
    var rowWidth by remember { mutableStateOf(0f) } // Row의 너비를 저장할 변수
     Row(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .roundedBackgroundWithPadding(
                backgroundColor = defaultTeam6Colors.greyButtonOutline,
                cornerRadius = 20.dp,
                padding = PaddingValues(horizontal = 10.dp),
            )
            .onGloballyPositioned { layoutCoordinates ->
                rowWidth = layoutCoordinates.size.width.toFloat() // Row의 너비를 저장
            }
    ) {
        legs.forEach { leg ->
            val fraction = leg.sectionTime / total // 비율 계산
            val barWidth = rowWidth.toDp() * fraction // Row의 너비에 비례하여 바의 너비 계산

            if (leg.transportType == TransportType.WALK) {
                Box(
                    modifier = Modifier
                        .width(barWidth) // 바의 너비 설정
                        .fillMaxHeight()
                        .background(defaultTeam6Colors.greyButtonOutline)
                        .align(Alignment.CenterVertically),
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${leg.sectionTime}",
                            color = defaultTeam6Colors.greySecondaryLabel,
                            style = defaultTeam6Typography.bodySemiBold10,
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
                        .background(leg.routeColor, shape = RoundedCornerShape(20.dp))
                        .align(Alignment.CenterVertically),
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${leg.sectionTime}",
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


@Preview(showBackground = true, backgroundColor = Color.BLACK.toLong())
@Composable
fun BarChartPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {

    SummaryBarChart(
        modifier = Modifier
            .fillMaxWidth(),
        legs = legs
    )
}
