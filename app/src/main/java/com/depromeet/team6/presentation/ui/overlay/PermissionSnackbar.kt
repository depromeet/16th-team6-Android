package com.depromeet.team6.presentation.ui.overlay

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography
import kotlinx.coroutines.delay

@Composable
fun PermissionSnackbar(
    onSettingsClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    autoDismissDelay: Long = 3500L
) {
    var isVisible by remember { mutableStateOf(false) }
    var shouldDismiss by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = when {
            shouldDismiss -> 0f
            isVisible -> 1f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            if (shouldDismiss && it == 0f) {
                onDismiss()
            }
        }
    )

    LaunchedEffect(Unit) {
        isVisible = true // 나타나기 시작
        delay(autoDismissDelay - 300) // 표시 시간
        shouldDismiss = true // 사라지기 시작
    }

    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    Card(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp)
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.gray920
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    R.string.overlay_permission_toast_message
                ),
                color = colors.white,
                style = typography.bodyRegular15
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.overlay_permission_go_setting_text),
                color = colors.systemGreen,
                style = typography.bodyRegular15,
                modifier = Modifier
                    .noRippleClickable { onSettingsClick() }
            )
        }
    }
}
