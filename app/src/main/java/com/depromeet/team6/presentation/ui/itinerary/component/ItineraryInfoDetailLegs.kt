package com.depromeet.team6.presentation.ui.itinerary.component

import android.util.SparseArray
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.BusStatus
import com.depromeet.team6.domain.model.RealTimeBusArrival
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.domain.model.toInfo
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.ui.common.text.AtChaRemainTimeText
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.Dimens
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import java.time.LocalDateTime

@Composable
fun ItineraryInfoDetailLegs(
    legs: List<LegInfo>,
    busArrivalStatus : SparseArray<RealTimeBusArrival>,
    modifier: Modifier = Modifier,
    onClickBusInfo: (BusArrivalParameter) -> Unit = {}
) {
    Column {
        for ((idx, leg) in legs.withIndex()) {
            when (leg.transportType) {
                TransportType.WALK -> {
                    DetailLegsWalk(
                        boardingDateTime = leg.departureDateTime!!,
                        timeMinute = leg.sectionTime / 60,
                        distanceMeter = leg.distance
                    )
                }
                TransportType.BUS -> {
                    DetailLegsBus(
                        busName = leg.routeName!!,
                        subtypeIdx = leg.subTypeIdx,
                        boardingStation = leg.startPoint.name,
                        disembarkingStation = leg.endPoint.name,
                        boardingDateTime = leg.departureDateTime!!,
                        timeMinute = leg.sectionTime / 60,
                        distanceMeter = leg.distance,
                        busArrivalStatus = busArrivalStatus.get(idx),
                        onClickBusInfo = { routeName, stationName, subtypeIdx ->
                            onClickBusInfo(
                                BusArrivalParameter(
                                    routeName = routeName,
                                    stationName = stationName,
                                    lat = leg.startPoint.lat,
                                    lon = leg.startPoint.lon,
                                    subtypeIdx = subtypeIdx
                                )
                            )
                        }
                    )
                }
                TransportType.SUBWAY -> {
                    DetailLegsSubway(
                        subwayName = leg.routeName!!,
                        subtypeIdx = leg.subTypeIdx,
                        boardingStation = leg.startPoint.name,
                        disembarkingStation = leg.endPoint.name,
                        boardingDateTime = leg.departureDateTime!!,
                        timeMinute = leg.sectionTime / 60,
                        distanceMeter = leg.distance
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailLegsBus(
    busName: String,
    subtypeIdx: Int,
    boardingStation: String,
    disembarkingStation: String,
    boardingDateTime: String,
    timeMinute: Int,
    distanceMeter: Int,
    busArrivalStatus : RealTimeBusArrival?,
    modifier: Modifier = Modifier,
    onClickBusInfo: (String, String, Int) -> Unit = { routeName: String, stationName: String, subtypeIdx: Int -> }
) {
    var rowHeight by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .wrapContentHeight()
    ) {
        val busColor = TransportTypeUiMapper.getColor(TransportType.BUS, subtypeIdx)
        val busIconId = TransportTypeUiMapper.getIconResId(TransportType.BUS, subtypeIdx)
        // 좌측 버스 수직라인
        Box(
            modifier = Modifier
                .width(Dimens.LegDetailVerticalLineWidth)
                .height(rowHeight.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // 세로 직선
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .padding(top = 15.dp, bottom = 20.dp)
                    .background(busColor)
                    .align(Alignment.Center)
            )
            Column(
                modifier = Modifier
                    .wrapContentWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 버스 아이콘
                Image(
                    imageVector = ImageVector.vectorResource(busIconId),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
                // 탑승 시각
                BoardingTime(
                    boardingDateTime = boardingDateTime,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.weight(weight = 1f)) // 남은 공간 차지해서 아래로 밀어줌

                // 하차지점
                Box(
                    modifier = Modifier
                        .size(18.dp) // 지름 크기
                        .clip(CircleShape)
                        .background(busColor)
                )
                Spacer(modifier = Modifier.height(2.dp))
                BoardingTime(
                    boardingDateTime = boardingDateTime,
                    modifier = Modifier
                )
            }
        }

        Spacer(
            modifier = Modifier
                .width(Dimens.LegDetailLineTextMargin)
        )

        val density = LocalDensity.current
        // 우측 버스정보 텍스트
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .onGloballyPositioned { layoutCoordinates ->
                    with(density) {
                        rowHeight = layoutCoordinates.size.height.toDp().value.toInt()
                    }
                }
        ) {
            Spacer(
                modifier = Modifier.height(9.dp)
            )
            // 승차
            Row {
                Text(
                    text = boardingStation,
                    style = defaultTeam6Typography.bodySemiBold14,
                    color = defaultTeam6Colors.white
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.itinerary_info_legs_boarding),
                    style = defaultTeam6Typography.bodySemiBold14,
                    color = defaultTeam6Colors.greySecondaryLabel
                )
            }
            Spacer(Modifier.height(19.dp))
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ){
                BusNumberButton(
                    modifier = Modifier
                        .noRippleClickable {
                            onClickBusInfo(
                                busName,
                                boardingStation,
                                subtypeIdx
                            )
                        },
                    number = busName,
                    busColor = busColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (busArrivalStatus == null) {
                    AtChaRemainTimeText(remainSecond = 0, busStatus = BusStatus.WAITING)
                }
                else {
                    AtChaRemainTimeText(remainSecond = busArrivalStatus.remainingTime, busStatus = busArrivalStatus.busStatus)
                    Text(
                        text = "(${busArrivalStatus.busCongestion.toInfo().label})",
                        style = defaultTeam6Typography.bodyRegular13,
                        color = defaultTeam6Colors.systemRed
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.itinerary_summary_duration_minute, timeMinute),
                style = defaultTeam6Typography.bodyMedium13,
                color = defaultTeam6Colors.white
            )
            Spacer(Modifier.height(36.dp))
            // 하차
            Row(
                modifier = Modifier
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = disembarkingStation,
                    style = defaultTeam6Typography.bodySemiBold14,
                    color = defaultTeam6Colors.white
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.itinerary_info_legs_disembarking),
                    style = defaultTeam6Typography.bodySemiBold14,
                    color = defaultTeam6Colors.greySecondaryLabel
                )
            }
        }
    }
}

@Composable
private fun DetailLegsSubway(
    subwayName: String,
    subtypeIdx: Int,
    boardingStation: String,
    disembarkingStation: String,
    boardingDateTime: String,
    timeMinute: Int,
    distanceMeter: Int,
    modifier: Modifier = Modifier
) {
    var rowHeight by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .wrapContentHeight()
    ) {
        val subwayColor = TransportTypeUiMapper.getColor(TransportType.SUBWAY, subtypeIdx)
        val subwayIconId = TransportTypeUiMapper.getIconResId(TransportType.SUBWAY, subtypeIdx)
        // 좌측 버스 수직라인
        Box(
            modifier = Modifier
                .width(Dimens.LegDetailVerticalLineWidth)
                .height(rowHeight.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // 세로 직선
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .padding(top = 15.dp, bottom = 20.dp)
                    .background(subwayColor)
                    .align(Alignment.Center)
            )
            Column(
                modifier = Modifier
                    .wrapContentWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 지하철 아이콘
                Image(
                    imageVector = ImageVector.vectorResource(subwayIconId),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
                // 탑승 시각
                BoardingTime(
                    boardingDateTime = boardingDateTime,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.weight(weight = 1f)) // 남은 공간 차지해서 아래로 밀어줌

                // 하차지점
                Box(
                    modifier = Modifier
                        .size(18.dp) // 지름 크기
                        .clip(CircleShape)
                        .background(subwayColor)
                )
                Spacer(modifier = Modifier.height(2.dp))
                BoardingTime(
                    boardingDateTime = boardingDateTime,
                    modifier = Modifier
                )
            }
        }

        Spacer(
            modifier = Modifier
                .width(Dimens.LegDetailLineTextMargin)
        )

        val density = LocalDensity.current
        // 우측 버스정보 텍스트
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .onGloballyPositioned { layoutCoordinates ->
                    with(density) {
                        rowHeight = layoutCoordinates.size.height.toDp().value.toInt()
                    }
                }
        ) {
            Spacer(
                modifier = Modifier.height(9.dp)
            )
            // 승차
            Row {
                Text(
                    text = boardingStation,
                    style = defaultTeam6Typography.bodySemiBold14,
                    color = defaultTeam6Colors.white
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.itinerary_info_legs_boarding),
                    style = defaultTeam6Typography.bodySemiBold14,
                    color = defaultTeam6Colors.greySecondaryLabel
                )
            }
            Spacer(Modifier.height(19.dp))
            Text(
                text = subwayName,
                style = defaultTeam6Typography.bodyMedium13,
                color = defaultTeam6Colors.greySecondaryLabel
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.itinerary_summary_duration_minute, timeMinute),
                style = defaultTeam6Typography.bodyMedium13,
                color = defaultTeam6Colors.white
            )
            Spacer(Modifier.height(36.dp))
            // 하차
            Row(
                modifier = Modifier
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = disembarkingStation,
                    style = defaultTeam6Typography.bodySemiBold14,
                    color = defaultTeam6Colors.white
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.itinerary_info_legs_disembarking),
                    style = defaultTeam6Typography.bodySemiBold14,
                    color = defaultTeam6Colors.greySecondaryLabel
                )
            }
        }
    }
}

