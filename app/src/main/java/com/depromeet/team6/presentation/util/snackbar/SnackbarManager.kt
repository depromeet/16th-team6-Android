package com.depromeet.team6.presentation.util.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

object SnackbarManager {
    private var _snackbarHostState: SnackbarHostState? = null
    private val snackbarHostState: SnackbarHostState
        get() = _snackbarHostState ?: error("SnackbarHostState not initialized")

    private var isShowing = false

    fun initialize(hostState: SnackbarHostState) {
        _snackbarHostState = hostState
    }

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ): SnackbarResult? {
        if (isShowing) return null

        isShowing = true
        return try {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        } finally {
            isShowing = false
        }
    }
}
