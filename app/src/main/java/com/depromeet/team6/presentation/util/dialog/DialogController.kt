package com.depromeet.team6.presentation.util.dialog

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.depromeet.team6.presentation.util.context.openAppSettings

@Stable
class DialogController {
    private val _dialogState = mutableStateOf<DialogState?>(null)
    val dialogState: State<DialogState?> = _dialogState

    fun showAtchaCommonAlert(
        context: Context,
        message: String,
        onConfirm: () -> Unit = {},
        onDismiss: () -> Unit = {}
    ) {
        _dialogState.value = DialogState.SystemSettings(
            message = message,
            onConfirm = {
                onConfirm()
                context.openAppSettings()
            },
            onDismiss = onDismiss
        )
    }

    fun hideDialog() {
        _dialogState.value = null
    }
}

sealed class DialogState {
    data class SystemSettings(
        val message: String,
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit
    ) : DialogState()
}

val LocalDialogController = staticCompositionLocalOf<DialogController> {
    error("DialogController not provided")
}
