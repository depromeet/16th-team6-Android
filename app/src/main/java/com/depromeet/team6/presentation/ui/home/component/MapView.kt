package com.depromeet.team6.presentation.ui.home.component

import android.util.Log
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.google.android.gms.maps.model.LatLng
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TMapViewCompose(
    currentLocation: LatLng,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tMapView = remember { TMapView(context) }
    var isMapReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        tMapView.setOnMapReadyListener {
            tMapView.mapType = TMapView.MapType.NIGHT
            isMapReady = true
        }
    }

    // 현재 위치 변경될 때만 마커 갱신
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
            update = { frameLayout ->
                // Update logic if needed (e.g., map settings)
            }
        )

        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_home_start_marker),
            contentDescription = "Start Marker",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 105.dp)
        )

        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_current_location),
            contentDescription = stringResource(R.string.home_current_location_btn),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 236.dp, end = 16.dp)
                .clickable {
                    val tMapPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)
                    tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude)
                }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("TMapViewCompose", "destroy!")
            tMapView.onDestroy()
        }
    }
}
