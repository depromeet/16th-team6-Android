package com.depromeet.team6.presentation.ui.onboarding.component

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.ui.common.bottomsheet.AtChaLocationSettingBottomSheet
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val lifecycleOwner = LocalLifecycleOwner.current

    var isMapReady by remember { mutableStateOf(false) }
    val offsetLat = 0.00005
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        backButtonClicked()
    }

    // Lifecycle 제어: ON_START 이후에만 지도 초기화
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_START) {
                keyboardController?.hide()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            Timber.d("TMapViewCompose destroy!")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                val tMapView = TMapView(context)

                tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
                tMapView.setOnMapReadyListener {
                    tMapView.mapType = TMapView.MapType.NIGHT

                    val lat = currentLocation.lat - offsetLat
                    val lon = currentLocation.lon

                    tMapView.fitBounds(
                        tMapView.getBoundsFromPoints(arrayListOf(TMapPoint(lat, lon)))
                    )
                    tMapView.zoomLevel = 18

                    tMapView.setOnDisableScrollWithZoomLevelListener { _, _ ->
                        startScrollIdleCheck(
                            scope = coroutineScope,
                            tMapView = tMapView,
                            getCenterLocation = getCenterLocation
                        )
                    }
                    isMapReady = true
                }

                tMapView
            }
        )

        if (!isMapReady) {
            AtChaLoadingView(
                transparent = false
            )
        }

        // 뒤로가기 아이콘

        CircleBtnBack(
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 12.dp)
                .noRippleClickable {
                    backButtonClicked()
                }
        )

        // 하단 UI
        Column(modifier = modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth().weight(1f)) {
                Icon(
                    tint = Color.Unspecified,
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_map_marker_setting),
                    contentDescription = "Start Marker",
                    modifier = Modifier.align(Alignment.Center)
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

@Composable
private fun CircleBtnBack(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .roundedBackgroundWithPadding(
                cornerRadius = 100.dp,
                backgroundColor = defaultTeam6Colors.gray940
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_grey),
            colorFilter = ColorFilter.tint(defaultTeam6Colors.white),
            contentDescription = "OnBoardingMapCircleBtnBack"
        )
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
