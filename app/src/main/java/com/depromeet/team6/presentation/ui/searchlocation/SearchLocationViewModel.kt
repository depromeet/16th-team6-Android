package com.depromeet.team6.presentation.ui.searchlocation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.model.SearchHistory
import com.depromeet.team6.domain.usecase.DeleteAllSearchHistoryUseCase
import com.depromeet.team6.domain.usecase.DeleteSearchHistoryUseCase
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetIsServiceRegionUseCase
import com.depromeet.team6.domain.usecase.GetLocationsUseCase
import com.depromeet.team6.domain.usecase.GetSearchHistoriesUseCase
import com.depromeet.team6.domain.usecase.PostSearchHistoriesUseCase
import com.depromeet.team6.presentation.ui.searchlocation.navigation.SearchLocationRoute.DEPARTURE_LOCATION
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getSearchHistoriesUseCase: GetSearchHistoriesUseCase,
    private val postSearchHistoriesUseCase: PostSearchHistoriesUseCase,
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryUseCase,
    private val deleteAllSearchHistoryUseCase: DeleteAllSearchHistoryUseCase,
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase,
    private val getIsServiceRegionUseCase: GetIsServiceRegionUseCase
) : BaseViewModel<SearchLocationContract.SearchLocationUiState, SearchLocationContract.SearchLocationSideEffect, SearchLocationContract.SearchLocationEvent>() {

    init {
        AmplitudeUtils.trackEvent(
            "출발지_수정_진입"
        )
        val departureLocationJSON: String? = savedStateHandle[DEPARTURE_LOCATION]
        if (departureLocationJSON != null) {
            val departureLocation = Gson().fromJson(departureLocationJSON, Address::class.java)
            setState {
                copy(searchQuery = departureLocation.name)
            }
            setEvent(
                SearchLocationContract.SearchLocationEvent.UpdateSearchQuery(
                    text = departureLocation.name,
                    lat = departureLocation.lat,
                    lon = departureLocation.lon
                )
            )
        }
    }
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
                copy()
            }

            is SearchLocationContract.SearchLocationEvent.ClearRecentSearches -> setState {
                copy(
                    recentSearches = emptyList()
                )
            }

            is SearchLocationContract.SearchLocationEvent.ClearText -> setState {
                copy(
                    searchQuery = "",
                    searchResults = emptyList()
                )
            }

            is SearchLocationContract.SearchLocationEvent.UpdateUserLocationSate -> setState { copy(userLocation = event.userLocation) }

            is SearchLocationContract.SearchLocationEvent.ChangeCurrentScreen -> setState {
                copy(currentScreen = event.screen)
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
        viewModelScope.launch {
            getSearchHistoriesUseCase(location.latitude, location.longitude)
                .onSuccess { searchHistories ->
                    setState {
                        copy(
                            recentSearches = searchHistories
                        )
                    }
                }
                .onFailure { exception ->
                    setState { copy(recentSearches = emptyList()) }
                    handleApiException(exception)
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
            deleteSearchHistoryUseCase(
                name = searchHistory.name,
                lat = searchHistory.lat,
                lon = searchHistory.lon,
                businessCategory = searchHistory.businessCategory,
                address = searchHistory.address
            )
                .onSuccess {
                    updateRecentSearches(location = LatLng(location.latitude, location.longitude))

                    setEvent(
                        SearchLocationContract.SearchLocationEvent.DeleteSearchHistory(
                            searchHistory = convertedSearchHistory
                        )
                    )
                }
                .onFailure { exception ->
                    Timber.e("deleteSearchHistory failure")
                    handleApiException(exception)
                }
        }
    }

    // 검색 내역 서버에 전송
    fun postSearchHistory(searchHistory: Location) {
        val convertedSearchHistory = SearchHistory(
            name = searchHistory.name,
            lat = searchHistory.lat,
            lon = searchHistory.lon,
            businessCategory = searchHistory.businessCategory,
            address = searchHistory.address
        )

        viewModelScope.launch {
            postSearchHistoriesUseCase(searchHistory = convertedSearchHistory)
                .onSuccess {
                    Timber.e("postSearchHistory Success")
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }

    // 최근 검색 내역 전체 삭제
    fun deleteAllSearchHistory() {
        viewModelScope.launch {
            deleteAllSearchHistoryUseCase().onSuccess {
                setEvent(SearchLocationContract.SearchLocationEvent.ClearRecentSearches)
            }
                .onFailure { exception ->
                    handleApiException(exception)
                }
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
                    if (locations.isEmpty()) {
                        setSideEffect(SearchLocationContract.SearchLocationSideEffect.ShowToastSideEffect("검색 결과가 없습니다."))
                    }
                    setState {
                        copy(
                            searchResults = locations
                        )
                    }
                }.onFailure { exception ->
                    setState { copy(searchResults = emptyList()) }
                    handleApiException(exception = exception)
                }
            }
        }
    }

    fun getCenterLocation(location: LatLng, onComplete: (Address) -> Unit = {}) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase(location.latitude, location.longitude)
                .onSuccess { address ->
                    val selectedAddress = address.copy(
                        name = "현재 위치",
                        lat = location.latitude,
                        lon = location.longitude
                    )
                    setState { copy(selectLocation = address) }
                    onComplete(address)
                }
                .onFailure {
                    handleApiException(it)
                }
//            getAddressFromCoordinatesUseCase(location.latitude, location.longitude)
//                .onSuccess { address ->
//                    setState { copy(selectLocation = address) }
//                    onComplete(address)
//                }
//                .onFailure {
//                }
        }
    }

    fun validateAndNavigateToCourseSearch(destinationLocation: Address) {
        val departureLocation = currentState.selectLocation
        val distance = calculateDistance(
            lat1 = departureLocation.lat,
            lon1 = departureLocation.lon,
            lat2 = destinationLocation.lat,
            lon2 = destinationLocation.lon
        )

        if (distance <= 800.0) {
            setSideEffect(SearchLocationContract.SearchLocationSideEffect.ShowTooCloseDialog)
            return
        }

        viewModelScope.launch {
            getIsServiceRegionUseCase(
                lat = departureLocation.lat,
                lon = departureLocation.lon
            ).onSuccess { isServiceRegion ->
                if (isServiceRegion) {
                    setSideEffect(
                        SearchLocationContract.SearchLocationSideEffect.NavigateToCourseSearch(
                            departureLocation = departureLocation
                        )
                    )
                } else {
                    setSideEffect(SearchLocationContract.SearchLocationSideEffect.ShowOutOfServiceRegionBottomSheet)
                }
            }.onFailure {
                setSideEffect(SearchLocationContract.SearchLocationSideEffect.ShowOutOfServiceRegionBottomSheet)
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(phi1) * Math.cos(phi2) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
        return 2 * r * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }
}
