package com.depromeet.team6.presentation.ui.common.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.dialog.DialogController
import com.depromeet.team6.presentation.util.dialog.DialogState
import com.depromeet.team6.presentation.util.dialog.LocalDialogController
import com.depromeet.team6.presentation.util.modifier.noRippleClickable

@Composable
fun GlobalDialogHandler(
    modifier: Modifier = Modifier,
    controller: DialogController = LocalDialogController.current
) {
    val dialogState by controller.dialogState

    var lastBottomSheetState by remember { mutableStateOf<DialogState.BottomSheet?>(null) }
    val isBottomSheetVisible = dialogState is DialogState.BottomSheet
    if (isBottomSheetVisible) {
        lastBottomSheetState = dialogState as DialogState.BottomSheet
    }

    // Dialog 기반 컴포넌트 (시스템 Dialog 윈도우로 그려짐 — 자체 스크림 보유)
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
                confirmButtonText = state.confirmButtonText,
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
                confirmButtonText = state.confirmButtonText,
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

        is DialogState.OffLine -> {
            AtchaOfflineDialog(
                modifier = modifier,
                onConfirm = {
                    state.onConfirm()
                }
            )
        }

        is DialogState.BottomSheet, null -> { /* 아래 AnimatedVisibility로 처리 */ }
    }

    // 스크림 + 바텀 시트 (메인 윈도우에 그려짐)
    Box(modifier = Modifier.fillMaxSize()) {
        // 스크림 — 다이얼로그가 하나라도 떠 있을 때 페이드 인
        AnimatedVisibility(
            visible = dialogState != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .noRippleClickable { /* 터치 이벤트 소비 — 뒤로 전달 차단 */ }
            )
        }

        // 바텀 시트 — 아래에서 위로 슬라이드
        AnimatedVisibility(
            visible = isBottomSheetVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            lastBottomSheetState?.let { state ->
                AtchaBottomSheetDialog(
                    locationName = state.locationName,
                    locationAddress = state.locationAddress,
                    completeButtonText = state.confirmButtonText,
                    buttonClicked = {
                        state.onConfirm()
                        controller.hideDialog()
                    }
                )
            }
        }
    }
}
