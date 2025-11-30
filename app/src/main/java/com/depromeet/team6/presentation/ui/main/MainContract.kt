package com.depromeet.team6.presentation.ui.main

import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LAT
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng

class MainContract {
    data class MainState(
        val splashState: LoadState = LoadState.Loading,
        val autoLogin: Boolean = false,
        val currentLocation: LatLng = LatLng(DEFAULT_LAT, DEFAULT_LNG)
    ) : UiState

    sealed interface MainSideEffect : UiSideEffect

    sealed class MainEvent : UiEvent
}
