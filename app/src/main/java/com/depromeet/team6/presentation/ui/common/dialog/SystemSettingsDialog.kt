package com.depromeet.team6.presentation.ui.common.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.Team6Theme

@Composable
fun SystemSettingsDialog(
    modifier: Modifier = Modifier,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = modifier.roundedBackgroundWithPadding(
                backgroundColor = Team6Theme.colors.gray940,
                cornerRadius = 20.dp,
                padding = PaddingValues(top = 32.dp, bottom = 28.dp, start = 24.dp, end = 24.dp)
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = Team6Theme.typography.heading3_H3SB17,
                color = Team6Theme.colors.white,
                textAlign = TextAlign.Center

            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "닫기",
                    textAlign = TextAlign.Center,
                    style = Team6Theme.typography.body5_B5SB14,
                    color = Team6Theme.colors.white,
                    modifier = Modifier
                        .weight(1f)
                        .roundedBackgroundWithPadding(
                            backgroundColor = Team6Theme.colors.gray910,
                            cornerRadius = 8.dp,
                            padding = PaddingValues(vertical = 13.dp)
                        )
                        .noRippleClickable(onDismiss)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "설정하기",
                    textAlign = TextAlign.Center,
                    style = Team6Theme.typography.body5_B5SB14,
                    color = Team6Theme.colors.black,
                    modifier = Modifier
                        .weight(1f)
                        .roundedBackgroundWithPadding(
                            backgroundColor = Team6Theme.colors.main,
                            cornerRadius = 8.dp,
                            padding = PaddingValues(vertical = 13.dp)
                        )
                        .noRippleClickable(onConfirm)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SystemSettingsDialogPreview() {
    SystemSettingsDialog(
        message = "현위치를 찾을 수 없어요.\n" +
            "위치 권한을 허용해 주세요.",
        onConfirm = {},
        onDismiss = {}
    )
}
