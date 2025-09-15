package com.depromeet.team6.presentation.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.common.textfields.TextFieldSearchIcon
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String = "",
    hintText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onTextClearButtonClicked: () -> Unit = {},
    onMapButtonClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextFieldSearchIcon(
            modifier = Modifier.weight(1f),
            value = value,
            hintText = hintText,
            onValueChange = onSearchTextChange,
            onTextClearButtonClicked = onTextClearButtonClicked
        )

        Spacer(modifier = Modifier.width(6.dp))

        Icon(
            modifier = Modifier
                .noRippleClickable {
                    onMapButtonClicked()
                },
            imageVector = ImageVector.vectorResource(R.drawable.ic_search_list_map_28dp),
            tint = defaultTeam6Colors.gray200,
            contentDescription = stringResource(R.string.home_search_map_icon)
        )
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(
        hintText = "지번, 도로명, 건물명으로 검색",
        onMapButtonClicked = {}
    )
}
