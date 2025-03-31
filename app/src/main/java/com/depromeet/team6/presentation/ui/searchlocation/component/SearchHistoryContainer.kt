package com.depromeet.team6.presentation.ui.searchlocation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.presentation.ui.searchlocation.SearchLocationContract
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun SearchHistoryContainer(
    modifier: Modifier = Modifier,
    uiState: SearchLocationContract.SearchLocationUiState = SearchLocationContract.SearchLocationUiState(),
    onDeleteButtonClicked: (Location) -> Unit = {},
    onDeleteAllButtonClicked: () -> Unit = {},
    selectButtonClicked: (Location) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = defaultTeam6Colors.greyWashBackground)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_search_recent_history_text),
                style = defaultTeam6Typography.bodyRegular14,
                color = defaultTeam6Colors.greyTertiaryLabel
            )

            Text(
                text = stringResource(R.string.home_search_delete_all_text),
                style = defaultTeam6Typography.bodyRegular13,
                color = defaultTeam6Colors.greyTertiaryLabel,
                modifier = Modifier.noRippleClickable {
                    // 검색 내역 전체 삭제
                    onDeleteAllButtonClicked()
                }
            )
        }

        LazyColumn {
            items(uiState.recentSearches) { recentSearchLocation ->
                SearchHistoryItem(
                    homeSearchLocation = recentSearchLocation,
                    deleteButtonClicked = { onDeleteButtonClicked(recentSearchLocation) },
                    selectItemClicked = { selectButtonClicked(recentSearchLocation) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SearchHistoryContainerPreview() {
    val dummyLocations = listOf(
        Location(
            name = "서울역",
            lat = 37.5547,
            lon = 126.9706,
            businessCategory = "지하철",
            address = "서울 중구 한강대로 405",
            radius = "100m"
        ),
        Location(
            name = "서울역",
            lat = 37.5547,
            lon = 126.9706,
            businessCategory = "지하철",
            address = "서울 중구 한강대로 405",
            radius = "100"
        ),
        Location(
            name = "서울역",
            lat = 37.5547,
            lon = 126.9706,
            businessCategory = "지하철",
            address = "서울 중구 한강대로 405",
            radius = "100"
        )
    )

    SearchHistoryContainer(
        uiState = SearchLocationContract.SearchLocationUiState(
            searchQuery = "",
            recentSearches = dummyLocations
        ),
        onDeleteButtonClicked = { /* TODO */ },
        selectButtonClicked = { /* TODO */ }
    )
}
