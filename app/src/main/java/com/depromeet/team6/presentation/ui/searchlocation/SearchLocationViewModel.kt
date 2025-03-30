package com.depromeet.team6.presentation.ui.searchlocation

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.model.SearchHistory
import com.depromeet.team6.domain.usecase.DeleteAllSearchHistoryUseCase
import com.depromeet.team6.domain.usecase.DeleteSearchHistoryUseCase
import com.depromeet.team6.domain.usecase.GetLocationsUseCase
import com.depromeet.team6.domain.usecase.GetSearchHistoriesUseCase
import com.depromeet.team6.domain.usecase.PostSearchHistoriesUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getSearchHistoriesUseCase: GetSearchHistoriesUseCase,
    private val postSearchHistoriesUseCase: PostSearchHistoriesUseCase,
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryUseCase,
    private val deleteAllSearchHistoryUseCase: DeleteAllSearchHistoryUseCase
) : BaseViewModel<SearchLocationContract.SearchLocationUiState, SearchLocationContract.SearchLocationSideEffect, SearchLocationContract.SearchLocationEvent>() {

    override fun createInitialState(): SearchLocationContract.SearchLocationUiState =
        SearchLocationContract.SearchLocationUiState()

    override suspend fun handleEvent(event: SearchLocationContract.SearchLocationEvent) {
        when (event) {
            is SearchLocationContract.SearchLocationEvent.UpdateSearchQuery -> handleUpdateSearchText(
                event = event
            )

            is SearchLocationContract.SearchLocationEvent.UpdateSearchResults -> setState {
                copy(
                    searchResults = event.results
                )
            }

            is SearchLocationContract.SearchLocationEvent.UpdateRecentSearches -> updateRecentSearches(
                location = LatLng(event.lat, event.lon)
            )

            is SearchLocationContract.SearchLocationEvent.DeleteSearchHistory -> setState {
                copy(

                )
            }

            is SearchLocationContract.SearchLocationEvent.ClearRecentSearches -> setState {
                copy(
                    recentSearches = emptyList()
                )
            }
        }
    }

    fun updateSearchResults(results: List<Location>) {
        viewModelScope.launch {
            setEvent(SearchLocationContract.SearchLocationEvent.UpdateSearchResults(results))
        }
    }

    // 최근 검색 내역 조회
    fun updateRecentSearches(location: LatLng) {
        setEvent(
            SearchLocationContract.SearchLocationEvent.UpdateRecentSearches(
                lat = location.latitude,
                lon = location.longitude
            )
        )

        viewModelScope.launch {
            getSearchHistoriesUseCase(location.latitude, location.longitude)
                .onSuccess { searchHistories ->
                    setState { copy(
                        recentSearches = searchHistories
                    ) }
                }
                .onFailure {
                    setState { copy(recentSearches = emptyList()) }
                }
        }
    }

    // 검색 내역 삭제
    fun deleteSearchHistory(searchHistory: Location, location: LatLng) {

        val convertedSearchHistory = SearchHistory(
            name = searchHistory.name,
            lat = searchHistory.lat,
            lon = searchHistory.lon,
            businessCategory = searchHistory.businessCategory,
            address = searchHistory.address
        )

        setEvent(
            SearchLocationContract.SearchLocationEvent.DeleteSearchHistory(
                searchHistory = convertedSearchHistory
            )
        )

        viewModelScope.launch {
            if (deleteSearchHistoryUseCase(searchHistory = convertedSearchHistory).isSuccessful) {
                updateRecentSearches(location = LatLng(location.latitude, location.longitude))
            }
            else {
                Timber.e("deleteSearchHistory failure")
            }
        }
    }

    // 최근 검색 내역 전체 삭제
    fun deleteAllSearchHistory() {
        viewModelScope.launch {
            setEvent(SearchLocationContract.SearchLocationEvent.ClearRecentSearches)
        }
    }

    private var debounceJob: Job? = null

    private fun handleUpdateSearchText(event: SearchLocationContract.SearchLocationEvent.UpdateSearchQuery) {
        setState { copy(searchQuery = event.text) }
        debounceJob?.cancel()
        if (event.text.isNotEmpty()) {
            debounceJob = viewModelScope.launch {
                delay(300)
                getLocationsUseCase(
                    keyword = event.text,
                    lat = event.lat,
                    lon = event.lon
                ).onSuccess { locations ->
                    setState {
                        copy(
                            searchResults = locations
                        )
                    }
                }.onFailure {
                    setState { copy(searchResults = emptyList()) }
                }
            }
        }
    }
}
