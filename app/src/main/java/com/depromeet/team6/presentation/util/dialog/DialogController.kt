package com.depromeet.team6.presentation.util.dialog

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf

@Stable
class DialogController {
    private val _dialogState = mutableStateOf<DialogState?>(null)
    val dialogState: State<DialogState?> = _dialogState

    fun showSystemSettingsDialog(
        message: String = "현위치를 찾을 수 없어요.\n" +
            "위치 권한을 허용해 주세요.",
        onConfirm: () -> Unit,
        onDismiss: () -> Unit = {}
    ) {
        _dialogState.value = DialogState.SystemSettings(
            message = message,
            onConfirm = onConfirm,
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
