package com.depromeet.team6.presentation.ui.searchlocation

import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class SearchLocationContract {
    data class SearchLocationUiState(
        val loadState: LoadState = LoadState.Idle,
        val searchQuery: String = "",
        val searchResults: List<Location> = emptyList(),
        val recentSearches: List<Location> = emptyList()
    ) : UiState

    sealed interface SearchLocationSideEffect : UiSideEffect {
        data object NavigateBack: SearchLocationSideEffect
    }

    sealed class SearchLocationEvent : UiEvent {
        data class UpdateSearchQuery(val text: String, val lat: Double, val lon: Double) : SearchLocationEvent()
        data class UpdateSearchResults(val results: List<Location>) :
            SearchLocationEvent()

        data class UpdateRecentSearches(val recentSearches: List<Location>) :
            SearchLocationEvent()

        data object ClearRecentSearches : SearchLocationEvent()
    }
}
