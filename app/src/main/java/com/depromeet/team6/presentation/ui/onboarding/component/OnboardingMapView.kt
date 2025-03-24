package com.depromeet.team6.presentation.ui.onboarding.component

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.ui.common.bottomsheet.AtChaLocationSettingBottomSheet
import com.depromeet.team6.presentation.ui.onboarding.OnboardingContract
import com.depromeet.team6.presentation.ui.onboarding.OnboardingViewModel
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun OnboardingMapView(
    currentLocation: Address,
    modifier: Modifier = Modifier,
    uiState: OnboardingContract.OnboardingUiState = OnboardingContract.OnboardingUiState(),
    viewModel: OnboardingViewModel = hiltViewModel(),
    buttonClicked: (Address) -> Unit = {},
    backButtonClicked: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }
    val offsetLat = 0.00005

    LaunchedEffect(Unit) {
        keyboardController?.hide()
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

    // 현재 위치 변경될 때만 현위치 마커 갱신
    LaunchedEffect(currentLocation, isMapReady) {
        if (isMapReady) {
            val tMapPoint = TMapPoint(currentLocation.lat, currentLocation.lon)

            withContext(Dispatchers.Main) {
                // 중심 좌표를 살짝 아래로 (lat - offset)
                tMapView.setCenterPoint(
                    currentLocation.lat - offsetLat,
                    currentLocation.lon
                )
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

                            viewModel.getCenterLocation(LatLng(tMapPoint.latitude, tMapPoint.longitude))
                        }
                        .graphicsLayer { alpha = if (isMapReady) 1f else 0.5f } // 비활성화 시 투명도 조정
                )
            }

            AtChaLocationSettingBottomSheet(
                locationName = uiState.myAddress.name,
                locationAddress = uiState.myAddress.address,
                completeButtonText = "우리집 등록",
                buttonClicked = { buttonClicked(uiState.myAddress) }
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
