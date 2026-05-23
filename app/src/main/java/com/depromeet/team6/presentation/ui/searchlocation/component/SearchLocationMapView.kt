package com.depromeet.team6.presentation.ui.searchlocation.component

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
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
fun SearchLocationMapView(
    marginTop: Dp,
    currentLocation: LatLng,
    myAddress: Address,
    context: Context,
    modifier: Modifier = Modifier,
    getCenterLocation: (LatLng) -> Unit = {},
    setDepartureButtonClicked: () -> Unit = {},
    backButtonClicked: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }
    val offsetLat = 0.00009
    val coroutineScope = rememberCoroutineScope()

    // Lifecycle 제어: ON_START 이후에만 지도 초기화
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_START) {
                Timber.d("TMapView - ON_START")
                keyboardController?.hide()
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
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
                tMapView.mapType = TMapView.MapType.NIGHT
                tMapView.setOnMapReadyListener {
                    isMapReady = true

                    tMapView.setOnDisableScrollWithZoomLevelListener { _, _ ->
                        startScrollIdleCheck(
                            scope = coroutineScope,
                            tMapView = tMapView,
                            getCenterLocation = getCenterLocation
                        )
                    }
                }

                val (lat, lon) = if (myAddress.lat == 0.0 && myAddress.lon == 0.0) {
                    currentLocation.latitude - offsetLat to currentLocation.longitude
                } else {
                    myAddress.lat - offsetLat to myAddress.lon
                }

                tMapView.setCenterPoint(lat, lon, true)
                getCenterLocation(LatLng(lat, lon))

                tMapView.zoomLevel = 18

                val markerDrawable =
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_home_current_location
                    )
                val markerBitmap = markerDrawable?.toBitmap()

                val markerItem = TMapMarkerItem().apply {
                    id = "CurrentMarker"
                    name = "Current Location"
                    icon = markerBitmap
                    setTMapPoint(
                        TMapPoint(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )
                    )
                }

                tMapView.addTMapMarkerItem(markerItem)

                tMapView
            }
        )

        if (!isMapReady) {
            AtChaLoadingView(
                transparent = false
            )
        }

        // 뒤로가기
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_white),
            contentDescription = stringResource(R.string.home_search_back_text),
            tint = defaultTeam6Colors.gray300,
            modifier = Modifier
                .offset(x = 16.dp, y = 12.dp + marginTop)
                .noRippleClickable {
                    backButtonClicked()
                }
        )

        // 지도 중심(화면 정중앙)에 핀 끝이 오도록 배치
        // ic_map_marker_setting 높이 44dp → 이미지 중앙 기준 -22dp 오프셋으로 핀 끝 정렬
        Icon(
            tint = Color.Unspecified,
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_map_marker_setting),
            contentDescription = "Start Marker",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-50).dp)
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
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_current_location),
                    contentDescription = stringResource(R.string.home_current_location_btn),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                        .clickable(enabled = isMapReady) {
                            val tMapPoint =
                                TMapPoint(currentLocation.latitude, currentLocation.longitude)
                            tMapView.setCenterPoint(
                                tMapPoint.latitude - offsetLat,
                                tMapPoint.longitude
                            )
                            tMapView.zoomLevel = 18
                            getCenterLocation(LatLng(tMapPoint.latitude, tMapPoint.longitude))
                        }
                        .graphicsLayer { alpha = if (isMapReady) 1f else 0.5f }
                )
            }

            AtChaLocationSettingBottomSheet(
                locationName = myAddress.name,
                locationAddress = myAddress.address,
                completeButtonText = "  출발지로 설정",
                buttonClicked = setDepartureButtonClicked
            )
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

@Preview
@Composable
fun SearchLocationMapViewPreview() {
    SearchLocationMapView(
        currentLocation = LatLng(
//            name = "서울 시청",
            37.5665,
            126.9780
//            address = "서울특별시"
        ),
        myAddress = Address(
            name = "서울 시청",
            lat = 37.5665,
            lon = 126.9780,
            address = "서울특별시"
        ),
        context = LocalContext.current,
        marginTop = 0.dp
    )
}
