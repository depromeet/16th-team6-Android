package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

enum class AlarmTime(val minutes: Int) {
    ONE_MIN(1),
    FIVE_MIN(5),
    TEN_MIN(10),
    FIFTEEN_MIN(15),
    THIRTY_MIN(30),
    ONE_HOUR(60);

    override fun toString(): String {
        return if (minutes == 60) "1시간 전" else "${minutes}분 전"
    }
}

@Composable
fun OnboardingAlarmSelectorItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp)
    ) {
        Text(
            text = text,
            style = defaultTeam6Typography.bodyRegular15,
            color = defaultTeam6Colors.white
        )
        Spacer(modifier = Modifier.weight(1f))
        if (text == "1분 전") {
            Text(
                text = "1분 전 푸시 알림은 무조건 드려요",
                color = defaultTeam6Colors.greyTertiaryLabel,
                style = defaultTeam6Typography.bodyRegular14
            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_select_alarm),
                tint = if (isSelected) defaultTeam6Colors.main else defaultTeam6Colors.greyDisabled,
                contentDescription = null
            )
        }
    }
}

@Composable
fun OnboardingAlarmSelector(
    modifier: Modifier = Modifier,
    selectedItems: Set<AlarmTime> = emptySet(),
    onItemClick: (AlarmTime) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AlarmTime.entries.forEach { alarmTime ->
            OnboardingAlarmSelectorItem(
                text = alarmTime.toString(),
                isSelected = alarmTime in selectedItems,
                onClick = { onItemClick(alarmTime) }
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingAlarmSelectorPreview() {
    OnboardingAlarmSelector(
        onItemClick = { }
    )
}
