package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.model.location.Location
import com.depromeet.team6.presentation.util.modifier.addFocusCleaner
import com.depromeet.team6.presentation.util.view.partitionByAddressCategory
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import com.google.android.gms.maps.model.LatLng

@Composable
fun OnboardingSearchPopup(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    searchLocations: List<Location> = emptyList(),
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onBackButtonClicked: () -> Unit = {},
    onTextClearButtonClicked: () -> Unit = {},
    onGpsButtonClicked: () -> Unit = {},
    selectButtonClicked: (Location) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val (addressLocations, placeLocations) = searchLocations.partitionByAddressCategory()

    var selectedLocation by remember { mutableStateOf<Location?>(null) }

    Box(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
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
                if (addressLocations.isNotEmpty()) {
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(color = defaultTeam6Colors.black)
                        )
                        Text(
                            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 4.dp),
                            text = "주소 결과",
                            style = defaultTeam6Typography.bodyRegular14,
                            color = defaultTeam6Colors.greyTertiaryLabel
                        )
                    }
                    items(addressLocations) { location ->
                        OnboardingSearchLocationItem(
                            onboardingSearchLocation = location,
                            selectButtonClicked = {
//                                selectButtonClicked(location)
                                selectedLocation = location
                            }
                        )
                    }
                }
                if (placeLocations.isNotEmpty()) {
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(color = defaultTeam6Colors.black)
                        )
                        Text(
                            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 4.dp),
                            text = "장소 결과",
                            style = defaultTeam6Typography.bodyRegular14,
                            color = defaultTeam6Colors.greyTertiaryLabel
                        )
                    }
                    items(placeLocations) { location ->
                        OnboardingSearchLocationItem(
                            onboardingSearchLocation = location,
                            selectButtonClicked = {
//                                selectButtonClicked(location)
                                selectedLocation = location
                            }
                        )
                    }
                }
            }
        }
        if (selectedLocation != null) {
            OnboardingMapView(
                currentLocation = LatLng(selectedLocation!!.lat, selectedLocation!!.lon),
                buttonClicked = { selectButtonClicked(selectedLocation!!) },
                backButtonClicked = { selectedLocation = null }
            )
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
