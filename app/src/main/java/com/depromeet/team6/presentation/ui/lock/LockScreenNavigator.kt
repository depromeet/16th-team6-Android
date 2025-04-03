package com.depromeet.team6.presentation.ui.lock

import android.content.Context
import android.content.Intent
import javax.inject.Inject

class LockScreenNavigator @Inject constructor() {
    fun navigateToLockScreen(context: Context, taxiCost: Int) {
        context.startActivity(
            Intent(context, LockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

                putExtra(EXTRA_TAXI_COST, taxiCost)
            }
        )
    }

    companion object {
        const val EXTRA_TAXI_COST = "extra_taxi_cost"
    }
}
