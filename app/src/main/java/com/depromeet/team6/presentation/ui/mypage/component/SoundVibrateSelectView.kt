package com.depromeet.team6.presentation.ui.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.mypage.MypageContract
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun SoundVibrateSelectView(
    type: MypageContract.AlarmType,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = LocalTeam6Typography.current
    val colors = LocalTeam6Colors.current

    val (title, iconRes) = when (type) {
        MypageContract.AlarmType.VIBRATION -> Pair(
            stringResource(R.string.mypage_alarm_vibration_text),
            R.drawable.ic_mypage_vibrate
        )
        MypageContract.AlarmType.SOUND -> Pair(
            stringResource(R.string.mypage_alarm_sound_text),
            R.drawable.ic_mypage_sound
        )
    }

    val color = if (isSelected) colors.systemGreen else colors.white
    val radioColor = if (isSelected) colors.systemGreen else colors.greyQuaternaryLabel
    val radioIcon = if (isSelected) R.drawable.ic_mypage_radio_selected else R.drawable.ic_mypage_radio_unselected

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                colors.greyElevatedBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .noRippleClickable(onSelected)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 14.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconRes),
                contentDescription = title,
                tint = color,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )

            Text(
                text = title,
                style = typography.bodyMedium15,
                color = color
            )

            Icon(
                imageVector = ImageVector.vectorResource(id = radioIcon),
                contentDescription = title,
                tint = radioColor,
                modifier = Modifier
                    .padding(vertical = 18.dp)
            )
        }
    }
}

@Preview
@Composable
fun SoundVibrateSelectViewPreview() {
    SoundVibrateSelectView(
        type = MypageContract.AlarmType.SOUND,
        isSelected = true,
        onSelected = {}
    )
}
