package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import java.time.LocalDateTime

@Composable
fun ItineraryInfoDetailLegs(
    legs : List<LegInfo>,
    modifier: Modifier = Modifier
) {
    DetailLegsWalk(
        boardingDateTime = "2023-06-06T00:00:00",
        timeMinute = 25,
        distanceMeter = 1000
    )
}

@Composable
private fun DetailLegsBus(
    subtypeIdx : Int,
    boardingDateTime : String,
    timeMinute : Int,
    distanceMeter : Int,
    modifier : Modifier = Modifier,
) {
    Row(
        modifier = Modifier,
    ){
        val busColor = TransportTypeUiMapper.getColor(TransportType.BUS, subtypeIdx)
        val busIconId = TransportTypeUiMapper.getIconResId(TransportType.BUS, subtypeIdx)
        // 좌측 버스 수직라인
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = ImageVector.vectorResource(busIconId),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Box (
                modifier = Modifier
                    .height(105.dp)
                    .background(busColor),
//                backgroundColor = busColor,
            ){

            }
        }

        // 우측 버스정보 텍스트
        Column {

        }
    }
}

@Composable
private fun DetailLegsWalk(
    boardingDateTime : String,
    timeMinute : Int,
    distanceMeter : Int,
    modifier : Modifier = Modifier,
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        // 좌측 점선
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoardingTime(
                boardingDateTime = boardingDateTime
            )
            DottedLineWithCircles(
                height = 50.dp
            )
        }
        Spacer(
            modifier = Modifier.width(17.dp)
        )

        // 우측 도보 경로정보 텍스트
        Text(
            text = stringResource(R.string.itinerary_info_legs_walk_time, timeMinute),
            style = defaultTeam6Typography.bodyMedium13,
            color = defaultTeam6Colors.greySecondaryLabel
        )
        Spacer(
            modifier = Modifier.width(4.dp)
        )
        Text(
            text = stringResource(R.string.itinerary_info_legs_walk_distance, distanceMeter),
            style = defaultTeam6Typography.bodyMedium13,
            color = defaultTeam6Colors.systemGrey2
        )
    }
}

@Composable
private fun BoardingTime(
    boardingDateTime : String,
    modifier : Modifier = Modifier,
) {
    val boardingTime = LocalDateTime.parse(boardingDateTime)
    Box(
        modifier = Modifier
            .wrapContentSize()
            .border(
                width = 1.dp,
                color = Color(0xFF27272B),
                shape = RoundedCornerShape(size = 4.dp)
            )
            .roundedBackgroundWithPadding(
                cornerRadius = 4.dp,
                backgroundColor = defaultTeam6Colors.greyElevatedCard,
                padding = PaddingValues(vertical = 2.dp, horizontal = 4.dp)
            )
    ) {
        Text(
            text = stringResource(
                R.string.itinerary_info_legs_boarding_HHmm,
                boardingTime.hour,
                boardingTime.minute
            ),
            style = defaultTeam6Typography.bodyMedium11,
            color = defaultTeam6Colors.greySecondaryLabel
        )
    }
}

@Composable
fun DottedLineWithCircles(
    height: Dp,
    modifier: Modifier = Modifier
) {
    val dotColor: Color = defaultTeam6Colors.systemGrey3
    val unitSize = 5.dp
    val dotSize = unitSize
    val gapSize = unitSize

    val density = LocalDensity.current
    val adjustedHeightDp = with(density) {
        val rawHeightPx = height.toPx()
        val unitPx = unitSize.toPx()
        val availableUnits = (rawHeightPx / unitPx).toInt()
        // 실제 높이를 dp로 재계산
        (availableUnits * unitPx).toDp()
    }

    val numberOfDots = with(density) {
        val unitPx = unitSize.toPx()
        (adjustedHeightDp.toPx() / unitPx).toInt()
    }

    Column(
        modifier = modifier.height(height),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(numberOfDots) { index ->
            Canvas(modifier = Modifier.size(dotSize)) {
                drawCircle(color = dotColor)
            }
            if (index != numberOfDots - 1) {
                Spacer(modifier = Modifier.height(gapSize))
            }
        }
    }
}


@Preview
@Composable
fun ItineraryInfoDetailLegsPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItineraryInfoDetailLegs(
        legs = legs
    )
}
