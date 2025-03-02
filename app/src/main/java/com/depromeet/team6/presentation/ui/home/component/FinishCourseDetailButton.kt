package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun FinishCourseDetailButton(
    onFinishClick: () -> Unit,
    onCourseDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = onFinishClick,
            modifier = Modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.greyDefaultButton,
                contentColor = colors.white
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "종료",
                style = typography.bodyMedium17,
                modifier = modifier
                    .padding(horizontal = 5.dp)
            )
        }

        PrimaryButton(
            text = "경로 상세",
            onClick = onCourseDetailClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun FinishCourseDetailButtonPreview() {
    FinishCourseDetailButton(
        onFinishClick = { },
        onCourseDetailClick = { },
        modifier = Modifier
    )
}