package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.common.TransportVectorIcon
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun TransportStatus(
    transportationType: TransportType, // 종류 : 버스 or 지하철
    transportationNumber: Int, // 몇호선인지
    transportationName: String, // 타야하는 것 : 버스 번호 or 지하철역 이름
    stopLeft: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    val stopLeftText = if (transportationType == TransportType.SUBWAY) {
        "개 역 전"
    } else {
        "정류장 전"
    }

    Row {
        TransportVectorIcon(
            modifier = Modifier.size(15.dp),
            type = transportationType,
            color = TransportTypeUiMapper.getColor(transportationType, transportationNumber),
            isMarker = false
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
        transportationType = TransportType.BUS,
        transportationNumber = 0,
        transportationName = "서울역",
        stopLeft = 1
    )
}