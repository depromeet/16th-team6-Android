package com.depromeet.team6.presentation.model.exception

import com.depromeet.team6.presentation.model.route.Route
import java.io.IOException

sealed class ErrorControlFailureException : IOException() {
    class ShowToastException(val toastMessage: String) : ErrorControlFailureException()
    class NavigateAndShowToastException(val toastMessage: String, val route: Route) : ErrorControlFailureException()
    class SetUIStateException() : ErrorControlFailureException()
}
