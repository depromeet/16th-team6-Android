package com.depromeet.team6.presentation.ui.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.depromeet.team6.R
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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

//    private fun startLockService() {
//        lockServiceManager.start()
//        Toast.makeText(this, getString(R.string.lock_service_start_text), Toast.LENGTH_SHORT).show()
//    }

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

@Composable
fun LockTestScreen(btnClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.app_name), fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { btnClick() }) {
            Text(text = stringResource(id = R.string.lock_service_start_text))
        }
    }
}
