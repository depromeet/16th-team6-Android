package com.depromeet.team6.presentation.ui.itinerary.component

import android.util.SparseArray
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.BusCongestion
import com.depromeet.team6.domain.model.BusStatus
import com.depromeet.team6.domain.model.RealTimeBusArrival
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.Station
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.domain.model.toInfo
import com.depromeet.team6.domain.usecase.CalculateDistanceUseCase
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.ui.common.text.AtChaRemainTimeText
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.Dimens
import com.depromeet.team6.presentation.util.Dimens.WalkIconWithRippleSize
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun ItineraryInfoDetailLegs(
    currentLocation: LatLng,
    userDeparted: Boolean,
    legs: List<LegInfo>,
    busArrivalStatus: SparseArray<RealTimeBusArrival>,
    modifier: Modifier = Modifier,
    onClickBusInfo: (BusArrivalParameter) -> Unit = {}
) {
    val timelineIconSize = Dimens.LegTimelineIconSize
    val timelineTimeIconGap = Dimens.LegTimelineTimeIconGap
    val timelineTimeSlotWidth = Dimens.LegTimelineTimeSlotWidth
    val timelineColumnMinWidth = timelineTimeSlotWidth + timelineTimeIconGap + timelineIconSize
    val timelineAxisOffset = timelineTimeSlotWidth + timelineTimeIconGap + (timelineIconSize / 2)

    // Column의 실제 높이(px)
    var columnHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight() // Column의 실제 높이에 맞추고 싶다면 wrapContentHeight 유지
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coords ->
                    columnHeightPx = coords.size.height
                }
        ) {
            for ((idx, leg) in legs.withIndex()) {
                when (leg.transportType) {
                    TransportType.WALK -> {
                        val verticalHeight = walkTimelineHeight(
                            distanceMeter = leg.distance,
                            isFirstWalk = idx == 0,
                            isLastWalk = idx == legs.size - 1
                        )
                        val walkLineOffsetY = if (idx == 0) (-3).dp else 0.dp
                        DetailLegsWalk(
                            boardingDateTime = leg.departureDateTime!!,
                            timeMinute = leg.sectionTime / 60,
                            distanceMeter = leg.distance,
                            verticalHeight = verticalHeight,
                            lineOffsetY = walkLineOffsetY,
                            timelineAxisOffset = timelineAxisOffset,
                            timelineColumnWidth = timelineColumnMinWidth
                        )
                    }
                    TransportType.BUS -> {
                        DetailLegsBus(
                            currentLocation = currentLocation,
                            busName = leg.routeName!!,
                            subtypeIdx = leg.subTypeIdx,
                            boardingStation = leg.startPoint.name,
                            boardingStationLat = leg.startPoint.lat,
                            boardingStationLon = leg.startPoint.lon,
                            disembarkingStation = leg.endPoint.name,
                            boardingDateTime = leg.departureDateTime!!,
                            timeMinute = leg.sectionTime / 60,
                            distanceMeter = leg.distance,
                            busArrivalStatus = busArrivalStatus.get(idx),
                            passStopList = leg.passStopList,
                            timelineIconSize = timelineIconSize,
                            timelineTimeSlotWidth = timelineTimeSlotWidth,
                            timelineTimeIconGap = timelineTimeIconGap,
                            timelineColumnMinWidth = timelineColumnMinWidth,
                            onClickBusInfo = { routeName, stationName, subtypeIdx ->
                                onClickBusInfo(
                                    BusArrivalParameter(
                                        routeName = routeName,
                                        stationName = stationName,
                                        lat = leg.startPoint.lat,
                                        lon = leg.startPoint.lon,
                                        subtypeIdx = subtypeIdx,
                                        passingStations = leg.passStopList
                                    )
                                )
                            }
                        )
                    }
                    TransportType.SUBWAY -> {
                        DetailLegsSubway(
                            subwayName = leg.routeName!!,
                            isExpressSubway = leg.isExpressSubway,
                            isLastSubway = leg.isLastSubway,
                            subtypeIdx = leg.subTypeIdx,
                            boardingStation = leg.startPoint.name,
                            disembarkingStation = leg.endPoint.name,
                            boardingDateTime = leg.departureDateTime!!,
                            timeMinute = leg.sectionTime / 60,
                            passStopList = leg.passStopList,
                            distanceMeter = leg.distance,
                            timelineIconSize = timelineIconSize,
                            timelineTimeSlotWidth = timelineTimeSlotWidth,
                            timelineTimeIconGap = timelineTimeIconGap,
                            timelineColumnMinWidth = timelineColumnMinWidth
                        )
                    }
                }
            }
        }

        // 출발한 경우 부왕부왕 아이콘 표시
        if (userDeparted) {
            val iconSizePx = with(density) { WalkIconWithRippleSize.toPx() }
            // 현재위치와 경로의 직선거리를 통해 얼만큼 왔는지 비율 계산 (부왕부왕 마커 표시하기 위함)
            val totalDistance by remember {
                mutableFloatStateOf(
                    CalculateDistanceUseCase().invoke(
                        lat1 = legs[0].startPoint.lat,
                        lon1 = legs[0].startPoint.lon,
                        lat2 = legs.last().endPoint.lat,
                        lon2 = legs.last().endPoint.lon
                    )
                )
            }
            val currentDistance by remember {
                derivedStateOf {
                    CalculateDistanceUseCase().invoke(
                        lat1 = currentLocation.latitude,
                        lon1 = currentLocation.longitude,
                        lat2 = legs[0].endPoint.lat,
                        lon2 = legs[0].endPoint.lon
                    )
                }
            }
            val currentPositionRatio by remember(currentDistance, totalDistance) {
                derivedStateOf { currentDistance / totalDistance }
            }
            // 부왕부왕 마커 부드럽게 이동하기 위한 애니메이션 좌표값
            val markerX = remember {
                (with(density) { 20.dp.toPx() } - (iconSizePx / 2))
            }
            val animatedMarkerY = remember {
                Animatable(
                    0 - (iconSizePx / 2)
                )
            }
            // 부왕부왕 마커가 실제로 표시되어야 하는 물리적 픽셀 좌표
            val markerYPx by remember(columnHeightPx, iconSizePx) {
                derivedStateOf {
                    ((columnHeightPx * currentPositionRatio) - (iconSizePx / 2)).roundToInt()
                }
            }
            LaunchedEffect(markerYPx) {
                animatedMarkerY.animateTo(
                    targetValue = markerYPx.toFloat(),
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            WalkIconWithRipple(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = markerX.roundToInt(),
                            y = animatedMarkerY.value.roundToInt()
                        )
                    }
            )
        }
    }
}

