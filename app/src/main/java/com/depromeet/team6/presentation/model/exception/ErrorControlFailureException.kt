package com.depromeet.team6.presentation.model.exception

import java.io.IOException

sealed class ErrorControlFailureException : IOException(){
    class ShowToastException(message: String) : ErrorControlFailureException()
    class NavigateAndShowToastException(message: String) : ErrorControlFailureException()
}