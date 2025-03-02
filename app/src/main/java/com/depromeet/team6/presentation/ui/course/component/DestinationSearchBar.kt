package com.depromeet.team6.presentation.ui.course.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun DestinationSearchBar(
    startingPoint: String,
    destination: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = defaultTeam6Colors.greyElevatedCard,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO : typography 디자인 시스템 적용
        Text(
            text = startingPoint,
            fontSize = 16.sp,
            color = defaultTeam6Colors.greySecondaryLabel,
            modifier = Modifier
                .padding(start = 62.dp)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_all_arrow_right_grey),
            contentDescription = "arrow right",
            tint = defaultTeam6Colors.greySecondaryLabel,
            modifier = Modifier.size(21.dp)
        )

        // TODO : typography 디자인 시스템 적용
        Text(
            text = destination,
            fontSize = 16.sp,
            color = defaultTeam6Colors.greySecondaryLabel,
            modifier = Modifier.padding(end = 62.dp)
        )
    }
}

@Preview
@Composable
fun PreviewDestinationSearchBar() {
    DestinationSearchBar(
        startingPoint = "마루 180",
        destination = "집"
    )
}
