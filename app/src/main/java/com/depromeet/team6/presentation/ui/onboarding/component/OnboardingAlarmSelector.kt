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

enum class AlarmTime(val string: String) {
    ONE_MIN("1분 전"),
    FIVE_MIN("5분 전"),
    TEN_MIN("10분 전"),
    FIFTEEN_MIN("15분 전"),
    THIRTY_MIN("30분 전"),
    ONE_HOUR("1시간 전")
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
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_onboarding_select_alarm),
            tint = if (isSelected) defaultTeam6Colors.main else defaultTeam6Colors.disabledLabel,
            contentDescription = null
        )
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
                text = alarmTime.string,
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
