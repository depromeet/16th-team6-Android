package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.layout.Row
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
fun TransportStatus(
    transportationType: String, // 종류 : 버스 or 지하철
    transportationNumber: String, // 몇호선인지
    transportationName: String, // 타야하는 것 : 버스 번호 or 지하철역 이름
    stopLeft: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    val iconImage = if (transportationType == "SUBWAY") {
        ImageVector.vectorResource(R.drawable.ic_home_subway_green)
    } else {
        ImageVector.vectorResource(R.drawable.ic_home_bus_blue)
    }

    // TODO : transportationNumber 몇 호선인지에 따라 Icon 색상 변경
    val iconColor = if (transportationNumber == "2호선") {
        colors.systemGreen
    } else {
        colors.white
    }

    val stopLeftText = if (transportationType == "SUBWAY") {
        "개 역 전"
    } else {
        "정류장 전"
    }

    Row(

    ) {
        Icon(
            imageVector = iconImage,
            contentDescription = stringResource(R.string.home_icon_transportation),
            modifier = modifier,
            tint = iconColor
        )

        Text(
            text = transportationName,
            style = typography.bodySemiBold13,
            color = colors.white,
            modifier = Modifier
                .padding(horizontal = 4.dp)
        )

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_home_dot_grey),
            contentDescription = stringResource(R.string.home_icon_dot),
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterVertically),
            tint = colors.systemGrey1
        )

        Text(
            text = stopLeft.toString(),
            style = typography.bodyRegular13,
            color = colors.systemGrey1,
            modifier = Modifier
                .padding(start = 4.dp)
        )

        Text(
            text = stopLeftText,
            style = typography.bodyRegular13,
            color = colors.systemGrey1,
            modifier = Modifier
                .padding(horizontal = 2.dp)
        )
    }
}

@Preview
@Composable
fun TransportStatusPreview() {
    TransportStatus(
        transportationType = "SUBWAY",
        transportationNumber = "2호선",
        transportationName = "서울역",
        stopLeft = 1
    )
}