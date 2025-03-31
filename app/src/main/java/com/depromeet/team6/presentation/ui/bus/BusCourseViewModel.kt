package com.depromeet.team6.presentation.ui.bus

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.usecase.GetBusArrivalUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusCourseViewModel @Inject constructor(
    private val getBusArrivalUseCase: GetBusArrivalUseCase,
    ) : BaseViewModel<BusCourseContract.BusCourseUiState, BusCourseContract.BusCourseSideEffect, BusCourseContract.BusCourseEvent>() {
    override fun createInitialState(): BusCourseContract.BusCourseUiState =
        BusCourseContract.BusCourseUiState()

    override suspend fun handleEvent(event: BusCourseContract.BusCourseEvent) {
        when (event) {
            is BusCourseContract.BusCourseEvent.SetScreenLoadState -> setState { copy(loadState = event.loadState)}
        }
    }

    fun getBusArrival(routeName: String, stationName: String, lat: Double, lon: Double) {
        setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Loading))
        viewModelScope.launch {
            getBusArrivalUseCase(
                routeName = routeName,
                stationName = stationName,
                lat = lat,
                lon = lon
            ).onSuccess { busArrival ->
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Success))
                busArrival

            }.onFailure {
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Error))
            }
        }


    }
}