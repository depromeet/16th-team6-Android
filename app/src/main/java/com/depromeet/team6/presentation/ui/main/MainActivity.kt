package com.depromeet.team6.presentation.ui.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.depromeet.team6.data.datalocal.manager.LockServiceManager
import com.depromeet.team6.data.datalocal.permission.PermissionUtil
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator
import com.depromeet.team6.presentation.ui.main.navigation.MainNavHost
import com.depromeet.team6.presentation.ui.main.navigation.MainNavigator
import com.depromeet.team6.presentation.ui.main.navigation.rememberMainNavigator
import com.depromeet.team6.presentation.ui.splash.SplashScreen
import com.depromeet.team6.ui.theme.Team6Theme
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var lockServiceManager: LockServiceManager
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()

        if (PermissionUtil.alertPermissionCheck(this)) {
            PermissionUtil.onObtainingPermissionOverlayWindow(this)
        }

        // LockActivity에서 전달된 데이터 확인 및 유효성 검사
        val navigateToCourseSearch = intent.getBooleanExtra(LockScreenNavigator.EXTRA_NAVIGATE_TO_COURSE_SEARCH, false)
        val departurePoint = intent.getStringExtra(LockScreenNavigator.EXTRA_DEPARTURE_POINT) ?: ""
        val destinationPoint = intent.getStringExtra(LockScreenNavigator.EXTRA_DESTINATION_POINT) ?: ""
        val fromLockScreen = intent.getBooleanExtra(LockScreenNavigator.EXTRA_FROM_LOCK_SCREEN, false)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val navigator: MainNavigator = rememberMainNavigator(firebaseAnalytics = firebaseAnalytics)
            val showSplash by viewModel.showSplash.observeAsState(true)

            var shouldNavigateToCourseSearch by remember { mutableStateOf(navigateToCourseSearch) }

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

                        if (shouldNavigateToCourseSearch) {
                            LaunchedEffect(Unit) {
                                navigator.navigateToCourseSearch(
                                    departure = departurePoint,
                                    destination = destinationPoint,
                                    fromLockScreen = fromLockScreen
                                )
                                shouldNavigateToCourseSearch = false
                            }
                        }
                    }
                }
            }
        }
    }
}