@Composable
private fun DetailLegsBus(
    currentLocation: LatLng,
    busName: String,
    subtypeIdx: Int,
    boardingStation: String,
    boardingStationLat: Double,
    boardingStationLon: Double,
    disembarkingStation: String,
    boardingDateTime: String,
    timeMinute: Int,
    distanceMeter: Int,
    busArrivalStatus: RealTimeBusArrival?,
    passStopList: List<Station>,
    timelineIconSize: Dp,
    timelineTimeSlotWidth: Dp,
    timelineTimeIconGap: Dp,
    timelineColumnMinWidth: Dp,
    modifier: Modifier = Modifier,
    onClickBusInfo: (String, String, Int) -> Unit = { routeName: String, stationName: String, subtypeIdx: Int -> }
) {
    var rowHeight by remember { mutableStateOf(0) }
    var isPassStopShow by remember { mutableStateOf(false) }
    var arrivedAtStationAtMillis by remember(boardingStation, boardingStationLat, boardingStationLon) { mutableLongStateOf(0L) }
    var isBoardingCompleted by remember(boardingStation, boardingStationLat, boardingStationLon) { mutableStateOf(false) }
    val disembarkingDateTime: String = LocalDateTime
        .parse(boardingDateTime)
        .plusMinutes(timeMinute.toLong())
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val timelineAxisOffset = timelineTimeSlotWidth + timelineTimeIconGap + (timelineIconSize / 2)
    val disembarkingMarkerSize = timelineIconSize * (14f / 26f)
    val disembarkingMarkerOffset = (timelineIconSize - disembarkingMarkerSize) / 2
    val distanceToBoardingStation = remember(currentLocation, boardingStationLat, boardingStationLon) {
        CalculateDistanceUseCase().invoke(
            lat1 = currentLocation.latitude,
            lon1 = currentLocation.longitude,
            lat2 = boardingStationLat,
            lon2 = boardingStationLon
        )
    }
    val displayBusStatus = if (isBoardingCompleted) {
        BusStatus.BOARDING_COMPLETED
    } else {
        busArrivalStatus?.busStatus ?: BusStatus.WAITING
    }

    LaunchedEffect(distanceToBoardingStation) {
        if (isBoardingCompleted) return@LaunchedEffect

        val now = System.currentTimeMillis()
        if (arrivedAtStationAtMillis == 0L && distanceToBoardingStation <= BOARDING_STOP_ARRIVAL_DISTANCE_METER) {
            arrivedAtStationAtMillis = now
            return@LaunchedEffect
        }

        if (arrivedAtStationAtMillis != 0L) {
            val stayedForOneMinute = now - arrivedAtStationAtMillis >= BOARDING_COMPLETED_STAY_MILLIS
            val movedAwayFromStation = distanceToBoardingStation >= BOARDING_COMPLETED_LEAVE_DISTANCE_METER

            if (stayedForOneMinute || movedAwayFromStation) {
                isBoardingCompleted = true
            }
        }
    }

    Row(
        modifier = modifier
            .zIndex(1f)
            .wrapContentHeight()
    ) {
        val busColor = TransportTypeUiMapper.getColor(TransportType.BUS, subtypeIdx)
        val busIconId = TransportTypeUiMapper.getIconResId(TransportType.BUS, subtypeIdx)
        // 좌측 버스 수직라인
        Box(
            modifier = Modifier
                .widthIn(min = timelineColumnMinWidth)
                .wrapContentWidth()
                .height(rowHeight.dp),
            contentAlignment = Alignment.TopStart
        ) {
            // 세로 직선
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(3.dp)
                    .align(Alignment.TopStart)
                    .offset(x = timelineAxisOffset - 1.5.dp)
                    .padding(top = 12.dp, bottom = 8.dp)
                    .background(busColor)
            )
            Column(
                modifier = Modifier
                    .widthIn(min = timelineColumnMinWidth)
                    .wrapContentWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(timelineTimeSlotWidth)) {
                        BoardingTime(
                            boardingDateTime = boardingDateTime,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                    Spacer(modifier = Modifier.width(timelineTimeIconGap))
                    Image(
                        imageVector = ImageVector.vectorResource(busIconId),
                        contentDescription = null,
                        modifier = Modifier.size(timelineIconSize)
                    )
                }

                Spacer(modifier = Modifier.weight(weight = 1f)) // 남은 공간 차지해서 아래로 밀어줌

                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(timelineTimeSlotWidth)) {
                        BoardingTime(
                            boardingDateTime = disembarkingDateTime,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                    Spacer(modifier = Modifier.width(timelineTimeIconGap))
                    Box(
                        modifier = Modifier
                            .size(disembarkingMarkerSize)
                            .offset(x = disembarkingMarkerOffset)
                            .clip(CircleShape)
                            .background(busColor)
                    )
                }
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
                    style = defaultTeam6Typography.body5_B5SB14,
                    color = defaultTeam6Colors.white
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.itinerary_info_legs_boarding),
                    style = defaultTeam6Typography.body5_B5SB14,
                    color = defaultTeam6Colors.gray200
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .noRippleClickable {
                        onClickBusInfo(
                            busName,
                            boardingStation,
                            subtypeIdx
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                BusNumberButton(
                    modifier = Modifier,
                    busName = busName,
                    busColor = busColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                AtChaRemainTimeText(
                    remainSecond = busArrivalStatus?.remainingTime ?: 0,
                    busStatus = displayBusStatus
                )
                if (
                    !isBoardingCompleted &&
                    busArrivalStatus != null &&
                    busArrivalStatus.busCongestion != BusCongestion.UNKNOWN
                ) {
                    Text(
                        text = "(${busArrivalStatus.busCongestion.toInfo().label})",
                        style = defaultTeam6Typography.body6_B6R14,
                        color = defaultTeam6Colors.systemRed
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            // 정류장 이동정보 & 토글버튼
            Row(
                modifier = Modifier
                    .noRippleClickable {
                        isPassStopShow = !isPassStopShow
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.itinerary_info_legs_bus_stopovers, timeMinute, max(0, passStopList.size - 1)),
                    style = defaultTeam6Typography.body7_B7M13,
                    color = defaultTeam6Colors.white
                )
                Spacer(
                    modifier = Modifier.width(4.dp)
                )
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_down_big),
                    contentDescription = "arrow_down",
                    modifier = Modifier.size(12.dp)
                )
            }

            if (isPassStopShow) {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 1 until passStopList.size - 1) {
                        val stop = passStopList[i]
                        Text(
                            text = stop.stationName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = defaultTeam6Typography.body7_B7M13,
                            color = defaultTeam6Colors.gray200
                        )
                    }
                }
            }

            Spacer(Modifier.height(36.dp))
            // 하차
            DisembarkingStationText(
                stationName = disembarkingStation,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}

@Composable
private fun DetailLegsSubway(
    subwayName: String,
    isExpressSubway: Boolean,
    isLastSubway: Boolean,
    subtypeIdx: Int,
    boardingStation: String,
    disembarkingStation: String,
    boardingDateTime: String,
    timeMinute: Int,
    distanceMeter: Int,
    passStopList: List<Station>,
    timelineIconSize: Dp,
    timelineTimeSlotWidth: Dp,
    timelineTimeIconGap: Dp,
    timelineColumnMinWidth: Dp,
    modifier: Modifier = Modifier
) {
    var rowHeight by remember { mutableStateOf(0) }
    var isPassStopShow by remember { mutableStateOf(false) }
    val disembarkingDateTime: String = LocalDateTime
        .parse(boardingDateTime)
        .plusMinutes(timeMinute.toLong())
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val timelineAxisOffset = timelineTimeSlotWidth + timelineTimeIconGap + (timelineIconSize / 2)
    val disembarkingMarkerSize = timelineIconSize * (14f / 26f)
    val disembarkingMarkerOffset = (timelineIconSize - disembarkingMarkerSize) / 2

    Row(
        modifier = modifier
            .zIndex(1f)
            .wrapContentHeight()
    ) {
        val subwayColor = TransportTypeUiMapper.getColor(TransportType.SUBWAY, subtypeIdx)
        val subwayIconId = TransportTypeUiMapper.getIconResId(TransportType.SUBWAY, subtypeIdx)
        // 좌측 버스 수직라인
        Box(
            modifier = Modifier
                .widthIn(min = timelineColumnMinWidth)
                .wrapContentWidth()
                .height(rowHeight.dp),
            contentAlignment = Alignment.TopStart
        ) {
            // 세로 직선
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(3.dp)
                    .align(Alignment.TopStart)
                    .offset(x = timelineAxisOffset - 1.5.dp)
                    .padding(top = 12.dp, bottom = 8.dp)
                    .background(subwayColor)
            )
            Column(
                modifier = Modifier
                    .widthIn(min = timelineColumnMinWidth)
                    .wrapContentWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(timelineTimeSlotWidth)) {
                        BoardingTime(
                            boardingDateTime = boardingDateTime,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                    Spacer(modifier = Modifier.width(timelineTimeIconGap))
                    Image(
                        imageVector = ImageVector.vectorResource(subwayIconId),
                        contentDescription = null,
                        modifier = Modifier.size(timelineIconSize)
                    )
                }

                Spacer(modifier = Modifier.weight(weight = 1f)) // 남은 공간 차지해서 아래로 밀어줌

                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(timelineTimeSlotWidth)) {
                        BoardingTime(
                            boardingDateTime = disembarkingDateTime,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                    Spacer(modifier = Modifier.width(timelineTimeIconGap))
                    Box(
                        modifier = Modifier
                            .size(disembarkingMarkerSize)
                            .offset(x = disembarkingMarkerOffset)
                            .clip(CircleShape)
                            .background(subwayColor)
                    )
                }
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
                    style = defaultTeam6Typography.body5_B5SB14,
                    color = defaultTeam6Colors.white
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.itinerary_info_legs_boarding),
                    style = defaultTeam6Typography.body5_B5SB14,
                    color = defaultTeam6Colors.gray200
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = subwayName,
                    style = defaultTeam6Typography.body7_B7M13,
                    color = defaultTeam6Colors.gray200
                )
                if (isLastSubway) {
                    Spacer(modifier = Modifier.width(6.dp))
                    TransitFlagBadge(
                        text = "막",
                        containerColor = defaultTeam6Colors.systemRed
                    )
                }
                if (isExpressSubway) {
                    Spacer(modifier = Modifier.width(6.dp))
                    TransitFlagBadge(
                        text = "급",
                        containerColor = Color(0xFF1777FF)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .noRippleClickable {
                        isPassStopShow = !isPassStopShow
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.itinerary_info_legs_subway_stopovers, timeMinute, max(0, passStopList.size - 1)),
                    style = defaultTeam6Typography.body7_B7M13,
                    color = defaultTeam6Colors.white
                )
                Spacer(
                    modifier = Modifier.width(4.dp)
                )
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_down_big),
                    contentDescription = "arrow_down",
                    modifier = Modifier.size(12.dp)
                )
            }

            // 경유 역 정보
            if (isPassStopShow) {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 1 until passStopList.size - 1) {
                        val stop = passStopList[i]
                        Text(
                            text = stop.stationName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = defaultTeam6Typography.body7_B7M13,
                            color = defaultTeam6Colors.gray200
                        )
                    }
                }
            }

            Spacer(Modifier.height(36.dp))
            // 하차
            DisembarkingStationText(
                stationName = disembarkingStation,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}

