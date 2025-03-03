package com.depromeet.team6.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.usecase.DummyUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dummyUseCase: DummyUseCase
) : BaseViewModel<HomeContract.HomeUiState, HomeContract.HomeSideEffect, HomeContract.HomeEvent>() {
    override fun createInitialState(): HomeContract.HomeUiState = HomeContract.HomeUiState()

    override suspend fun handleEvent(event: HomeContract.HomeEvent) {
        when (event) {
            is HomeContract.HomeEvent.DummyEvent -> setState { copy(loadState = event.loadState) }
            is HomeContract.HomeEvent.UpdateAlarmRegistered -> setState { copy(isAlarmRegistered = event.isRegistered) }
        }
    }

    fun dummyFunction() {
        viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.DummyEvent(loadState = LoadState.Loading))
            dummyUseCase()
                .onSuccess {
                    setEvent(
                        HomeContract.HomeEvent.DummyEvent(loadState = LoadState.Success)
                    )
                }
                .onFailure {
                    setEvent(
                        HomeContract.HomeEvent.DummyEvent(loadState = LoadState.Error)
                    )
                }
        }
    }

    fun regsiterAlarm() {
        viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(true))
        }
    }
}
