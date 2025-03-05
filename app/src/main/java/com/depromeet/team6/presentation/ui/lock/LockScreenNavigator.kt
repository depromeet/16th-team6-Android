package com.depromeet.team6.presentation.ui.lock

import android.content.Context
import android.content.Intent
import javax.inject.Inject

class LockScreenNavigator @Inject constructor() {
    fun navigateToLockScreen(context: Context) {
        context.startActivity(
            Intent(context, LockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }
        )
    }
}