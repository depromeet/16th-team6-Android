package com.depromeet.team6.presentation.ui.searchlocation

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.HomeSearchLocation
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchLocationViewModel @Inject constructor() : BaseViewModel<SearchLocationContract.SearchLocationUiState, SearchLocationContract.SearchLocationSideEffect, SearchLocationContract.SearchLocationEvent>() {

    override fun createInitialState(): SearchLocationContract.SearchLocationUiState =
        SearchLocationContract.SearchLocationUiState()

    override suspend fun handleEvent(event: SearchLocationContract.SearchLocationEvent) {
        when (event) {
            is SearchLocationContract.SearchLocationEvent.UpdateSearchQuery -> setState {
                copy(
                    searchQuery = event.query
                )
            }

            is SearchLocationContract.SearchLocationEvent.UpdateSearchResults -> setState {
                copy(
                    searchResults = event.results
                )
            }

            is SearchLocationContract.SearchLocationEvent.UpdateRecentSearches -> setState {
                copy(
                    recentSearches = event.recentSearches
                )
            }

            is SearchLocationContract.SearchLocationEvent.ClearRecentSearches -> setState {
                copy(
                    recentSearches = emptyList()
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            setEvent(SearchLocationContract.SearchLocationEvent.UpdateSearchQuery(query))
        }
    }

    fun updateSearchResults(results: List<HomeSearchLocation>) {
        viewModelScope.launch {
            setEvent(SearchLocationContract.SearchLocationEvent.UpdateSearchResults(results))
        }
    }

    fun updateRecentSearches(recentSearches: List<HomeSearchLocation>) {
        viewModelScope.launch {
            setEvent(SearchLocationContract.SearchLocationEvent.UpdateRecentSearches(recentSearches))
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            setEvent(SearchLocationContract.SearchLocationEvent.ClearRecentSearches)
        }
    }
}
