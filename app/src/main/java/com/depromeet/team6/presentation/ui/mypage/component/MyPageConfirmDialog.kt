package com.depromeet.team6.presentation.ui.mypage.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun MyPageConfirmDialog(
    title: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.greyElevatedBackground
        )
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = colors.white,
                style = typography.heading5Bold17,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp, bottom = 20.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 28.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.greyDefaultButton
                    )
                ) {
                    Text(
                        text = stringResource(R.string.mypage_dialog_cancle),
                        color = colors.white,
                        style = typography.bodyMedium14,
                        modifier = Modifier.padding(vertical = 13.dp)
                    )
                }

                Button(
                    onClick = onSuccess,
                    modifier = Modifier
                        .padding(bottom = 28.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.white
                    )
                ) {
                    Text(
                        text = confirmText,
                        color = colors.black,
                        style = typography.bodyMedium14,
                        modifier = Modifier.padding(vertical = 13.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MyPageConfirmDialogPreview() {
    MyPageConfirmDialog(
        title = stringResource(R.string.mypage_logout_dialog_title),
        confirmText = stringResource(R.string.mypage_logout_dialog_confirm),
        onDismiss = {},
        onSuccess = {},
        modifier = Modifier
    )
}