@Composable
private fun DetailLegsWalk(
    boardingDateTime: String,
    timeMinute: Int,
    distanceMeter: Int,
    verticalHeight: Dp,
    lineOffsetY: Dp,
    timelineAxisOffset: Dp,
    timelineColumnWidth: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측 점선
        Column(
            modifier = Modifier
                .width(timelineColumnWidth),
            horizontalAlignment = Alignment.Start
        ) {
            DottedLineWithCircles(
                modifier = Modifier.offset(x = timelineAxisOffset - 2.5.dp, y = lineOffsetY),
                height = verticalHeight
            )
        }
        Spacer(
            modifier = Modifier.width(Dimens.LegDetailLineTextMargin)
        )

        // 우측 도보 경로정보 텍스트
        Text(
            modifier = Modifier
                .offset(y = (-8).dp),
            text = stringResource(R.string.itinerary_info_legs_walk_time, timeMinute),
            style = defaultTeam6Typography.body7_B7M13,
            color = defaultTeam6Colors.gray200
        )
        Spacer(
            modifier = Modifier.width(4.dp)
        )
        Text(
            modifier = Modifier
                .offset(y = (-8).dp),
            text = stringResource(R.string.itinerary_info_legs_walk_distance, distanceMeter),
            style = defaultTeam6Typography.body7_B7M13,
            color = defaultTeam6Colors.gray500
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
        modifier = modifier
            .wrapContentSize()
            .border(
                width = 1.dp,
                color = defaultTeam6Colors.gray910,
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
            style = defaultTeam6Typography.detail2_M11,
            color = defaultTeam6Colors.gray200
        )
    }
}

