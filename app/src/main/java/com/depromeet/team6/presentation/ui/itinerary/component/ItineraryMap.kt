package com.depromeet.team6.presentation.ui.itinerary.component

import android.widget.FrameLayout
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.overlay.TMapPolyLine
import com.skt.tmap.overlay.TMapTrafficLine
import com.skt.tmap.overlay.TMapTrafficLine.TrafficLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun ItineraryMap(
    currentLocation: LatLng,
    legs: List<LegInfo>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }

    val departLocation = LatLng(legs[0].startPoint.lat, legs[0].startPoint.lon)
    val destinationLocation = LatLng(legs[legs.size - 1].endPoint.lat, legs[legs.size - 1].endPoint.lon)

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

            // TMapTrafficLine 객체 생성
            val tmapTrafficLine = TMapTrafficLine("line_itinerary")
            // 교통 정보 표출 여부 설정
            tmapTrafficLine.isShowTraffic = true
            // 방향 인디케이터(화살표) 표시 설정
            tmapTrafficLine.isShowIndicator = true
            // 경로 선의 두께 설정
            tmapTrafficLine.lineWidth = 9
            // 경로 외곽선의 두께 설정
            tmapTrafficLine.outLineWidth = 2

            // 라인 그리기
            for (leg in legs) {
                val lineWayPoints = getWayPointList(leg.passShape)


                // TrafficLine 객체 생성 후 리스트에 추가
                val trafficLine = TrafficLine(1, lineWayPoints)
                tmapTrafficLine.trafficLineList.add(trafficLine)


                val line = TMapPolyLine("line_${leg.transportType}_${leg.sectionTime}", lineWayPoints)
                tMapView.addTMapPolyLine(line)
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

            // 지도 위치 조정
            val midPoint = getMidPoint(departLocation, destinationLocation)
            tMapView.setCenterPoint(midPoint.latitude, midPoint.longitude)

            // 지도 Scale 조정
            val leftTop = departTMapPoint
            val rightBottom = destinationTMapPoint
            tMapView.zoomToTMapPoint(leftTop, rightBottom)
            tMapView.mapZoomOut()
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
                .offset(x = 16.dp, y = 12.dp)
                .noRippleClickable {
                    // TODO : 뒤로가기
                }
        )

        Image(
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-16).dp, y = (-16).dp)
                .noRippleClickable {
                    tMapView.setCenterPoint(currentLocation.latitude, currentLocation.longitude)
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
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_white),
            contentDescription = "ItineraryCircleBtnBack"
        )
    }
}

private fun getMidPoint(point1: LatLng, point2: LatLng): LatLng {
    val midLatitude = (point1.latitude + point2.latitude) / 2
    val midLongitude = (point1.longitude + point2.longitude) / 2
    return LatLng(midLatitude, midLongitude)
}

private fun getWayPointList(passShape: String): ArrayList<TMapPoint> {
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
        legs = legs,
        currentLocation = LatLng(37.5665, 126.9780)
    )
}
