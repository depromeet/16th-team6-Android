package com.depromeet.team6.presentation.ui.itinerary.component

import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.common.TransportVectorIconBitmap
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.presentation.util.view.toPx
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapInsets
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.overlay.TMapTrafficLine
import com.skt.tmap.overlay.TMapTrafficLine.TrafficLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun ItineraryMap(
    currentLocation: LatLng,
    legs: List<LegInfo>,
    departurePoint: Address,
    destinationPoint: Address,
    focusedMarkerParameter: FocusedMarkerParameter?,
    marginTop: Dp,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    currentLocationBtnClick: () -> Unit
) {
    val context = LocalContext.current
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }

    Timber.d("departurelocation : $departurePoint")
    val departLocation = LatLng(departurePoint.lat, departurePoint.lon)
    val destinationLocation = LatLng(destinationPoint.lat, destinationPoint.lon)
    val markerSizePx = 28.dp.toPx().toInt()

    LaunchedEffect(Unit) {
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        tMapView.setOnMapReadyListener {
            tMapView.mapType = TMapView.MapType.NIGHT
            isMapReady = true
        }
    }

    // 목적지, 도착지 변경되면 지도 갱신
    LaunchedEffect(departLocation, destinationLocation, isMapReady) {
        if (isMapReady) {
            val departTMapPoint = TMapPoint(departLocation.latitude, departLocation.longitude)
            val destinationTMapPoint = TMapPoint(destinationLocation.latitude, destinationLocation.longitude)
            val tMapPointList = arrayListOf(departTMapPoint, destinationTMapPoint)

            // 경로 그리기
            for (leg in legs) {
                // 라인 그리기
                val lineWayPoints = getWayPointList(leg.passShape)
                for (point in lineWayPoints) {
                    tMapPointList.add(point)
                }
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
                val trafficLine = TrafficLine(1, lineWayPoints)
                tmapTrafficLine.basicColor = TransportTypeUiMapper.getColor(leg.transportType, leg.subTypeIdx).toArgb()
                tmapTrafficLine.passedColor = TransportTypeUiMapper.getColor(leg.transportType, leg.subTypeIdx).toArgb()
                tmapTrafficLine.trafficLineList.add(trafficLine)
                tMapView.addTrafficLine(tmapTrafficLine)

                // 마커 그리기
                val markerTmapPoint = TMapPoint(leg.startPoint.lat, leg.startPoint.lon)
                tMapPointList.add(markerTmapPoint)
                val marker = TMapMarkerItem()
                marker.id = "marker_${leg.transportType}_${leg.sectionTime}"
                marker.tMapPoint = markerTmapPoint
                marker.icon = TransportVectorIconBitmap(
                    type = leg.transportType,
                    fillColor = TransportTypeUiMapper.getColor(leg.transportType, leg.subTypeIdx),
                    isMarker = true,
                    sizePx = markerSizePx,
                    context = context
                )
                tMapView.addTMapMarkerItem(marker)
            }

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

            // scale 기준점 설정
            val leftTopLocation =
                if (focusedMarkerParameter == null) {
                    departTMapPoint
                } else {
                    TMapPoint(
                        focusedMarkerParameter.lat,
                        focusedMarkerParameter.lon
                    )
                }
            val rightBottomLocation =
                if (focusedMarkerParameter == null) {
                    destinationTMapPoint
                } else {
                    TMapPoint(
                        legs[focusedMarkerParameter.legIndex].endPoint.lat,
                        legs[focusedMarkerParameter.legIndex].endPoint.lon
                    )
                }

            val focusBound =
                if (focusedMarkerParameter == null) {
                    tMapView.getBoundsFromPoints(tMapPointList)
                } else {
                    val focusPointList = arrayListOf<TMapPoint>()
                    val lineWayPoints = getWayPointList(legs[focusedMarkerParameter.legIndex].passShape)
                    for (point in lineWayPoints) {
                        focusPointList.add(point)
                    }
                    tMapView.getBoundsFromPoints(focusPointList)
                }

            // 지도 위치 조정
            val midPoint = getMidPoint(leftTopLocation, rightBottomLocation)
            tMapView.fitBounds(
                focusBound,
                TMapInsets.of(100, 100, 100, 100)
            )

            // 지도 Scale 조정
            tMapView.mapZoomIn()
        }
    }

    // 현재 위치 변경될 때만 마커 갱신
    LaunchedEffect(currentLocation, isMapReady) {
        if (isMapReady) {
            val tMapPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)

            withContext(Dispatchers.Main) {
                val markerDrawable =
                    ContextCompat.getDrawable(context, R.drawable.ic_home_current_location)
                val markerBitmap = markerDrawable?.toBitmap()

                val markerItem = TMapMarkerItem().apply {
                    id = "CurrentMarker"
                    name = "Current Location"
                    icon = markerBitmap
                    setTMapPoint(tMapPoint)
                }

                tMapView.addTMapMarkerItem(markerItem)
            }
        }
    }

    Box(
        modifier = modifier
            .background(defaultTeam6Colors.greyWashBackground)
    ) {
        // Tmap
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { context ->
                // FrameLayout을 직접 생성
                FrameLayout(context).apply {
                    // TMapView를 FrameLayout에 추가
                    addView(tMapView)
                }
            },
            update = { frameLayout ->
                // Update logic if needed (e.g., map settings)
            }
        )
        CircleBtnBack(
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 12.dp + marginTop)
                .noRippleClickable {
                    onBackPressed()
                }
        )

        Image(
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-16).dp, y = (-36).dp)
                .noRippleClickable {
                    currentLocationBtnClick()
                    tMapView.setCenterPoint(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                },
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_current_location),
            contentDescription = "ItineraryCircleBtnBack"
        )
    }
}

@Composable
fun CircleBtnBack(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .roundedBackgroundWithPadding(
                cornerRadius = 100.dp,
                backgroundColor = defaultTeam6Colors.greyElevatedBackground
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_grey),
            colorFilter = ColorFilter.tint(defaultTeam6Colors.white),
            contentDescription = "ItineraryCircleBtnBack"
        )
    }
}

private fun getMidPoint(point1: TMapPoint, point2: TMapPoint): LatLng {
    val midLatitude = (point1.latitude + point2.latitude) / 2
    val midLongitude = (point1.longitude + point2.longitude) / 2
    return LatLng(midLatitude, midLongitude)
}

fun getWayPointList(passShape: String): ArrayList<TMapPoint> {
    val pointList: ArrayList<TMapPoint> = passShape.split(" ").mapNotNull { pair ->
        val parts = pair.split(",")
        if (parts.size == 2) {
            val longitude = parts[0].toDoubleOrNull()
            val latitude = parts[1].toDoubleOrNull()
            if (longitude != null && latitude != null) TMapPoint(latitude, longitude) else null
        } else {
            null
        }
    }.toCollection(ArrayList())
    return pointList
}

@Preview
@Composable
fun ItineraryMapPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItineraryMap(
        marginTop = 10.dp,
        legs = legs,
        currentLocation = LatLng(37.5665, 126.9780),
        departurePoint = Address(
            name = "성균관대학교 자연과학캠퍼스",
            lat = 37.303534788694,
            lon = 127.01085807594,
            address = ""
        ),
        destinationPoint = Address(
            name = "우리집",
            lat = 37.296391553347,
            lon = 126.97755824522,
            address = ""
        ),
        onBackPressed = { },
        focusedMarkerParameter = null,
        currentLocationBtnClick = {}
    )
}
