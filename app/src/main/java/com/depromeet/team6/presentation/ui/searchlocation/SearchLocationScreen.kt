package com.depromeet.team6.presentation.ui.searchlocation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.presentation.ui.home.HomeViewModel
import com.depromeet.team6.presentation.ui.searchlocation.component.BackTopBar
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchDepartureTextField
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchHistoryContainer
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchHistoryEmptyContainer
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchLocationItem
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchLocationMapView
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchLocationTextField
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNT
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import timber.log.Timber

@Composable
fun SearchLocationRoute(
    viewModel: SearchLocationViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    destinationLocation : Address,
    navigateToBack: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
    navigateToCourseSearch: (String, String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var userLocation by remember { mutableStateOf(LatLng(DEFAULT_LNT, DEFAULT_LNG)) }

    LaunchedEffect(Unit) {
        Timber.tag("Location Permission").d("${PermissionUtil.hasLocationPermissions(context)}")
        if (PermissionUtil.hasLocationPermissions(context)) {
            val location = context.getUserLocation()
            userLocation = LatLng(DEFAULT_LNT, DEFAULT_LNG)

//            homeViewModel.getCenterLocation(userLocation)
//            viewModel.setState {
//                copy(searchQuery = homeUiState.departurePoint.name)
//            }

            viewModel.setEvent(
                SearchLocationContract.SearchLocationEvent.UpdateUserLocationSate(
                    LoadState.Success
                )
            )
        }
    }

    LaunchedEffect(uiState.userLocation) {
        viewModel.updateRecentSearches(location = userLocation)
    }

    LaunchedEffect(uiState.searchResults) {
        if (uiState.searchQuery.isNotEmpty() && uiState.searchResults.isEmpty()) {
            Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
        viewModel.sideEffect.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is SearchLocationContract.SearchLocationSideEffect.NavigateBack -> navigateToBack()
                }
            }
    }

    when (uiState.loadState) {
        LoadState.Idle -> SearchLocationScreen(
            context = context,
            modifier = Modifier,
            viewModel = viewModel,
            backButtonClick = navigateToBack,
            location = userLocation,
            uiState = uiState,
            searchText = uiState.searchQuery,
            onSearchTextChange = { newText ->
                viewModel.setState {
                    copy(searchQuery = newText)
                }
                viewModel.setEvent(
                    SearchLocationContract.SearchLocationEvent.UpdateSearchQuery(
                        text = newText,
                        lat = userLocation.latitude,
                        lon = userLocation.longitude
                    )
                )
            },
            onDeleteAllButtonClicked = { viewModel.deleteAllSearchHistory() },
            onDeleteButtonClicked =
            { searchHistory -> // 검색 내역 삭제
                viewModel.deleteSearchHistory(
                    searchHistory = searchHistory,
                    location = userLocation
                )
            },
            selectButtonClicked = { searchHistory -> // 장소 선택
                // 장소 텍스트 검색
                viewModel.setState {
                    copy(searchQuery = searchHistory.name)
                }
                viewModel.setState {
                    copy(
                        selectLocation = Address(
                            searchHistory.name,
                            searchHistory.lat,
                            searchHistory.lon,
                            searchHistory.address
                        )
                    )
                }
                viewModel.setEvent(
                    SearchLocationContract.SearchLocationEvent.UpdateSearchQuery(
                        text = searchHistory.name,
                        lat = userLocation.latitude,
                        lon = userLocation.longitude
                    )
                )
                viewModel.setEvent(
                    SearchLocationContract.SearchLocationEvent.ChangeSearchSelectMapViewVisible(true)
                )
                // 최근 검색 내역 추가
                viewModel.postSearchHistory(searchHistory)
            },
            navigateToCourseSearch = {
                viewModel.setEvent(
                    SearchLocationContract.SearchLocationEvent.ChangeSearchSelectMapViewVisible(
                        false
                    )
                )

                val currentLocationJSON = Gson().toJson(uiState.selectLocation)
                val destinationPointJSON = Gson().toJson(destinationLocation)
                navigateToCourseSearch(
                    currentLocationJSON,
                    destinationPointJSON
                )
            },
            clearAddress = {
                viewModel.setEvent(SearchLocationContract.SearchLocationEvent.ClearText)
                viewModel.setEvent(
                    SearchLocationContract.SearchLocationEvent.ChangeSearchSelectMapViewVisible(
                        searchSelectMapView = false
                    )
                )
            },
            getCenterLocation = { viewModel.getCenterLocation(it) }
        )

        LoadState.Error -> navigateToLogin()
        else -> Unit
    }
}

@Composable
fun SearchLocationScreen(
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    viewModel: SearchLocationViewModel = hiltViewModel(),
    backButtonClick: () -> Unit,
    location: LatLng,
    uiState: SearchLocationContract.SearchLocationUiState = SearchLocationContract.SearchLocationUiState(),
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onDeleteButtonClicked: (Location) -> Unit = {},
    onDeleteAllButtonClicked: () -> Unit = {},
    selectButtonClicked: (Location) -> Unit = {},
    navigateToCourseSearch: () -> Unit = {},
    clearAddress: () -> Unit = {},
    getCenterLocation: (LatLng) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = defaultTeam6Colors.greyWashBackground
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BackTopBar(
                backButtonClick = { backButtonClick() },
                modifier = Modifier
            )

            SearchLocationTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                onTextClearButtonClicked = {
                    viewModel.setEvent(SearchLocationContract.SearchLocationEvent.ClearText)
                    viewModel.updateRecentSearches(location = location)
                },
                modifier = Modifier.fillMaxWidth()
            )

            SearchDepartureTextField()

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 10.dp,
                color = defaultTeam6Colors.black
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                /** 검색 리스트에 들어왔을 때
                 * 1. 검색창에 현위치 띄우고 최근 검색 내역 보여주기
                 *   1-1. 검색 기록이 있을 경우 SearchHistoryContainer
                 *   1-2. 검색 기록이 없을 경우 SearchHistoryEmptyContainer
                 * 2. 검색 시작 시 검색 리스트 연결
                 * 3. 검색 리스트 삭제 시 다시 최근 검색 내역 보여주기
                 */

                // 검색 창이 비었거나 현위치가 띄워져 있을 때
                if (uiState.searchQuery.isEmpty()) {
                    if (uiState.recentSearches.isNotEmpty()) { // 최근 검색 내역이 존재할 때
                        SearchHistoryContainer(
                            modifier = Modifier,
                            uiState = uiState,
                            onDeleteButtonClicked = onDeleteButtonClicked,
                            onDeleteAllButtonClicked = onDeleteAllButtonClicked,
                            selectButtonClicked = selectButtonClicked
                        )
                    } else {
                        SearchHistoryEmptyContainer()
                    }
                } else { // 검색어 입력 시
                    if (uiState.searchResults.isNotEmpty()) {
                        LazyColumn {
                            items(uiState.searchResults) { location ->
                                SearchLocationItem(
                                    homeSearchLocation = location,
                                    modifier = Modifier,
                                    selectButtonClicked = selectButtonClicked
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.searchSelectMapView) {
            val currentLocation by remember { mutableStateOf(uiState.selectLocation) }
            SearchLocationMapView(
                context = context,
                myAddress = uiState.selectLocation,
                getCenterLocation = getCenterLocation,
                currentLocation = currentLocation,
                setDepartureButtonClicked = navigateToCourseSearch,
                backButtonClicked = clearAddress
            )
        }
    }
}

@Preview
@Composable
fun SearchLocationScreenPreview() {
    SearchLocationScreen(
        backButtonClick = {},
        location = LatLng(37.5665, 126.9780)
    )
}
