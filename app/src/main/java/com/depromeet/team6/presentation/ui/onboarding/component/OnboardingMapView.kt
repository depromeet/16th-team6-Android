package com.depromeet.team6.presentation.ui.onboarding.component

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.ui.common.bottomsheet.AtChaLocationSettingBottomSheet
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun OnboardingMapView(
    currentLocation: Address,
    myAddress: Address,
    context: Context,
    modifier: Modifier = Modifier,
    getCenterLocation: (LatLng) -> Unit = {},
    buttonClicked: () -> Unit = {},
    backButtonClicked: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var isFirstZoom by remember { mutableStateOf(true) }
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }
    val offsetLat = 0.00005
    val coroutineScope = rememberCoroutineScope()
    var initialRenderDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        keyboardController?.hide()
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        tMapView.setOnMapReadyListener {
            tMapView.mapType = TMapView.MapType.NIGHT
            isMapReady = true

            // 드래그 종료 시 지도 중심 좌표 업데이트
            tMapView.setOnDisableScrollWithZoomLevelListener { _, _ ->
                startScrollIdleCheck(
                    scope = coroutineScope,
                    tMapView = tMapView,
                    getCenterLocation = getCenterLocation
                )
            }
        }
    }

    // 현재 위치 변경될 때만 현위치 마커 갱신
    LaunchedEffect(currentLocation, isMapReady) {
        if (isMapReady) {
            val tMapPoint = TMapPoint(currentLocation.lat, currentLocation.lon)

            withContext(Dispatchers.Main) {
                if (isFirstZoom) {
                    tMapView.zoomLevel = 18
                    isFirstZoom = false
                    tMapView.setCenterPoint(
                        currentLocation.lat - offsetLat,
                        currentLocation.lon
                    )
                } else {
                    tMapView.setCenterPoint(
                        currentLocation.lat,
                        currentLocation.lon
                    )
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
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { ctx ->
                FrameLayout(ctx).apply {
                    addView(tMapView)

                    viewTreeObserver.addOnGlobalLayoutListener {
                        if (isMapReady && !initialRenderDone) {
                            initialRenderDone = true

                            val initialLat = currentLocation.lat - offsetLat
                            val initialLon = currentLocation.lon

                            tMapView.setCenterPoint(initialLat, initialLon, true)
                            tMapView.zoomLevel = 18

                            val markerDrawable = ContextCompat.getDrawable(ctx, R.drawable.ic_home_current_location)
                            val markerBitmap = markerDrawable?.toBitmap()

                            val markerItem = TMapMarkerItem().apply {
                                id = "CurrentMarker"
                                icon = markerBitmap
                                setTMapPoint(TMapPoint(currentLocation.lat, currentLocation.lon))
                            }

                            tMapView.addTMapMarkerItem(markerItem)
                        }
                    }
                }
            }
        )

        Icon(
            modifier = Modifier
                .align(Alignment.TopStart)
                .noRippleClickable { backButtonClicked() }
                .padding(vertical = 16.dp, horizontal = 18.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_white),
            tint = Color.Unspecified,
            contentDescription = null
        )

        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // 출발 마커
            Box(Modifier.fillMaxWidth().weight(1f)) {
                Icon(
                    tint = Color.Unspecified,
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_map_marker_setting),
                    contentDescription = "Start Marker",
                    modifier = Modifier
                        .align(Alignment.Center)
                )

                Icon(
                    tint = Color.Unspecified,
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_current_location),
                    contentDescription = stringResource(R.string.home_current_location_btn),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                        .clickable(enabled = isMapReady) {
                            val tMapPoint =
                                TMapPoint(currentLocation.lat, currentLocation.lon)
                            tMapView.setCenterPoint(tMapPoint.latitude - offsetLat, tMapPoint.longitude)
                            tMapView.zoomLevel = 18
                            getCenterLocation(LatLng(tMapPoint.latitude, tMapPoint.longitude))
                        }
                        .graphicsLayer { alpha = if (isMapReady) 1f else 0.5f } // 비활성화 시 투명도 조정
                )
            }

            AtChaLocationSettingBottomSheet(
                locationName = myAddress.name,
                locationAddress = myAddress.address,
                completeButtonText = "우리집 등록",
                buttonClicked = buttonClicked
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Timber.d("TMapViewCompose destroy!")
            tMapView.onDestroy()
        }
    }
}

private var checkScrollJob: Job? = null

fun startScrollIdleCheck(
    scope: CoroutineScope,
    tMapView: TMapView,
    getCenterLocation: (LatLng) -> Unit
) {
    checkScrollJob?.cancel()
    checkScrollJob = scope.launch {
        var previousLatLng: LatLng? = null
        var sameCount = 0

        while (sameCount < 1) {
            val currentLatLng = LatLng(
                tMapView.centerPoint.latitude,
                tMapView.centerPoint.longitude
            )

            if (previousLatLng == currentLatLng) {
                sameCount++
            } else {
                sameCount = 0
            }

            previousLatLng = currentLatLng
            delay(70L)
        }

        getCenterLocation(previousLatLng!!)
    }
}
