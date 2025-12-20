package com.depromeet.team6.presentation.ui.onboarding.component

import android.content.Context
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.mapper.toAddress
import com.depromeet.team6.presentation.model.location.Location
import com.depromeet.team6.presentation.ui.onboarding.OnboardingViewModel
import com.depromeet.team6.presentation.util.modifier.addFocusCleaner
import com.depromeet.team6.presentation.util.modifier.advancedImePadding
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.partitionByAddressCategory
import com.depromeet.team6.ui.theme.Team6Theme
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun OnboardingSearchPopup(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    viewModel: OnboardingViewModel = hiltViewModel(),
    searchLocations: List<Location> = emptyList(),
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onBackButtonClicked: () -> Unit = {},
    onTextClearButtonClicked: () -> Unit = {},
    selectButtonClicked: (Address) -> Unit = {},
    settingDialog: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val (addressLocations, placeLocations) = searchLocations.partitionByAddressCategory()

    BackHandler {
        onBackButtonClicked()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .addFocusCleaner(focusManager)
            .background(color = defaultTeam6Colors.gray950)
            .padding(padding)
            .advancedImePadding()
    ) {
        OnboardingSearchTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            onBackButtonClicked = onBackButtonClicked,
            onTextClearButtonClicked = onTextClearButtonClicked,
            onGpsButtonClicked = {
                if (PermissionUtil.hasLocationPermissions(context = context)) {
                    viewModel.setCurrentLocationToHomeAddress(context = context) { address ->
                        selectButtonClicked(address)
                    }
                } else {
                    settingDialog()
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
                        style = defaultTeam6Typography.body6_B6R14,
                        color = defaultTeam6Colors.gray400
                    )
                }
                items(addressLocations) { location ->
                    OnboardingSearchLocationItem(
                        onboardingSearchLocation = location,
                        selectButtonClicked = {
                            selectButtonClicked(location.toAddress())
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
                        style = defaultTeam6Typography.body6_B6R14,
                        color = defaultTeam6Colors.gray400
                    )
                }
                items(placeLocations) { location ->
                    OnboardingSearchLocationItem(
                        onboardingSearchLocation = location,
                        selectButtonClicked = {
                            selectButtonClicked(location.toAddress())
                        }
                    )
                }
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
    Team6Theme {
        OnboardingSearchPopup(
            padding = PaddingValues(0.dp),
            settingDialog = {}
        )
    }
}