@Composable
private fun DottedLineWithCircles(
    height: Dp,
    modifier: Modifier = Modifier
) {
    val dotColor: Color = defaultTeam6Colors.gray600
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
private fun DisembarkingStationText(
    stationName: String,
    modifier: Modifier = Modifier
) {
    val stationStyle = defaultTeam6Typography.body5_B5SB14
    val suffixStyle = defaultTeam6Typography.body5_B5SB14
    val suffixText = stringResource(R.string.itinerary_info_legs_disembarking)
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val maxWidthPx = with(density) { maxWidth.toPx() }.toInt()
        val spacingPx = with(density) { 4.dp.roundToPx() }
        val suffixWidthPx = textMeasurer.measure(
            text = suffixText,
            style = suffixStyle,
            maxLines = 1
        ).size.width

        val combinedLayout = textMeasurer.measure(
            text = buildAnnotatedString {
                append(stationName)
                append(" ")
                append(suffixText)
            },
            style = stationStyle,
            constraints = Constraints(maxWidth = maxWidthPx)
        )
        val useCompactSingleLine = combinedLayout.lineCount > 2

        if (useCompactSingleLine) {
            val stationMaxWidthDp = with(density) {
                (maxWidthPx - suffixWidthPx - spacingPx).coerceAtLeast(0).toDp()
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.width(stationMaxWidthDp),
                    text = stationName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = stationStyle,
                    color = defaultTeam6Colors.white
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = suffixText,
                    style = suffixStyle,
                    color = defaultTeam6Colors.gray200
                )
            }
        } else {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = defaultTeam6Colors.white)) { append(stationName) }
                    append(" ")
                    withStyle(SpanStyle(color = defaultTeam6Colors.gray200)) { append(suffixText) }
                },
                maxLines = 2,
                overflow = TextOverflow.Clip,
                style = stationStyle
            )
        }
    }
}

