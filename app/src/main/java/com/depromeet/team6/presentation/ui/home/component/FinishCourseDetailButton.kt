package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
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
        OutlinedButton(
            onClick = {
                onFinishClick()
            },
            modifier = modifier,
            border = BorderStroke(
                width = 1.dp,
                color = colors.systemGrey6
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.greyWashBackground,
                contentColor = colors.white
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = stringResource(R.string.home_finish_button_text),
                style = typography.bodyMedium15,
                modifier = Modifier.padding(vertical = 14.dp)
            )
        }

        CourseDetailButton(
            text = stringResource(R.string.home_course_detail_button_text),
            onClick = onCourseDetailClick,
            modifier = modifier
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
