package com.depromeet.team6.presentation.ui.searchlocation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.ui.searchlocation.component.BackTopBar
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchDepartureTextField
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchHistoryEmptyContainer
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchLocationTextField
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun SearchLocationScreen(
    backButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: SearchLocationContract.SearchLocationUiState = SearchLocationContract.SearchLocationUiState()
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.searchResults) {
        if (uiState.searchResults.isEmpty()) {
            Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = defaultTeam6Colors.greyElevatedBackground
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BackTopBar(
                backButtonClick = {},
                modifier = Modifier
            )

            SearchLocationTextField(
                value = uiState.searchQuery,
                onValueChange = { searchText },
                onCloseButtonClicked = { backButtonClick() },
                modifier = Modifier.fillMaxWidth()
            )

            SearchDepartureTextField()

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 10.dp,
                color = defaultTeam6Colors.black
            )

            Box() {
                if (uiState.recentSearches.isEmpty()) {
                    SearchHistoryEmptyContainer()
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchLocationScreenPreview() {
    SearchLocationScreen(
        backButtonClick = {}
    )
}
