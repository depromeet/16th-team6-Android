package com.depromeet.team6.presentation.ui.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.data.datalocal.manager.LockServiceManager
import com.depromeet.team6.data.datalocal.permission.PermissionUtil
import com.depromeet.team6.presentation.ui.course.CourseScreen
import com.depromeet.team6.presentation.ui.itinerary.ItineraryScreen
import com.depromeet.team6.presentation.util.view.SnackbarController
import com.depromeet.team6.ui.theme.Team6Theme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var lockServiceManager: LockServiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PermissionUtil.alertPermissionCheck(this)) {
            PermissionUtil.onObtainingPermissionOverlayWindow(this)
        }

        setContent {
            Team6Theme {
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            hostState = snackbarHostState)
                    }
                ) { innerPadding ->
                    // 전역적으로 사용할 SnackbarController 에 대한 이벤트 수신
                    ObserveEvents(
                        flow = SnackbarController.events,
                        key1 = snackbarHostState
                    ) { event ->
                        coroutineScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()

                            val result = snackbarHostState.showSnackbar(
                                message = event.message,
                                actionLabel = event.action?.name,
                                duration = SnackbarDuration.Short
                            )

                            if(result == SnackbarResult.ActionPerformed) {
                                event.action?.action?.invoke()
                            }
                        }
                    }

//                    LoginRoute(
//                        modifier = Modifier.padding(innerPadding),
//                        navigateToOnboarding = {},
//                        navigateToHome = {}
//                    )
                    ItineraryScreen(
                        emptyList()
                    )
                }
            }

            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.d(TAG, "Fetching FCM registration token failed")
                        return@OnCompleteListener
                    }

                    val token = task.result

                    Log.d("Fcm Token", token)
                }
            )
        }
    }
    private fun startLockService() {
        lockServiceManager.start()
        Toast.makeText(this, getString(R.string.lock_service_start_text), Toast.LENGTH_SHORT).show()
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
