package com.depromeet.team6.presentation.ui.home.component

import android.util.Log
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.depromeet.team6.BuildConfig
import com.skt.tmap.TMapView

@Composable
fun TMapViewCompose(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tMapView = remember { TMapView(context) }

    LaunchedEffect(Unit) {
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        tMapView.setOnMapReadyListener {
            tMapView.mapType = TMapView.MapType.NIGHT
        }
    }

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

    DisposableEffect(Unit) {
        onDispose {
            Log.d("TMapViewCompose", "destroy!")
            tMapView.onDestroy()
        }
    }
}
