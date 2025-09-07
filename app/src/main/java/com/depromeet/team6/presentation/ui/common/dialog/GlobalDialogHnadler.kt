package com.depromeet.team6.presentation.ui.common.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.dialog.DialogController
import com.depromeet.team6.presentation.util.dialog.DialogState
import com.depromeet.team6.presentation.util.dialog.LocalDialogController

@Composable
fun GlobalDialogHandler(
    modifier: Modifier = Modifier,
    controller: DialogController = LocalDialogController.current
) {
    val dialogState by controller.dialogState

    when (val state = dialogState) {
        is DialogState.SystemSettings -> {
            AtchaTwoButtonDialog(
                modifier = modifier,
                message = state.message,
                confirmButtonText = stringResource(R.string.all_dialog_setting_button_text),
                onConfirm = {
                    state.onConfirm()
                    controller.hideDialog()
                },
                onDismiss = {
                    state.onDismiss()
                    controller.hideDialog()
                }
            )
        }

        is DialogState.OneButton -> {
            AtchaOneButtonDialog(
                modifier = modifier,
                message = state.message,
                confirmButtonText = stringResource(R.string.all_dialog_setting_button_text),
                onConfirm = {
                    state.onConfirm()
                    controller.hideDialog()
                }
            )
        }

        is DialogState.TwoButton -> {
            AtchaTwoButtonDialog(
                modifier = modifier,
                message = state.message,
                confirmButtonText = stringResource(R.string.all_dialog_setting_button_text),
                onConfirm = {
                    state.onConfirm()
                    controller.hideDialog()
                },
                onDismiss = {
                    state.onDismiss()
                    controller.hideDialog()
                }
            )
        }

        null -> { /* 다이얼로그 없음 */
        }
    }
}
