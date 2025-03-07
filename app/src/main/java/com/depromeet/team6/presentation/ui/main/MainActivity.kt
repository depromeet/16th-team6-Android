package com.depromeet.team6.presentation.ui.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.depromeet.team6.presentation.ui.main.navigation.MainNavHost
import com.depromeet.team6.presentation.ui.main.navigation.MainNavigator
import com.depromeet.team6.presentation.ui.main.navigation.rememberMainNavigator
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.R
import com.depromeet.team6.data.datalocal.manager.LockServiceManager
import com.depromeet.team6.data.datalocal.permission.PermissionUtil
import com.depromeet.team6.presentation.ui.login.LoginRoute
import com.depromeet.team6.ui.theme.Team6Theme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var lockServiceManager: LockServiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        if (PermissionUtil.alertPermissionCheck(this)) {
            PermissionUtil.onObtainingPermissionOverlayWindow(this)
        }

        setContent {
            val navigator: MainNavigator = rememberMainNavigator()
            Team6Theme {
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginRoute(
                        modifier = Modifier.padding(innerPadding),
                        navigateToOnboarding = {},
                        navigateToHome = {}
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { padding ->
                        MainNavHost(
                            navigator = navigator,
                            padding = padding
                        )
                    }
                )
            }
        }
    }
}
