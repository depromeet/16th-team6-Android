package com.depromeet.team6.presentation.ui.searchlocation

import com.depromeet.team6.domain.model.HomeSearchLocation
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState

class SearchLocationContract {
    data class SearchLocationUiState(
        val searchQuery: String = "",
        val searchResults: List<HomeSearchLocation> = emptyList(),
        val recentSearches: List<HomeSearchLocation> = listOf(
            HomeSearchLocation(
                "60계 치킨 강남점",
                "100m",
                "강남구 테헤란 4길 6ㅁ"
            )
        )
    ) : UiState

    sealed interface SearchLocationSideEffect : UiSideEffect {
    }

    sealed class SearchLocationEvent : UiEvent {
        data class UpdateSearchQuery(val query: String) : SearchLocationEvent()
        data class UpdateSearchResults(val results: List<HomeSearchLocation>) :
            SearchLocationEvent()

        data class UpdateRecentSearches(val recentSearches: List<HomeSearchLocation>) :
            SearchLocationEvent()

        data object ClearRecentSearches : SearchLocationEvent()
    }
}