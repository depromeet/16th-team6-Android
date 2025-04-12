package com.depromeet.team6.presentation.ui.lock

import android.content.Context
import android.content.Intent
import com.depromeet.team6.presentation.ui.main.MainActivity
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

    fun navigateToSpecificScreen(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    companion object {
        const val EXTRA_TAXI_COST = "extra_taxi_cost"
    }
}
