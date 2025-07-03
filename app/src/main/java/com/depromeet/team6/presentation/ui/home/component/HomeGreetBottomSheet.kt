package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.Team6Theme
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun HomeGreetBottomSheet(
    modifier: Modifier = Modifier,
    buttonClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = defaultTeam6Colors.gray940
            )
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_home_greet_bottom_sheet),contentDescription = null, tint = Color.Unspecified, modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).padding(vertical = 12.dp))

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = buildAnnotatedString {
                append("앗차는 티맵 API와 공공 데이터로 \n")
                withStyle(style = SpanStyle(color = Team6Theme.colors.main)) {
                    append("신뢰도 있는 정보")
                }
                append("를 제공하고 있어요")
            },
            style = Team6Theme.typography.heading4Bold20,
            color = Team6Theme.colors.white
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "확인",
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { buttonClicked() }
                .roundedBackgroundWithPadding(
                    cornerRadius = 9.dp,
                    backgroundColor = defaultTeam6Colors.main,
                    padding = PaddingValues(vertical = 14.dp, horizontal = 28.dp)
                ),
            textAlign = TextAlign.Center,
            style = defaultTeam6Typography.heading6Bold15,
            color = defaultTeam6Colors.black
        )
        Spacer(modifier = Modifier.height(20.dp))
    }

}

@Preview
@Composable
private fun HomeGreetBottomSheetPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeGreetBottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
