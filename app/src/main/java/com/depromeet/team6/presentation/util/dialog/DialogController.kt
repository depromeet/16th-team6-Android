package com.depromeet.team6.presentation.util.dialog

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getString
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.context.openAppSettings

@Stable
class DialogController {
    private val _dialogState = mutableStateOf<DialogState?>(null)
    val dialogState: State<DialogState?> = _dialogState

    fun showAtchaSystemSettingAlert(
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

    fun showAtchaTwoButtonAlert(
        message: String,
        onConfirm: () -> Unit = {},
        onDismiss: () -> Unit = {},
        closeButtonText:String?,
        confirmButtonText:String,
    ) {
        _dialogState.value = DialogState.TwoButton(
            message = message,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            closeButtonText = closeButtonText,
            confirmButtonText = confirmButtonText
        )
    }

    fun showAtchaOneButtonAlert(
        message: String,
        onConfirm: () -> Unit = {},
        confirmButtonText:String

        ) {
        _dialogState.value = DialogState.OneButton(
            message = message,
            onConfirm = onConfirm,
            confirmButtonText = confirmButtonText
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

    data class TwoButton(
        val message: String,
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit,
        val closeButtonText:String?,
        val confirmButtonText:String,
        ) : DialogState()

    data class OneButton(
        val message: String,
        val onConfirm: () -> Unit,
        val confirmButtonText:String,
        ) : DialogState()
}

val LocalDialogController = staticCompositionLocalOf<DialogController> {
    error("DialogController not provided")
}
