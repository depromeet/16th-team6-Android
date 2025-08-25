package com.depromeet.team6.presentation.ui.common.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.util.snackbar.CustomSnackbarData
import kotlinx.coroutines.delay

@Composable
fun GlobalSnackbarHandler(
    snackbarData: CustomSnackbarData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSnackbarVisible = remember { mutableStateOf(false) }

    LaunchedEffect(snackbarData) {
        if (snackbarData != null) {
            isSnackbarVisible.value = true
            delay(2000L)
            isSnackbarVisible.value = false
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = isSnackbarVisible.value,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 300),
                initialOffsetY = { -it }
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 300),
                targetOffsetY = { -it }
            )
        ) {
            AtchaCommonSnackBar(
                modifier = Modifier.padding(top = 12.dp),
                text = snackbarData?.message.orEmpty(),
                buttonText = snackbarData?.actionLabel,
                onClick = {
                    isSnackbarVisible.value = false
                    onDismiss()
                    snackbarData?.onAction?.invoke()
                }
            )
        }
    }
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}
