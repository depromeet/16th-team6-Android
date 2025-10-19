package com.depromeet.team6.presentation.ui.common.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.Team6Theme

@Composable
fun AtchaOfflineDialog(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
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
                text = stringResource(R.string.alert_offline_one_button_body),
                style = Team6Theme.typography.heading3_H3SB17,
                color = Team6Theme.colors.white,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.alert_offline_one_button),
                textAlign = TextAlign.Center,
                style = Team6Theme.typography.body5_B5SB14,
                color = Team6Theme.colors.black,
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable(onConfirm)
                    .roundedBackgroundWithPadding(
                        backgroundColor = Team6Theme.colors.main,
                        cornerRadius = 8.dp,
                        padding = PaddingValues(vertical = 13.dp)
                    )
            )
        }
    }
}

@Preview
@Composable
private fun AtchaOneButtonDialogPreview() {
    AtchaOfflineDialog(
        onConfirm = {}
    )
}
