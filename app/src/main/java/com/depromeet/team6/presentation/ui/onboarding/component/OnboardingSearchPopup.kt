package com.depromeet.team6.presentation.ui.onboarding.component

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.mapper.toAddress
import com.depromeet.team6.presentation.model.location.Location
import com.depromeet.team6.presentation.ui.onboarding.OnboardingContract
import com.depromeet.team6.presentation.ui.onboarding.OnboardingViewModel
import com.depromeet.team6.presentation.util.modifier.addFocusCleaner
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.toast.atChaToastMessage
import com.depromeet.team6.presentation.util.view.partitionByAddressCategory
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import com.google.android.gms.maps.model.LatLng

@Composable
fun OnboardingSearchPopup(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    uiState: OnboardingContract.OnboardingUiState = OnboardingContract.OnboardingUiState(),
    viewModel: OnboardingViewModel = hiltViewModel(),
    searchLocations: List<Location> = emptyList(),
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onBackButtonClicked: () -> Unit = {},
    onTextClearButtonClicked: () -> Unit = {},
    selectButtonClicked: (Address) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val (addressLocations, placeLocations) = searchLocations.partitionByAddressCategory()

    var selectedLocation by remember {
        mutableStateOf(
            Address(
                name = "",
                lat = 0.0,
                lon = 0.0,
                address = ""
            )
        )
    }

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
                onGpsButtonClicked = {
                    if (PermissionUtil.hasLocationPermissions(context = context)) {
                        viewModel.setCurrentLocationToHomeAddress(context = context) { address ->
                            selectedLocation = address
                        }
                    } else {
                        atChaToastMessage(
                            context = context,
                            R.string.onboarding_location_no_permission_toast,
                            length = Toast.LENGTH_SHORT
                        )
                    }
                },
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
                                selectedLocation = location.toAddress()
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
                                selectedLocation = location.toAddress()
                            }
                        )
                    }
                }
            }
        }
        if (selectedLocation.address.isNotEmpty()) {
            OnboardingMapView(
                uiState = uiState,
                viewModel = viewModel,
                currentLocation = selectedLocation,
                buttonClicked = { selectButtonClicked(selectedLocation) },
                backButtonClicked = {
                    selectedLocation = Address(
                        name = "",
                        lat = 0.0,
                        lon = 0.0,
                        address = ""
                    )
                }
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
