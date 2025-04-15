package com.depromeet.team6.presentation.ui.lock

import android.content.Context
import android.content.Intent
import com.depromeet.team6.presentation.ui.main.MainActivity
import timber.log.Timber
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

    fun navigateToCourseSearch(context: Context, departurePoint: String, destinationPoint: String) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(EXTRA_NAVIGATE_TO_COURSE_SEARCH, true)
                putExtra(EXTRA_DEPARTURE_POINT, departurePoint)
                putExtra(EXTRA_DESTINATION_POINT, destinationPoint)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Error in navigateToCourseSearch")
            navigateToSpecificScreen(context)
        }
    }

    companion object {
        const val EXTRA_TAXI_COST = "extra_taxi_cost"
        const val EXTRA_NAVIGATE_TO_COURSE_SEARCH = "extra_navigate_to_course_search"
        const val EXTRA_DEPARTURE_POINT = "extra_departure_point"
        const val EXTRA_DESTINATION_POINT = "extra_destination_point"
        const val EXTRA_FROM_LOCK_SCREEN = "extra_from_lock_screen" // 상수 추가
    }
}
