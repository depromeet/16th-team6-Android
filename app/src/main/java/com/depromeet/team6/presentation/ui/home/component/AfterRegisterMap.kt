package com.depromeet.team6.presentation.ui.home.component

import TransportVectorIconWithTextBitmap
import android.graphics.PointF
import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.model.home.MapFocusState
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.common.TransportVectorIconBitmap
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.ui.itinerary.component.getWayPointList
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.presentation.util.view.toPx
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.TMapView.OnClickListenerCallback
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.overlay.TMapTrafficLine
import com.skt.tmap.poi.TMapPOIItem
import timber.log.Timber
import kotlin.math.abs

@Composable
fun AfterRegisterMap(
    padding: PaddingValues,
    currentLocation: LatLng,
    legs: List<LegInfo>,
    isAlarmRegistered: Boolean,
    isMapFocused: MapFocusState,
    initialMapFocus: MapFocusState,
    modifier: Modifier = Modifier,
    mapModified: () -> Unit,
    getCenterLocation: (LatLng) -> Unit,
    onTransportMarkerClick: (FocusedMarkerParameter) -> Unit = {},
    isMapReadyCallback: () -> Unit = {}
) {
    var isMapReady by remember { mutableStateOf(false) }
    var hasAppliedInitialFocus by remember { mutableStateOf(false) }

    var locationUpdateTrigger by remember { mutableStateOf(0) }

    val departLocation = LatLng(legs[0].startPoint.lat, legs[0].startPoint.lon)
    val destinationLocation = LatLng(legs[legs.size - 1].endPoint.lat, legs[legs.size - 1].endPoint.lon)
    val markerSizePx = 28.dp.toPx().toInt()

    val (firstTransportationPoint, markBusStationName) = remember(legs) {
        val firstTransportLeg = legs.firstOrNull { it.transportType != TransportType.WALK }
        val point = firstTransportLeg?.let { LatLng(it.startPoint.lat, it.startPoint.lon) }
            ?: LatLng(legs[0].startPoint.lat, legs[0].startPoint.lon)
        val busStationName = firstTransportLeg?.takeIf { it.transportType == TransportType.BUS }
            ?.startPoint?.name ?: ""

        Pair(point, busStationName)
    }

    var hasShownToast by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Box(
        modifier = modifier
    ) {
        // Tmap
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                // TODO : 하단 모달 영역 제외한 부분에 띄우도록 수정
                .height(screenHeight - 248.dp + padding.calculateBottomPadding())
                .align(Alignment.TopCenter),
            factory = { context ->
                val tMapView = TMapView(context)

                tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
                tMapView.setOnMapReadyListener {
                    tMapView.mapType = TMapView.MapType.NIGHT

                    val departTMapPoint = TMapPoint(departLocation.latitude, departLocation.longitude)
                    val destinationTMapPoint = TMapPoint(destinationLocation.latitude, destinationLocation.longitude)

                    // 경로 그리기
                    for ((index, leg) in legs.withIndex()) {
                        // 라인 그리기
                        val lineWayPoints =
                            getWayPointList(leg.passShape)
                        // TMapTrafficLine 객체 생성
                        val tmapTrafficLine = TMapTrafficLine("line_${leg.transportType}_${leg.sectionTime}")
                        // 교통 정보 표출 여부 설정
                        tmapTrafficLine.isShowTraffic = false
                        // 방향 인디케이터(화살표) 표시 설정
                        tmapTrafficLine.isShowIndicator = true
                        // 경로 선의 두께 설정
                        tmapTrafficLine.lineWidth = 9
                        // 경로 외곽선의 두께 설정
                        tmapTrafficLine.outLineWidth = 0

                        // TrafficLine 객체 생성 후 리스트에 추가
                        val trafficLine = TMapTrafficLine.TrafficLine(1, lineWayPoints)
                        tmapTrafficLine.basicColor = TransportTypeUiMapper.getColor(leg.transportType, leg.subTypeIdx).toArgb()
                        tmapTrafficLine.passedColor = TransportTypeUiMapper.getColor(leg.transportType, leg.subTypeIdx).toArgb()
                        tmapTrafficLine.trafficLineList.add(trafficLine)
                        tMapView.addTrafficLine(tmapTrafficLine)

                        // 마커 그리기
                        val marker = TMapMarkerItem()
                        marker.id = "marker_${leg.transportType}_${leg.subTypeIdx}_$index"

                        if ((leg.transportType == TransportType.WALK) && (lineWayPoints.isNotEmpty())) {
                            marker.tMapPoint = lineWayPoints[0]
                        } else {
                            marker.tMapPoint = TMapPoint(leg.startPoint.lat, leg.startPoint.lon)
                        }

                        if ((firstTransportationPoint == LatLng(leg.startPoint.lat, leg.startPoint.lon)) && (leg.transportType == TransportType.BUS)) {
                            marker.icon = TransportVectorIconWithTextBitmap(
                                type = leg.transportType,
                                fillColor = TransportTypeUiMapper.getColor(
                                    leg.transportType,
                                    leg.subTypeIdx
                                ),
                                isMarker = true,
                                context = context,
                                iconSizePx = markerSizePx,
                                name = markBusStationName,
                                textPadding = 4
                            )
                        } else {
                            marker.icon = TransportVectorIconBitmap(
                                type = leg.transportType,
                                fillColor = TransportTypeUiMapper.getColor(
                                    leg.transportType,
                                    leg.subTypeIdx
                                ),
                                isMarker = true,
                                sizePx = markerSizePx,
                                context = context
                            )
                        }

                        tMapView.addTMapMarkerItem(marker)
                    }

                    tMapView.setOnClickListenerCallback(object : OnClickListenerCallback {
                        override fun onPressDown(
                            p0: ArrayList<TMapMarkerItem>?,
                            p1: ArrayList<TMapPOIItem>?,
                            p2: TMapPoint?,
                            p3: PointF?
                        ) {
                            Timber.d("on TMap Press Down : $p0 / $p1 / $p2 / $p3")
                        }

                        override fun onPressUp(
                            markerItems: ArrayList<TMapMarkerItem>?,
                            p1: ArrayList<TMapPOIItem>?,
                            latLng: TMapPoint?,
                            p3: PointF?
                        ) {
                            if (markerItems?.isEmpty() == true) return

                            val marker = markerItems!![0]
                            val parts = marker.id.split("_")
                            if (parts[0] == "departPoint" || parts[0] == "destinationPoint" || parts[0] == "CurrentMarker") return
                            val transportTypeStr = parts[1]
                            val subTypeIdx = parts[2].toInt()
                            val transportType = enumValueOf<TransportType>(transportTypeStr)
                            val legIndex = parts[3].toInt()

                            if (transportType == TransportType.WALK) return
                            onTransportMarkerClick(
                                FocusedMarkerParameter(
                                    lat = latLng!!.latitude,
                                    lon = latLng.longitude,
                                    transportType = transportType,
                                    subTypeIdx = subTypeIdx,
                                    legIndex = legIndex
                                )
                            )
                        }
                    })

                    // 마커 설정
                    val marker = TMapMarkerItem()
                    marker.id = "departPoint"
                    marker.tMapPoint = departTMapPoint
                    marker.icon = ContextCompat.getDrawable(context, R.drawable.map_marker_departure)?.toBitmap()
                    tMapView.addTMapMarkerItem(marker)

                    marker.id = "destinationPoint"
                    marker.tMapPoint = destinationTMapPoint
                    marker.icon = ContextCompat.getDrawable(context, R.drawable.map_marker_arrival)?.toBitmap()
                    tMapView.addTMapMarkerItem(marker)

                    val currentMarkerDrawable =
                        ContextCompat.getDrawable(context, R.drawable.ic_home_current_location)
                    val markerBitmap = currentMarkerDrawable?.toBitmap()
                    val currentPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)

                    val currentMarker = TMapMarkerItem().apply {
                        id = "CurrentMarker"
                        name = "Current Location"
                        icon = markerBitmap
                        tMapPoint = currentPoint
                    }
                    tMapView.addTMapMarkerItem(currentMarker)

                    tMapView.isTrackingMode = false
                    tMapView.setSightVisible(false)
                    tMapView.isCompassMode = false

                    // 지도 Scale 조정 - 출발지와 첫 대중교통의 중간 지점 + 일정 값
                    val latSpan = abs(firstTransportationPoint.latitude - departLocation.latitude) + 0.01 // 0.01 or 0.001
                    val lonSpan = abs(firstTransportationPoint.longitude - departLocation.longitude) + 0.003
                    tMapView.zoomToSpan(latSpan, lonSpan)

                    // 화면 스크롤 발생시 mapFocused 여부 변경
                    tMapView.setOnEnableScrollWithZoomLevelListener { _, _ ->
                        mapModified()
                    }

                    isMapReady = true
                }

                tMapView
            },
            update = { tMapView ->
                if (!isMapReady) return@AndroidView

                val currentPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)
                val existingMarker = tMapView.getMarkerItemFromId("CurrentMarker")
                existingMarker.tMapPoint = currentPoint
                tMapView.updateTMapMarkerItem(existingMarker)

                if (!hasAppliedInitialFocus) {
                    // 지도 Focus에 따른 위치 설정
                    when (initialMapFocus) {
                        // 지도 위치 설정 - 출발지와 첫 대중교통의 중간 지점
                        MapFocusState.Departure -> {
                            val midPoint = getMidPoint(firstTransportationPoint, departLocation)
                            tMapView.setCenterPoint(
                                midPoint.latitude,
                                midPoint.longitude
                            )
                        }

                        MapFocusState.Current -> {
                            tMapView.setCenterPoint(
                                currentLocation.latitude,
                                currentLocation.longitude
                            )
                        }

                        MapFocusState.Modify -> Unit
                    }

                    hasAppliedInitialFocus = true
                    return@AndroidView
                }

                if (isMapFocused == MapFocusState.Current) {
                    tMapView.setCenterPoint(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                }
            }
        )

        if (isMapReady) {
            isMapReadyCallback()
        } else {
            AtChaLoadingView(
                transparent = false
            )
        }
    }
}

private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0]
}

private fun getMidPoint(point1: LatLng, point2: LatLng): LatLng {
    val midLatitude = (point1.latitude + point2.latitude) / 2
    val midLongitude = (point1.longitude + point2.longitude) / 2
    return LatLng(midLatitude, midLongitude)
}

@Preview
@Composable
fun AfterRegisterMapPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    AfterRegisterMap(
        padding = PaddingValues(),
        legs = legs,
        currentLocation = LatLng(37.5665, 126.9780),
        isAlarmRegistered = false,
        isMapFocused = MapFocusState.Departure,
        initialMapFocus = MapFocusState.Departure,
        getCenterLocation = {},
        mapModified = {}
    )
}
