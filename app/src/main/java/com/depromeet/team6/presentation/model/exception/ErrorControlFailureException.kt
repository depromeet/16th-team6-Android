package com.depromeet.team6.presentation.model.exception

import com.depromeet.team6.presentation.model.route.Route
import com.depromeet.team6.presentation.util.base.UiState
import java.io.IOException

sealed class ErrorControlFailureException : IOException() {
    class ShowToastException(val toastMessage: String) : ErrorControlFailureException()
    class NavigateAndShowToastException(val toastMessage: String, val route: Route) : ErrorControlFailureException()
    class SetUIStateException(val errorReduce: UiState.() -> UiState) : ErrorControlFailureException()
    class ReportDiscord : ErrorControlFailureException()
    class ReportDiscordWithToast(val toastMessage: String) : ErrorControlFailureException()
}
