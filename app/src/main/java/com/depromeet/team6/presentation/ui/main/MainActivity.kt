package com.depromeet.team6.presentation.ui.main

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.common.dialog.GlobalDialogHandler
import com.depromeet.team6.presentation.ui.common.snackbar.GlobalSnackbarHandler
import com.depromeet.team6.presentation.ui.common.snackbar.LocalSnackbarHostState
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator
import com.depromeet.team6.presentation.ui.main.navigation.MainNavHost
import com.depromeet.team6.presentation.ui.main.navigation.MainNavigator
import com.depromeet.team6.presentation.ui.main.navigation.rememberMainNavigator
import com.depromeet.team6.presentation.ui.splash.SplashScreen
import com.depromeet.team6.presentation.util.dialog.DialogController
import com.depromeet.team6.presentation.util.dialog.LocalDialogController
import com.depromeet.team6.presentation.util.snackbar.LocalSnackbarController
import com.depromeet.team6.presentation.util.snackbar.rememberSnackbarController
import com.depromeet.team6.presentation.util.view.NetworkState
import com.depromeet.team6.ui.theme.Team6Theme
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var backPressedTime = 0L

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - backPressedTime < 2000) {
                        finishAffinity()
                    } else {
                        backPressedTime = currentTime
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.all_toast_exit_app),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )

        firebaseAnalytics = Firebase.analytics

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()

        val navigateToCourseSearch = intent.getBooleanExtra(LockScreenNavigator.EXTRA_NAVIGATE_TO_COURSE_SEARCH, false)
        val departurePoint = intent.getStringExtra(LockScreenNavigator.EXTRA_DEPARTURE_POINT) ?: ""
        val destinationPoint = intent.getStringExtra(LockScreenNavigator.EXTRA_DESTINATION_POINT) ?: ""
        val fromLockScreen = intent.getBooleanExtra(LockScreenNavigator.EXTRA_FROM_LOCK_SCREEN, false)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val navigator: MainNavigator = rememberMainNavigator(firebaseAnalytics = firebaseAnalytics)
            val showSplash by viewModel.showSplash.observeAsState(true)
            val dialogController = remember { DialogController() }
            val networkAvailability = viewModel.networkAvailability.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }
            val (snackbarController, snackbarData) = rememberSnackbarController()

            var shouldNavigateToCourseSearch by remember { mutableStateOf(navigateToCourseSearch) }

            SideEffect {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightNavigationBars = false
                }
            }
            Team6Theme {
                CompositionLocalProvider(
                    LocalDialogController provides dialogController,
                    LocalSnackbarHostState provides snackbarHostState,
                    LocalSnackbarController provides snackbarController
                ) {
                    if (showSplash) {
                        SplashScreen()
                    } else {
                        if (networkAvailability.value == NetworkState.Unavailable) {
                            LocalDialogController.current.showAtchaOfflineAlert(
                                onConfirm = {
                                    if (networkAvailability.value == NetworkState.Unavailable) {
                                        Toast.makeText(this, "인터넷 연결을 다시 확인해주세요", Toast.LENGTH_SHORT).show()
                                    } else {
                                        dialogController.hideDialog()
                                        navigator.popBackStack()
                                    }
                                }
                            )
                        }
                        Box {
                            Scaffold(
                                snackbarHost = {
                                    SnackbarHost(hostState = snackbarHostState)
                                },
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
                            GlobalDialogHandler(
                                controller = dialogController,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )

                            GlobalSnackbarHandler(
                                snackbarData = snackbarData.value,
                                onDismiss = { snackbarData.value = null }
                            )
                        }
                    }
                }
            }
        }
    }
}
