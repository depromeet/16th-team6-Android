package com.depromeet.team6.presentation.ui.home.component

import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.overlay.TMapPolyLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
fun AfterRegisterMap(
    currentLocation: LatLng,
    legs: List<LegInfo>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }

    val departLocation = LatLng(legs[0].startPoint.latitude, legs[0].startPoint.longitude)
    val destinationLocation = LatLng(legs[legs.size - 1].endPoint.latitude, legs[legs.size - 1].endPoint.longitude)

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

            // 라인 그리기
            for (leg in legs) {
                val lineWayPoints = getWayPointList(leg.passShape)
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
            marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_all_location_black)?.toBitmap()
            tMapView.addTMapMarkerItem(marker)

            // 지도 위치 설정 - 현위치와 출발지의 중간 지점
            val midPoint = getMidPoint(currentLocation, departLocation)
            tMapView.setCenterPoint(midPoint.latitude, midPoint.longitude)

            // 지도 Scale 조정 - 현위치와 출발지 차이 + 일정 값
            val latSpan = abs(currentLocation.latitude - departLocation.latitude) + 0.001
            val lonSpan = abs(currentLocation.longitude - departLocation.longitude) + 0.003
            tMapView.zoomToSpan(latSpan, lonSpan)
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
            modifier = modifier
                .fillMaxWidth()
                // TODO : 하단 모달 영역 제외한 부분에 띄우도록 수정
                .height(500.dp)
                .align(Alignment.TopCenter)
            ,
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
fun AfterRegisterMapPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    AfterRegisterMap(
        legs = legs,
        currentLocation = LatLng(37.5665, 126.9780)
    )
}
