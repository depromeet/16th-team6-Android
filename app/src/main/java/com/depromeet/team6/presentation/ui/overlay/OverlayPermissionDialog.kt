package com.depromeet.team6.presentation.ui.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun OverlayPermissionDialog(
    onDismiss: () -> Unit,
    onSettingClicked: () -> Unit,
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
            containerColor = colors.gray940
        )
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.overlay_permission_content_text_start).replace("\\n", "\n"))
                    withStyle(style = SpanStyle(color = colors.systemGreen)) {
                        append(stringResource(R.string.overlay_permission_content_text_mid))
                    }
                    append(stringResource(R.string.overlay_permission_content_text_end))
                },
                color = colors.white,
                style = typography.heading3_H3SB17,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.gray950
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(vertical = 12.dp, horizontal = 12.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_logo_with_grey_backgroud),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )

                    Text(
                        text = stringResource(R.string.overlay_permission_atcha_text),
                        color = colors.white,
                        style = typography.body2_B2SB15,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 7.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        modifier = Modifier
                            .padding(end = 12.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_permission_toggle_on),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 28.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.gray910
                    )
                ) {
                    Text(
                        text = stringResource(R.string.overlay_permission_close_text),
                        color = colors.white,
                        style = typography.bodyMedium14,
                        modifier = Modifier.padding(vertical = 13.dp)
                    )
                }

                Button(
                    onClick = onSettingClicked,
                    modifier = Modifier
                        .padding(bottom = 28.dp)
                        .weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.systemGreen
                    )
                ) {
                    Text(
                        text = stringResource(R.string.overlay_permission_go_setting_text),
                        color = colors.black,
                        style = typography.body5_B5SB14,
                        modifier = Modifier.padding(vertical = 13.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun OverlayPermissionDialogPreview() {
    OverlayPermissionDialog(
        onDismiss = {},
        onSettingClicked = {},
        modifier = Modifier
    )
}
