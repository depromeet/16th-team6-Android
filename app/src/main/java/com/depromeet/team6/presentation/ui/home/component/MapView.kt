package com.depromeet.team6.presentation.ui.home.component

import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.home.HomeViewModel
import com.depromeet.team6.presentation.ui.onboarding.component.startScrollIdleCheck
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun TMapViewCompose(
    currentLocation: LatLng,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value
    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_START) {
                Timber.d("TMapView - ON_START")
                tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
                tMapView.setOnMapReadyListener {
                    tMapView.mapType = TMapView.MapType.NIGHT
                    isMapReady = true

                    // 드래그 종료 시 지도 중심 좌표 업데이트
                    tMapView.setOnDisableScrollWithZoomLevelListener { _, _ ->
                        val centerLat = tMapView.centerPoint.latitude
                        val centerLon = tMapView.centerPoint.longitude

                        viewModel.getCenterLocation(LatLng(centerLat, centerLon))
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            Timber.d("TMapViewCompose destroy!")
            tMapView.onDestroy()
        }
    }

    // 현재 위치 변경될 때만 현위치 마커 갱신
    LaunchedEffect(currentLocation, isMapReady) {
        if (isMapReady) {
            val tMapPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)

            withContext(Dispatchers.Main) {
                tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude)
                tMapView.zoomLevel = 18

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

    if (isMapReady) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            AndroidView(
                modifier = modifier.fillMaxSize(),
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
                imageVector = ImageVector.vectorResource(id = R.drawable.map_marker_departure),
                contentDescription = "Start Marker",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 105.dp)
            )

            // 현위치 버튼
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_current_location),
                contentDescription = stringResource(R.string.home_current_location_btn),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .then(
                        if (uiState.isAlarmRegistered) {
                            Modifier.padding(bottom = 274.dp, end = 16.dp)
                        } else {
                            Modifier.padding(bottom = 240.dp, end = 16.dp)
                        }
                    )
                    .clickable(enabled = isMapReady) {
                        val tMapPoint =
                            TMapPoint(currentLocation.latitude, currentLocation.longitude)
                        tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude)

                        viewModel.getCenterLocation(LatLng(tMapPoint.latitude, tMapPoint.longitude))
                    }
                    .graphicsLayer { alpha = if (isMapReady) 1f else 0.5f } // 비활성화 시 투명도 조정
            )
        }
    }
}
