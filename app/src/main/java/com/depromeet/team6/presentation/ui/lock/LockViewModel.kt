package com.depromeet.team6.presentation.ui.lock

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor() : BaseViewModel<LockContract.LockUiState,
    LockContract.LockSideEffect,
    LockContract.LockEvent>() {

    override fun createInitialState(): LockContract.LockUiState = LockContract.LockUiState()

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            var currentTime = LockContract.LockUiState.INITIAL_TIME
            while (currentTime > 0) {
                delay(1000L)
                currentTime--
                setState { copy(timeLeft = currentTime) }
            }
            setState { copy(isTimerFinished = true) }
        }
    }

    override suspend fun handleEvent(event: LockContract.LockEvent) {
        when (event) {
            is LockContract.LockEvent.OnTimerFinish -> {
                setSideEffect(LockContract.LockSideEffect.CloseScreen)
            }
            is LockContract.LockEvent.OnCloseClick -> {
                if (currentState.isTimerFinished) {
                    setSideEffect(LockContract.LockSideEffect.CloseScreen)
                }
            }
        }
    }
}
