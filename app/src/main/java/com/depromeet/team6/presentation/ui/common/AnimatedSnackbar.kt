package com.depromeet.team6.presentation.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.util.view.SnackbarController
import com.depromeet.team6.presentation.util.view.SnackbarEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO : 나중에 다시 브랜치 파서 완성
@Composable
fun AtchaSnackBarHost(
    hostState: SnackbarHostState,
) {
    var isVisible by remember { mutableStateOf(false) }
    val currentSnackbarData = hostState.currentSnackbarData
    val accessibilityManager = LocalAccessibilityManager.current

    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            val duration = 2000L
            delay(duration)
            currentSnackbarData.dismiss() // 스낵바 상태 초기화
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter // 화면 상단에 위치
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically { -it } + fadeIn(), // 위에서 내려오는 애니메이션
            exit = slideOutVertically { -it } + fadeOut() // 위로 올라가는 애니메이션
        ) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    hostState.currentSnackbarData?.visuals?.actionLabel?.let { actionLabel ->
                        TextButton(onClick = { hostState.currentSnackbarData?.performAction() }) {
                            Text(actionLabel)
                        }
                    }
                }
            ) {
                Text(text = hostState.currentSnackbarData?.visuals?.message ?: "")
            }
        }
    }
}

@Preview
@Composable
fun ScaffoldWithCustomSnackbar() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            AtchaSnackBarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                // 스낵바를 보여주는 로직
                coroutineScope.launch {
                    SnackbarController.sendEvent(SnackbarEvent(message = "뽀잉 내려왔다가 올라가는 애니메이션!"))
                }
            }) {
                Text("Show Custom Snackbar")
            }
        }
    }
}