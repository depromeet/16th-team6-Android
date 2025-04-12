package com.depromeet.team6.presentation.ui.mypage.component

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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun MypageMapView(
    currentLocation: Address,
    myAddress: Address,
    context: Context,
    modifier: Modifier = Modifier,
    getCenterLocation: (LatLng) -> Unit = {},
    buttonClicked: () -> Unit = {},
    backButtonClicked: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isFirstZoom by remember { mutableStateOf(true) }
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }
    val offsetLat = 0.00005
    val coroutineScope = rememberCoroutineScope()

    // Lifecycle 제어: ON_START 이후에만 지도 초기화
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_START) {
                Timber.d("TMapView - ON_START")
                keyboardController?.hide()
                tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
                tMapView.setOnMapReadyListener {
                    tMapView.mapType = TMapView.MapType.NIGHT
                    isMapReady = true

                    tMapView.setOnDisableScrollWithZoomLevelListener { _, _ ->
                        startScrollIdleCheck(
                            scope = coroutineScope,
                            tMapView = tMapView,
                            getCenterLocation = getCenterLocation
                        )
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

    // 지도 준비된 후에만 AndroidView 추가
    if (isMapReady) {
        Box(modifier = modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    FrameLayout(context).apply {
                        addView(tMapView)
                        tMapView.post {
                            if (isFirstZoom) {
                                val lat = currentLocation.lat - offsetLat
                                val lon = currentLocation.lon

                                tMapView.setCenterPoint(lat, lon, true)
                                tMapView.zoomLevel = 18
                                isFirstZoom = false

                                val markerDrawable =
                                    ContextCompat.getDrawable(context, R.drawable.ic_home_current_location)
                                val markerBitmap = markerDrawable?.toBitmap()

                                val markerItem = TMapMarkerItem().apply {
                                    id = "CurrentMarker"
                                    name = "Current Location"
                                    icon = markerBitmap
                                    setTMapPoint(TMapPoint(currentLocation.lat, currentLocation.lon))
                                }

                                tMapView.addTMapMarkerItem(markerItem)
                            }
                        }
                    }
                }
            )

            // 뒤로가기 아이콘
            Icon(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .noRippleClickable { backButtonClicked() }
                    .padding(vertical = 16.dp, horizontal = 18.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_white),
                tint = Color.Unspecified,
                contentDescription = null
            )

            // 하단 UI
            Column(modifier = modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Icon(
                        tint = Color.Unspecified,
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_map_marker_setting),
                        contentDescription = "Start Marker",
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Icon(
                        tint = Color.Unspecified,
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_current_location),
                        contentDescription = stringResource(R.string.home_current_location_btn),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 16.dp)
                            .clickable(enabled = isMapReady) {
                                val tMapPoint = TMapPoint(currentLocation.lat, currentLocation.lon)
                                tMapView.setCenterPoint(tMapPoint.latitude - offsetLat, tMapPoint.longitude)
                                tMapView.zoomLevel = 18
                                getCenterLocation(LatLng(tMapPoint.latitude, tMapPoint.longitude))
                            }
                            .graphicsLayer { alpha = if (isMapReady) 1f else 0.5f }
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
    }
}

private var checkScrollJob: Job? = null

private fun startScrollIdleCheck(
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