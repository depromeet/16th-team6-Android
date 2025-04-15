package com.depromeet.team6.presentation.ui.home.component

import TransportVectorIconWithTextBitmap
import android.graphics.PointF
import android.location.Location
import android.os.Looper
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.common.TransportVectorIconBitmap
import com.depromeet.team6.presentation.ui.home.HomeViewModel
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.ui.itinerary.component.getWayPointList
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.toast.atChaToastMessage
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.presentation.util.view.toPx
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.TMapView.OnClickListenerCallback
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.overlay.TMapTrafficLine
import com.skt.tmap.poi.TMapPOIItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.abs

@Composable
fun AfterRegisterMap(
    padding: PaddingValues,
    currentLocation: LatLng,
    legs: List<LegInfo>,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onTransportMarkerClick: (FocusedMarkerParameter) -> Unit = {}
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState().value
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }

    var userLocation by remember { mutableStateOf(currentLocation) }
    var locationUpdateTrigger by remember { mutableStateOf(0) }

    val departLocation = LatLng(legs[0].startPoint.lat, legs[0].startPoint.lon)
    val destinationLocation = LatLng(legs[legs.size - 1].endPoint.lat, legs[legs.size - 1].endPoint.lon)
    val markerSizePx = 28.dp.toPx().toInt()

    var firstTransportationPoint = LatLng(legs[0].startPoint.lat, legs[0].startPoint.lon)
    var markBusStationName = ""

    var hasShownToast by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

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

            for (leg in legs) {
                if (leg.transportType != TransportType.WALK) {
                    firstTransportationPoint = LatLng(leg.startPoint.lat, leg.startPoint.lon)
                    if (leg.transportType == TransportType.BUS) {
                        markBusStationName = leg.startPoint.name
                    }
                    break
                }
            }

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

            // 지도 위치 설정 - 출발지와 첫 대중교통의 중간 지점
            val midPoint = getMidPoint(firstTransportationPoint, departLocation)
            tMapView.setCenterPoint(midPoint.latitude, midPoint.longitude)

            // 지도 Scale 조정 - 출발지와 첫 대중교통의 중간 지점 + 일정 값
            val latSpan = abs(firstTransportationPoint.latitude - departLocation.latitude) + 0.01 // 0.01 or 0.001
            val lonSpan = abs(firstTransportationPoint.longitude - departLocation.longitude) + 0.003
            tMapView.zoomToSpan(latSpan, lonSpan)
        }
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val newLocation = LatLng(location.latitude, location.longitude)
                    userLocation = newLocation
                    locationUpdateTrigger++

                    val distance = calculateDistance(
                        newLocation.latitude,
                        newLocation.longitude,
                        firstTransportationPoint.latitude,
                        firstTransportationPoint.longitude
                    )

                    if (distance <= 50f && !hasShownToast) {
                        atChaToastMessage(context, R.string.home_arrive_station_toast_text, Toast.LENGTH_LONG)
                        hasShownToast = true
                    }
                }
            }
        }
    }

    // 위치 업데이트 시작
    LaunchedEffect(Unit) {
        if (PermissionUtil.hasLocationPermissions(context)) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(1000)
                .setMinUpdateDistanceMeters(1f)
                .build()

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                Timber.e("위치 권한 오류: ${e.message}")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // userLocation 또는 locationUpdateTrigger가 변경될 때마다 현재 위치 마커 업데이트
    LaunchedEffect(userLocation, locationUpdateTrigger, isMapReady) {
        if (isMapReady) {
            val tMapPoint = TMapPoint(userLocation.latitude, userLocation.longitude)

            withContext(Dispatchers.Main) {
                try {
                    tMapView.removeTMapMarkerItem("CurrentMarker")
                } catch (e: Exception) {
                }

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

                viewModel.updateCurrentLocation(userLocation)
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
                .height(screenHeight - 248.dp + padding.calculateBottomPadding())
                .align(Alignment.TopCenter),
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

        // 현위치 버튼
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_current_location),
            contentDescription = stringResource(R.string.home_current_location_btn),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .then(
                    if (uiState.isAlarmRegistered) {
                        Modifier.padding(bottom = 25.dp, end = 16.dp)
                    } else {
                        Modifier.padding(bottom = 25.dp, end = 16.dp)
                    }
                )
                .clickable(enabled = isMapReady) {
                    val tMapPoint = TMapPoint(userLocation.latitude, userLocation.longitude)
                    tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude)

                    viewModel.getCenterLocation(LatLng(tMapPoint.latitude, tMapPoint.longitude))
                }
                .graphicsLayer { alpha = if (isMapReady) 1f else 0.5f } // 비활성화 시 투명도 조정
        )
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
        modifier = TODO(),
        viewModel = TODO()
    )
}
