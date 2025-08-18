package com.depromeet.team6.presentation.util.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State


val LocalSnackbarController = staticCompositionLocalOf<SnackbarController> {
    error("SnackbarController not provided")
}


class SnackbarController(
    private val scope: CoroutineScope,
    private val onShow: (CustomSnackbarData) -> Unit
) {
    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            onShow(
                CustomSnackbarData(
                    message = message,
                    actionLabel = actionLabel,
                    onAction = onAction
                )
            )
        }
    }
}

@Composable
fun rememberSnackbarController(): Pair<SnackbarController, MutableState<CustomSnackbarData?>> {
    val coroutineScope = rememberCoroutineScope()
    val snackbarData = remember { mutableStateOf<CustomSnackbarData?>(null) }

    val controller = remember {
        SnackbarController(
            scope = coroutineScope,
            onShow = { data -> snackbarData.value = data }
        )
    }

    return controller to snackbarData
}

data class CustomSnackbarData(
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)
