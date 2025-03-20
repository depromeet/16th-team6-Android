package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.presentation.ui.onboarding.OnboardingContract
import com.depromeet.team6.presentation.util.modifier.addFocusCleaner
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun OnboardingSearchPopup(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    uiState: OnboardingContract.OnboardingUiState = OnboardingContract.OnboardingUiState(),
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onBackButtonClicked: () -> Unit = {},
    onTextClearButtonClicked: () -> Unit = {},
    onGpsButtonClicked: () -> Unit = {},
    selectButtonClicked: (Location) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .addFocusCleaner(focusManager)
            .background(color = defaultTeam6Colors.greyWashBackground)
    ) {
        OnboardingSearchTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            onBackButtonClicked = onBackButtonClicked,
            onTextClearButtonClicked = onTextClearButtonClicked,
            onGpsButtonClicked = onGpsButtonClicked,
            focusRequester = focusRequester
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyColumn {
            items(uiState.searchLocations) { onboardingSearchLocation ->
                OnboardingSearchLocationItem(
                    onboardingSearchLocation = onboardingSearchLocation,
                    selectButtonClicked = { selectButtonClicked(onboardingSearchLocation) }
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun OnboardingSearchPopupPreview() {
    OnboardingSearchPopup(padding = PaddingValues(0.dp))
}
