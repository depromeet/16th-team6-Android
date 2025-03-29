package com.depromeet.team6.presentation.ui.searchlocation

import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.presentation.ui.searchlocation.component.BackTopBar
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchDepartureTextField
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchHistoryEmptyContainer
import com.depromeet.team6.presentation.ui.searchlocation.component.SearchLocationTextField
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun SearchLocationRoute(
    viewModel: SearchLocationViewModel = hiltViewModel(),
    navigateToBack: () -> Unit = {},
    navigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var userLocation by remember { mutableStateOf(37.5665 to 126.9780) } // 서울시 기본 위치

    LaunchedEffect(Unit) {
        Log.d("Location Permission", "${PermissionUtil.hasLocationPermissions(context)}")
        if (PermissionUtil.hasLocationPermissions(context)) {
            val location = context.getUserLocation()
            if (location != null) {
                userLocation = location
            }
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
            backButtonClick = navigateToBack,
            modifier = Modifier,
            uiState = uiState,
            searchText = uiState.searchQuery,
            onSearchTextChange = { newText ->
                viewModel.setState {
                    copy(searchQuery = newText)
                }
                viewModel.setEvent(
                    SearchLocationContract.SearchLocationEvent.UpdateSearchQuery(
                        text = newText,
                        lat = userLocation.first,
                        lon = userLocation.second
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
    backButtonClick: () -> Unit,
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
                backButtonClick = {},
                modifier = Modifier
            )

            SearchLocationTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
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
                if (uiState.searchQuery.isEmpty()) { // 검색어가 없을 때
                    // 최근 기록 api 호출 후 검색 내역 있을 경우, 없을 경우 분기 처리

                    // 검색 내역이 없을 때
                    SearchHistoryEmptyContainer()
                } else { // 검색어 입력 시
                    // api 연결 함수 추가
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
