package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

@Composable
fun AfterRegisterSheet(
    isConfirmed: Boolean = false,
    timeToLeave: String,
    startLocation: String,
    destination: String,
    onCourseTextClick: () -> Unit,
    onFinishClick: () -> Unit,
    onCourseDetailClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBusDeparted: Boolean = false
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    var timeTextColor = colors.systemGrey1

    if (isConfirmed) {
        timeTextColor = colors.white
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .height(248.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    colors.greyWashBackground,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                ) {
                    if (isConfirmed) {
                        Text(
                            text = stringResource(R.string.home_start_time_text),
                            style = typography.bodyMedium13,
                            color = colors.white,
                            modifier = modifier
                        )

                    } else {
                        Text(
                            text = stringResource(R.string.home_expect_start_time_text),
                            style = typography.bodyMedium13,
                            color = colors.white,
                            modifier = modifier
                        )
                    }

                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_all_info_grey),
                        contentDescription = stringResource(R.string.home_icon_info),
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        tint = colors.systemGrey1
                    )

                    if (isConfirmed) {
                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_all_refresh_white),
                            contentDescription = stringResource(R.string.home_icon_refresh),
                            modifier = Modifier
                                .padding(horizontal = 5.dp),
                            tint = colors.white
                        )
                    }
                }

                TimeText(
                    timeToLeave = timeToLeave,
                    textColor = timeTextColor,
                    modifier = modifier.padding(top = 4.dp, bottom = 6.dp)
                )


            CourseTextButton(
                startLocation = startLocation,
                destination = destination,
                onClick = onCourseTextClick,
                modifier = modifier
            )

            Spacer(modifier = modifier.height(26.dp))

            FinishCourseDetailButton(
                onFinishClick = {
                    onFinishClick()
                },
                onCourseDetailClick = onCourseDetailClick,
                modifier = modifier
            )
        }
    }
}

@Preview
@Composable
fun AfterRegisterSheetPreview() {
    AfterRegisterSheet(
        timeToLeave = "23:30:00",
        startLocation = "중앙빌딩",
        destination = "우리집",
        onCourseTextClick = { },
        onFinishClick = { },
        onCourseDetailClick = { },
        modifier = Modifier
    )
}
