package com.depromeet.team6.presentation.ui.home.component

import android.widget.FrameLayout
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_COURSESEARCH_ENTERED_WITH_CURRENT_LOCATION
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_COURSESEARCH_ENTERED_WITH_MAP_DRAG
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun TMapViewCompose(
    padding: PaddingValues,
    currentLocation: LatLng,
    modifier: Modifier = Modifier,
    isAlarmRegistered: Boolean,
    userId: Int,
    getCenterLocation: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(Unit) {
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        tMapView.setOnMapReadyListener {
            tMapView.mapType = TMapView.MapType.NIGHT
            isMapReady = true

            // 드래그 종료 시 지도 중심 좌표 업데이트
            tMapView.setOnDisableScrollWithZoomLevelListener { _, _ ->
                val centerLat = tMapView.centerPoint.latitude
                val centerLon = tMapView.centerPoint.longitude

                getCenterLocation(LatLng(centerLat, centerLon))

                AmplitudeUtils.trackEventWithProperties(
                    eventName = HOME_COURSESEARCH_ENTERED_WITH_MAP_DRAG,
                    mapOf(
                        USER_ID to userId,
                        SCREEN_NAME to HOME,
                        HOME_COURSESEARCH_ENTERED_WITH_MAP_DRAG to true
                    )
                )
            }
        }
    }

    // 현재 위치 변경될 때만 현위치 마커 갱신
    LaunchedEffect(currentLocation, isMapReady) {
        if (isMapReady) {
            val tMapPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)

            withContext(Dispatchers.Main) {
                tMapView.fitBounds(
                    tMapView.getBoundsFromPoints(
                        arrayListOf(tMapPoint)
                    )
                )
//                tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude)
//                tMapView.fitBounds(tMapView.bounds)
//                tMapView.zoomLevel = 18

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
        AndroidView(
            modifier = modifier.fillMaxWidth()
                .height(screenHeight - 200.dp + padding.calculateTopPadding())
                .align(Alignment.TopCenter),
            factory = { context ->
                // FrameLayout을 직접 생성
                FrameLayout(context).apply {
                    // TMapView를 FrameLayout에 추가
                    addView(tMapView)
                }
            },
            update = { _ ->
                // Update logic if needed (e.g., map settings)
            }
        )

        // 출발 마커
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_home_dearture_marker),
            contentDescription = "Start Marker",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 118.dp)
        )

        // 현위치 버튼
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_current_location),
            contentDescription = stringResource(R.string.home_current_location_btn),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .then(
                    if (isAlarmRegistered) {
                        Modifier.padding(bottom = 35.dp, end = 16.dp)
                    } else {
                        Modifier.padding(bottom = 35.dp, end = 16.dp)
                    }
                )
                .clickable(enabled = isMapReady) {
                    val tMapPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)
                    tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude)

                    getCenterLocation(LatLng(tMapPoint.latitude, tMapPoint.longitude))

                    AmplitudeUtils.trackEventWithProperties(
                        eventName = HOME_COURSESEARCH_ENTERED_WITH_CURRENT_LOCATION,
                        mapOf(
                            USER_ID to userId,
                            SCREEN_NAME to HOME,
                            HOME_COURSESEARCH_ENTERED_WITH_CURRENT_LOCATION to true
                        )
                    )
                }
                .graphicsLayer { alpha = if (isMapReady) 1f else 0.5f } // 비활성화 시 투명도 조정
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            Timber.d("TMapViewCompose destroy!")
            tMapView.onDestroy()
        }
    }
}