private fun walkTimelineHeight(
    distanceMeter: Int,
    isFirstWalk: Boolean,
    isLastWalk: Boolean
): Dp {
    val baseHeight = (distanceMeter / 10f).dp.coerceIn(44.dp, 120.dp)
    return when {
        isFirstWalk -> baseHeight + 10.dp
        isLastWalk -> baseHeight - 4.dp
        else -> baseHeight
    }.coerceIn(40.dp, 130.dp)
}

@Composable
private fun BusNumberButton(
    busName: String,
    busColor: Color,
    modifier: Modifier = Modifier
) {
    val busNumber = busName.split(":")[1]
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
                text = busNumber,
                color = defaultTeam6Colors.white,
                style = defaultTeam6Typography.body6_B6R14
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

@Composable
private fun TransitFlagBadge(
    text: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = containerColor
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = containerColor,
            style = defaultTeam6Typography.detail3_M9
        )
    }
}

private const val BOARDING_STOP_ARRIVAL_DISTANCE_METER = 50.0
private const val BOARDING_COMPLETED_LEAVE_DISTANCE_METER = 150.0
private const val BOARDING_COMPLETED_STAY_MILLIS = 60_000L

@Preview
@Composable
fun ItineraryInfoDetailLegsPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItineraryInfoDetailLegs(
        currentLocation = LatLng(0.0, 0.0),
        legs = legs,
        userDeparted = true,
        busArrivalStatus = SparseArray<RealTimeBusArrival>()
    )
}
