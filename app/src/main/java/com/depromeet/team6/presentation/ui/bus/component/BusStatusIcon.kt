package com.depromeet.team6.presentation.ui.bus.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.depromeet.team6.domain.model.BusCongestion
import com.depromeet.team6.domain.model.toInfo
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun BusStatusIcon(
    busNumber: String,
    busCongestion: BusCongestion,
    color: Color,
    modifier: Modifier = Modifier
) {
    val busCongestionInfo = busCongestion.toInfo()

    val busIconRes =
        when (color) {
            Color(0xFF24B847) -> R.drawable.ic_bus_course_position_regular_20
            Color(0xFF6FC53F) -> R.drawable.ic_bus_course_position_town_20
            Color(0xFF1777FF) -> R.drawable.ic_bus_course_position_main_line_20
            Color(0xFFF24747) -> R.drawable.ic_bus_course_position_wide_area_20
            Color(0xFF5FbbF9) -> R.drawable.ic_bus_course_position_airport_20
            else -> R.drawable.ic_bus_course_position_regular_20
        }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier.width(54.dp).roundedBackgroundWithPadding(
                cornerRadius = 3.dp,
                padding = PaddingValues(vertical = 5.dp),
                backgroundColor = defaultTeam6Colors.greyElevatedBackground
            ),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = busNumber,
                style = defaultTeam6Typography.bodyRegular10,
                color = defaultTeam6Colors.greySecondaryLabel
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = busCongestionInfo.label,
                style = defaultTeam6Typography.bodyRegular10,
                color = busCongestionInfo.color
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
            imageVector = ImageVector.vectorResource(busIconRes),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Preview
@Composable
private fun BusStatusIconPreview() {
    BusStatusIcon(busNumber = 5200.toString(), busCongestion = BusCongestion.LOW, color = Color(0xFF1777FF))
}
