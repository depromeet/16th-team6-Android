package com.depromeet.team6.presentation.ui.mypage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.type.ButtonSize
import com.depromeet.team6.presentation.type.ButtonType
import com.depromeet.team6.presentation.ui.common.button.AtchaCommonButton
import com.depromeet.team6.presentation.ui.common.list.TextListItemRadio
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.presentation.util.dialog.LocalDialogController
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun MyPageWithdrawSelectScreen(
    modifier : Modifier = Modifier,
    moveToAccount : () -> Unit = {},
    withDrawConfirmed : (String) -> Unit = {}
) {
    BackHandler {
        moveToAccount()
    }

    var selectedIdx by remember { mutableIntStateOf(-1) }
    val reasons = listOf(
        R.string.mypage_withdraw_reason_1,
        R.string.mypage_withdraw_reason_2,
        R.string.mypage_withdraw_reason_3,
        R.string.mypage_withdraw_reason_4,
        R.string.mypage_withdraw_reason_5,
        R.string.mypage_withdraw_reason_6,
        R.string.mypage_withdraw_reason_7
    )
    val confirmButtonType by remember {
        derivedStateOf {
            if (selectedIdx >= 0) {
                ButtonType.PRIMARY
            } else {
                ButtonType.DISABLED
            }
        }
    }
    val dialogController = LocalDialogController.current
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = defaultTeam6Colors.gray950
            )
    ) {
        TitleBar(
            title = stringResource(R.string.mypage_withdraw_dialog_confirm),
            onBackClick = moveToAccount
        )
        reasons.forEach { reason ->
            TextListItemRadio(
                modifier = Modifier
                    .noRippleClickable {
                        selectedIdx = reasons.indexOf(reason)
                    },
                text = stringResource(reason),
                isSelected = selectedIdx == reasons.indexOf(reason)
            )
        }

        Spacer(
            modifier = Modifier
                .weight(1f)
        )

        AtchaCommonButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            buttonText = stringResource(R.string.mypage_withdraw_dialog_confirm),
            buttonType = confirmButtonType,
            buttonSize = ButtonSize.LARGE,
            onClick = {
                dialogController.showAtchaTwoButtonAlert(
                    message = context.getString(R.string.mypage_withdraw_dialog_title),
                    confirmButtonText = context.getString(R.string.mypage_withdraw_dialog_confirm),
                    closeButtonText = context.getString(R.string.mypage_dialog_cancle),
                    onConfirm = {
                        withDrawConfirmed( context.getString(reasons[selectedIdx]) )
                    },
                )
            }
        )
    }
}

@Preview
@Composable
fun MyPageWithdrawSelectScreenPreview() {
    MyPageWithdrawSelectScreen()
}