package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.presentation.ui.onboarding.OnboardingContract
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun OnboardingSearchPopup(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    uiState: OnboardingContract.OnboardingUiState = OnboardingContract.OnboardingUiState(),
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onCloseButtonClicked: () -> Unit = {},
    selectButtonClicked: (Location) -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .background(color = defaultTeam6Colors.greyWashBackground)
    ) {
        OnboardingSearchTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            onCloseButtonClicked = onCloseButtonClicked
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (uiState.searchLocations.isNotEmpty()) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = defaultTeam6Colors.greyDivider)
            )
            LazyColumn {
                items(uiState.searchLocations) { onboardingSearchLocation ->
                    OnboardingSearchLocationItem(
                        onboardingSearchLocation = onboardingSearchLocation,
                        selectButtonClicked = { selectButtonClicked(onboardingSearchLocation) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingSearchPopupPreview() {
    OnboardingSearchPopup(padding = PaddingValues(0.dp))
}
