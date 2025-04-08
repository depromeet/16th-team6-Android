package com.depromeet.team6.presentation.ui.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.depromeet.team6.data.datalocal.manager.LockServiceManager
import com.depromeet.team6.data.datalocal.permission.PermissionUtil
import com.depromeet.team6.presentation.ui.main.navigation.MainNavHost
import com.depromeet.team6.presentation.ui.main.navigation.MainNavigator
import com.depromeet.team6.presentation.ui.main.navigation.rememberMainNavigator
import com.depromeet.team6.presentation.ui.splash.SplashScreen
import com.depromeet.team6.ui.theme.Team6Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var lockServiceManager: LockServiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()

        if (PermissionUtil.alertPermissionCheck(this)) {
            PermissionUtil.onObtainingPermissionOverlayWindow(this)
        }

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val navigator: MainNavigator = rememberMainNavigator()
            val showSplash by viewModel.showSplash.observeAsState(true)

            SideEffect {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightNavigationBars = false
                }
            }

            Team6Theme {
                LaunchedEffect(Unit) {
                    viewModel.startSplashTimer()
                    viewModel.fetchFcmToken()
                }

                if (showSplash) {
                    SplashScreen()
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        MainNavHost(
                            navigator = navigator,
                            padding = innerPadding
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun <T> ObserveEvents(
        flow: Flow<T>,
        key1: Any? = null,
        key2: Any? = null,
        onEvent: (T) -> Unit
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(lifecycleOwner.lifecycle, key1, key2, flow) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.Main.immediate) {
                    flow.collect(onEvent)
                }
            }
        }
    }
}
