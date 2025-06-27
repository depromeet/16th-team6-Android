package com.depromeet.team6.presentation.util.base

sealed interface ApiErrorSideEffect : UiSideEffect {
    data class ShowToastSideEffect(val toastMessage: String) : ApiErrorSideEffect
    data object NavigateToBackSideEffect : ApiErrorSideEffect
    data object NavigateToLoginSideEffect : ApiErrorSideEffect
    data object NavigateToHomeSideEffect : ApiErrorSideEffect
}