@Composable
private fun DetailLegsWalk(
    boardingDateTime: String,
    timeMinute: Int,
    distanceMeter: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측 점선
        Column(
            modifier = Modifier
                .width(Dimens.LegDetailVerticalLineWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier.height(5.dp)
            )
            DottedLineWithCircles(
                height = 50.dp
            )
        }
        Spacer(
            modifier = Modifier.width(Dimens.LegDetailLineTextMargin)
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
fun BoardingTime(
    boardingDateTime: String,
    modifier: Modifier = Modifier
) {
    val boardingTime = LocalDateTime.parse(boardingDateTime)
    Box(
        modifier = Modifier
            .wrapContentSize()
            .border(
                width = 1.dp,
                color = defaultTeam6Colors.greyElevatedCard,
                shape = RoundedCornerShape(size = 4.dp)
            )
            .roundedBackgroundWithPadding(
                cornerRadius = 4.dp,
                backgroundColor = Color(0xFF27272B),
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
private fun DottedLineWithCircles(
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

@Composable
private fun BusNumberButton(
    number: String,
    busColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp)) // 둥근 모서리
            .background(busColor)
            .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = number,
                color = defaultTeam6Colors.white,
                style = defaultTeam6Typography.bodySemiBold12
            )
            // 오른쪽 화살표
            Image(
                modifier = Modifier
                    .size(10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right_big),
                colorFilter = ColorFilter.tint(Color(0x80FFFFFF)),
                contentDescription = "Go"
            )
        }
    }
}

@Preview
@Composable
fun ItineraryInfoDetailLegsPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItineraryInfoDetailLegs(
        legs = legs,
        busArrivalStatus = SparseArray<RealTimeBusArrival>()
    )

//    Column(){
//        DetailLegsWalk(
//            boardingDateTime = "2023-06-06T00:00:00",
//            timeMinute = 25,
//            distanceMeter = 1000
//        )
//        DetailLegsBus(
//            busName = "152",
//            subtypeIdx = 2,
//            boardingStation = "중앙빌딩",
//            disembarkingStation = "우리집",
//            boardingDateTime = "2023-06-06T00:00:00",
//            timeMinute = 25,
//            distanceMeter = 1000
//        )
//        DetailLegsSubway(
//            subwayName = "성수(내선)행",
//            subtypeIdx = 1,
//            boardingStation = "중앙빌딩",
//            disembarkingStation = "우리집",
//            boardingDateTime = "2023-06-06T00:00:00",
//            timeMinute = 25,
//            distanceMeter = 1000
//        )
//    }
}
