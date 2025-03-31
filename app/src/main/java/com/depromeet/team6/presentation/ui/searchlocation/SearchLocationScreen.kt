package com.depromeet.team6.presentation.ui.searchlocation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import com.depromeet.team6.presentation.ui.searchlocation.component.BackTopBar
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchDepartureTextField
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchHistoryContainer
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchHistoryEmptyContainer
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchLocationTextField
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNT
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

@Composable
fun SearchLocationRoute(
    viewModel: SearchLocationViewModel = hiltViewModel(),
    navigateToBack: () -> Unit = {},
    navigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var userLocation by remember { mutableStateOf(LatLng(DEFAULT_LNT, DEFAULT_LNG)) } // 서울시 기본 위치

    LaunchedEffect(Unit) {
        Timber.tag("Location Permission").d("${PermissionUtil.hasLocationPermissions(context)}")
        if (PermissionUtil.hasLocationPermissions(context)) {
            val location = context.getUserLocation()
            userLocation = location
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
            }
        )

        LoadState.Error -> navigateToLogin()
        else -> Unit
    }
}

@Composable
fun SearchLocationScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchLocationViewModel = hiltViewModel(),
    backButtonClick: () -> Unit,
    location: LatLng,
    uiState: SearchLocationContract.SearchLocationUiState = SearchLocationContract.SearchLocationUiState(),
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {}
) {
    val context = LocalContext.current

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
                backButtonClick = { backButtonClick() },
                modifier = Modifier
            )

            SearchLocationTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                onTextClearButtonClicked = {
                    viewModel.setEvent(SearchLocationContract.SearchLocationEvent.ClearText)
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

                if (uiState.searchQuery.isEmpty()) {
                    viewModel.updateRecentSearches(location = location)

                    if (uiState.recentSearches.isEmpty()) { // 검색 내역이 없을 때
                        SearchHistoryEmptyContainer()
                    } else {
                        SearchHistoryContainer(
                            modifier = Modifier,
                            uiState = SearchLocationContract.SearchLocationUiState(),
                            onDeleteButtonClicked = { searchHistory -> // 검색 내역 삭제
                                viewModel.deleteSearchHistory(searchHistory = searchHistory, location = location)
                            },
                            onDeleteAllButtonClicked = { viewModel.deleteAllSearchHistory() },
                            selectButtonClicked = { searchHistory -> // 장소 선택
                                // 장소 텍스트 검색
                                onSearchTextChange(searchHistory.name)
                                // 최근 검색 내역 추가
                                viewModel.postSearchHistory(searchHistory)
                            }
                        )
                    }
                }
                else {
//                    LazyColumn {
//                        if ()
//                    }
                }
            }
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
