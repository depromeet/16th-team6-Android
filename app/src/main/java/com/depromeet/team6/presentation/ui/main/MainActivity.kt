package com.depromeet.team6.presentation.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.data.background.ArrivalMonitorService
import com.depromeet.team6.presentation.ui.common.dialog.GlobalDialogHandler
import com.depromeet.team6.presentation.ui.common.snackbar.GlobalSnackbarHandler
import com.depromeet.team6.presentation.ui.common.snackbar.LocalSnackbarHostState
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator
import com.depromeet.team6.presentation.ui.main.navigation.MainNavHost
import com.depromeet.team6.presentation.ui.main.navigation.MainNavigator
import com.depromeet.team6.presentation.ui.main.navigation.rememberMainNavigator
import com.depromeet.team6.presentation.util.context.openAppSettings
import com.depromeet.team6.presentation.util.dialog.DialogController
import com.depromeet.team6.presentation.util.dialog.LocalDialogController
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.snackbar.LocalSnackbarController
import com.depromeet.team6.presentation.util.snackbar.rememberSnackbarController
import com.depromeet.team6.presentation.util.view.NetworkState
import com.depromeet.team6.ui.theme.Team6Theme
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

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

        val permissionGranted = PermissionUtil.hasLocationPermissions(this)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val navigator: MainNavigator = rememberMainNavigator(firebaseAnalytics = firebaseAnalytics)
            val dialogController = remember { DialogController() }
            val networkAvailability = viewModel.networkAvailability.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }
            val (snackbarController, snackbarData) = rememberSnackbarController()
            val lifecycleOwner = LocalLifecycleOwner.current
            var shouldNavigateToCourseSearch by remember { mutableStateOf(navigateToCourseSearch) }

            SideEffect {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightNavigationBars = false
                }
            }

            val locationPermissionsLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissions ->
                    if (permissions.values.all { it }) {
                        snackbarController.showSnackbar(
                            "위치 권한이 허용되었습니다."
                        )
                    } else {
                        Toast.makeText(this@MainActivity, "앗차 앱을 사용하시기 전에 위치 권한을 허용해 주세요", Toast.LENGTH_SHORT).show()
                        exitProcess(0)
                    }
                }
            )

            LaunchedEffect(PermissionUtil.hasLocationPermissions(this)) {
                if (PermissionUtil.hasLocationPermissions(this@MainActivity)) { // 위치 권한이 있으면
                    viewModel.startLocationUpdates()
                } else {
                    dialogController.showAtchaTwoButtonAlert(
                        message = this@MainActivity.getString(R.string.all_dialog_location_permission),
                        onConfirm = {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this@MainActivity,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            ) {
                                this@MainActivity.openAppSettings()
                            } else {
                                PermissionUtil.requestLocationPermissions(
                                    this@MainActivity,
                                    locationPermissionsLauncher
                                )
                            }
                        },
                        onDismiss = {
                            Toast.makeText(this@MainActivity, "앗차 앱을 사용하시기 전에 위치 권한을 허용해 주세요", Toast.LENGTH_SHORT)
                                .show()
                            exitProcess(0)
                        },
                        closeButtonText = "닫기",
                        confirmButtonText = "확인"
                    )
                }
            }

            if (networkAvailability.value == NetworkState.Unavailable) {
                dialogController.showAtchaOfflineAlert {
                    if (networkAvailability.value == NetworkState.Available) {
                        dialogController.hideDialog()
                    }
                }
            }
            Team6Theme {
                CompositionLocalProvider(
                    LocalDialogController provides dialogController,
                    LocalSnackbarHostState provides snackbarHostState,
                    LocalSnackbarController provides snackbarController
                ) {
                    val context = LocalContext.current

                    // Arrival 이벤트 처리용 BroadcastReceiver
                    val arrivalReceiver = remember {
                        object : BroadcastReceiver() {
                            override fun onReceive(context: Context?, intent: Intent?) {
                                dialogController.showAtchaOneButtonAlert(
                                    message = getString(R.string.arrival_guide_finish_dialog),
                                    onConfirm = { },
                                    confirmButtonText = getString(R.string.confirm_dialog)
                                )
                            }
                        }
                    }

                    // Lifecycle-aware 등록/해제
                    DisposableEffect(Unit) {
                        val filter = IntentFilter(ArrivalMonitorService.ACTION_ARRIVAL)
                        ContextCompat.registerReceiver(
                            context,
                            arrivalReceiver,
                            filter,
                            ContextCompat.RECEIVER_NOT_EXPORTED
                        )
                        onDispose {
                            context.unregisterReceiver(arrivalReceiver)
                        }
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
