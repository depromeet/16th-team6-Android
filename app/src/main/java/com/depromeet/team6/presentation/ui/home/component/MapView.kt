package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_COURSESEARCH_ENTERED_WITH_CURRENT_LOCATION
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_COURSESEARCH_ENTERED_WITH_MAP_DRAG
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_COURSESEARCH_ENTERED
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import timber.log.Timber

@Composable
fun TMapViewCompose(
    padding: PaddingValues,
    currentLocation: LatLng,
    isAlarmRegistered: Boolean,
    isMapFocused: Boolean,
    userId: Int,
    modifier: Modifier = Modifier,
    getCenterLocation: (LatLng) -> Unit,
    mapModified: () -> Unit
) {
    val context = LocalContext.current
    var isMapReady by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // focus 버튼 누를때마다 해당 위치로 지도 focus 이동
//    LaunchedEffect(isMapFocused) {
//        if (isMapFocused && isMapReady) {
//            tMapView.setCenterPoint(currentLocation.latitude, currentLocation.longitude)
//            getCenterLocation(LatLng(currentLocation.latitude, currentLocation.longitude)) // 필요없어보여서 주석처리 해뒀어요
//
//            AmplitudeUtils.trackEventWithProperties(
//                eventName = HOME_EVENT_COURSESEARCH_ENTERED,
//                mapOf(
//                    USER_ID to userId,
//                    SCREEN_NAME to HOME,
//                    HOME_COURSESEARCH_ENTERED_WITH_CURRENT_LOCATION to true
//                )
//            )
//        }
//    }

    Box(
        modifier = modifier
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight - 180.dp + padding.calculateTopPadding())
                .align(Alignment.TopCenter),
            factory = { context ->
                val tMapView = TMapView(context)

                tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
                tMapView.mapType = TMapView.MapType.NIGHT
                tMapView.setOnMapReadyListener {
                    getCenterLocation(currentLocation)
                    val currentPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)
                    tMapView.fitBounds(
                        tMapView.getBoundsFromPoints(
                            arrayListOf(currentPoint)
                        )
                    )

                    // 현위치 마커
                    val markerDrawable =
                        ContextCompat.getDrawable(context, R.drawable.ic_home_current_location)
                    val markerBitmap = markerDrawable?.toBitmap()

                    val markerItem = TMapMarkerItem().apply {
                        id = "CurrentMarker"
                        name = "Current Location"
                        icon = markerBitmap
                        tMapPoint = currentPoint
                        isAnimation = true
                    }
                    tMapView.addTMapMarkerItem(markerItem)

                    // 드래그 종료 시 지도 중심 좌표 업데이트
                    tMapView.setOnDisableScrollWithZoomLevelListener { _, _ ->
                        val centerLat = tMapView.centerPoint.latitude
                        val centerLon = tMapView.centerPoint.longitude

                        getCenterLocation(LatLng(centerLat, centerLon))

                        AmplitudeUtils.trackEventWithProperties(
                            eventName = HOME_EVENT_COURSESEARCH_ENTERED,
                            mapOf(
                                USER_ID to userId,
                                SCREEN_NAME to HOME,
                                HOME_COURSESEARCH_ENTERED_WITH_MAP_DRAG to true
                            )
                        )
                    }

                    // 화면 스크롤 발생시 mapFocused 여부 변경
                    tMapView.setOnEnableScrollWithZoomLevelListener { _, _ ->
                        mapModified()
                    }
                    isMapReady = true
                }

                tMapView
            },
            update = { tMapView ->
                // 지도 준비가 안된상태에서 리컴포즈 방지
                if (!isMapReady) return@AndroidView

                val currentPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)

                val existingMarker = tMapView.getMarkerItemFromId("CurrentMarker")
                existingMarker.tMapPoint = currentPoint
                tMapView.addTMapMarkerItem(existingMarker)
                tMapView.updateTMapMarkerItem(existingMarker)

                if (isMapFocused) {
                    Timber.d("currentLocation Changed : ${currentLocation.latitude}, ${currentLocation.longitude}")
                    tMapView.setCenterPoint(currentLocation.latitude, currentLocation.longitude)
                    getCenterLocation(LatLng(currentLocation.latitude, currentLocation.longitude)) // 필요없어보여서 주석처리 해뒀어요
                    tMapView.zoomLevel = 18

                    AmplitudeUtils.trackEventWithProperties(
                        eventName = HOME_EVENT_COURSESEARCH_ENTERED,
                        mapOf(
                            USER_ID to userId,
                            SCREEN_NAME to HOME,
                            HOME_COURSESEARCH_ENTERED_WITH_CURRENT_LOCATION to true
                        )
                    )
                }
            }
        )

        if (isMapReady) {
            // 출발 마커
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_home_dearture_marker),
                contentDescription = "Start Marker",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 118.dp)
            )
        } else {
            AtChaLoadingView(
                transparent = false
            )
        }
    }
}
