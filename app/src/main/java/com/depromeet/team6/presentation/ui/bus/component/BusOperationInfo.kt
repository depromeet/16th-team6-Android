package com.depromeet.team6.presentation.ui.bus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.common.TransportVectorIconComposable
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun BusOperationInfo(
    busNumber:String,
    busColor: Color,
    innerPaddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    backButtonClicked: () -> Unit = {}
) {
    Column {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = defaultTeam6Colors.greyElevatedBackground)
                .padding(innerPaddingValues)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_grey),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(vertical = 18.dp, horizontal = 16.dp)
                            .noRippleClickable { backButtonClicked() }
                            .align(Alignment.CenterStart)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TransportVectorIconComposable(
                            type = TransportType.BUS,
                            color = busColor,
                            isMarker = false,
                            modifier = Modifier
                                .size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = busNumber,
                            style = defaultTeam6Typography.heading5SemiBold17,
                            color = defaultTeam6Colors.white
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun BusOperationInfoPreview() {
    BusOperationInfo(
        busNumber = "350",
        busColor = LocalTeam6Colors.current.busMainLine,
        innerPaddingValues = PaddingValues(0.dp)
    )
}