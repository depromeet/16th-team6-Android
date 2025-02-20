package com.depromeet.team6.presentation.ui.home.component;

import android.graphics.Color
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.FragmentManager;
import androidx.compose.ui.viewinterop.AndroidView
import com.depromeet.team6.BuildConfig

import com.depromeet.team6.R;
import com.skt.tmap.TMapView

//@Composable
//fun TMapView() {
//    val activity = LocalActivity.current as AppCompatActivity
//    val fragmentManager = activity.supportFragmentManager
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val fragment = remember { ExampleFragment() } // Fragment 인스턴스 기억
//
//
//    AndroidView(
//        modifier = Modifier.fillMaxSize(),
//        factory = { context ->
//            FrameLayout(context).apply {
//                id = R.id.tmapViewContainer
//                fragmentManager.beginTransaction()
//                        .replace(R.id.tmapViewContainer, ExampleFragment()) // XML 기반 Fragment
//                        .commitNow()
//            }
//        }
//    )
//}


@Composable
fun TMapViewCompose() {
    val context = LocalContext.current
    val tMapView = remember { TMapView(context) }

    LaunchedEffect(Unit) {
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        tMapView.setOnMapReadyListener {
            Log.d("TMapViewCompose", "TMap is ready!")
            // TODO: Implement your logic after the map is ready
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
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
