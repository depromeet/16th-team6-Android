package com.depromeet.team6.presentation.util.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : UiState, SideEffect : UiSideEffect, Event : UiEvent>() : ViewModel() {

    private val firebaseCrashlytics = Firebase.crashlytics
    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    private val _uiState = MutableStateFlow<State>(initialState)
    val uiState: StateFlow<State>
        get() = _uiState.asStateFlow()
    val currentState: State
        get() = uiState.value

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event: SharedFlow<Event>
        get() = _event.asSharedFlow()

    private val _sideEffect: MutableSharedFlow<UiSideEffect> = MutableSharedFlow()
    val sideEffect: Flow<UiSideEffect>
        get() = _sideEffect.asSharedFlow()

    fun setState(reduce: State.() -> State) {
        _uiState.value = currentState.reduce()
    }

    open fun setEvent(event: Event) {
        dispatchEvent(event)
    }

    private fun dispatchEvent(event: Event) = viewModelScope.launch {
        handleEvent(event)
    }

    protected abstract suspend fun handleEvent(event: Event)

    @Suppress("UNCHECKED_CAST")
    fun handleApiException(
        exception: Throwable
    ) {
        if (exception is ErrorControlFailureException) {
            when (exception) {
                is ErrorControlFailureException.ShowToastException -> {
                    viewModelScope.launch {
                        _sideEffect.emit(ApiErrorSideEffect.ShowToastSideEffect(exception.toastMessage))
                    }
                }

                is ErrorControlFailureException.NavigateAndShowToastException -> {
                    viewModelScope.launch {
                        when (exception.route) {
                            Route.Home -> _sideEffect.emit(ApiErrorSideEffect.NavigateToHomeSideEffect)
                            Route.Login -> _sideEffect.emit(ApiErrorSideEffect.NavigateToLoginSideEffect)
                            Route.Back -> _sideEffect.emit(ApiErrorSideEffect.NavigateToBackSideEffect)
                        }
                        _sideEffect.emit(ApiErrorSideEffect.ShowToastSideEffect(exception.toastMessage))
                    }
                }
                is ErrorControlFailureException.SetUIStateException -> {
                    _uiState.value = exception.errorReduce(currentState) as State
                }
            }
        } else {
            logException(exception)
        }
    }

    private fun logException(exception: Throwable) {
        firebaseCrashlytics.recordException(exception)
    }

    fun setSideEffect(sideEffect: SideEffect) {
        viewModelScope.launch { _sideEffect.emit(sideEffect) }
    }
}
