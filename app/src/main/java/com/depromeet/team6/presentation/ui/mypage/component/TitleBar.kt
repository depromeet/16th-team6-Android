package com.depromeet.team6.presentation.ui.mypage.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun TitleBar(
    title: String,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val typography = LocalTeam6Typography.current
    val colors = LocalTeam6Colors.current

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_grey),
            contentDescription = stringResource(R.string.mypage_icon_arrow_text),
            tint = colors.systemGrey1,
            modifier = modifier
                .padding(vertical = 18.dp, horizontal = 16.dp)
                .clickable { onBackClick() }
        )

        Text(
            text = title,
            style = typography.heading5SemiBold17,
            color = colors.white,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(18.dp)
        )
    }
}

@Preview
@Composable
fun TitleBarPreview() {
    TitleBar(
        title = "마이페이지"
    )
}
