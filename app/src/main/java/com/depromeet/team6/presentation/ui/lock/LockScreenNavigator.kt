package com.depromeet.team6.presentation.ui.lock

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

    fun navigateToCourseSearch(
        context: Context,
        departurePoint: String,
        destinationPoint: String,
        fromLockScreen: Boolean = false
    ) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(EXTRA_NAVIGATE_TO_COURSE_SEARCH, true)
                putExtra(EXTRA_DEPARTURE_POINT, departurePoint)
                putExtra(EXTRA_DESTINATION_POINT, destinationPoint)
                putExtra(EXTRA_FROM_LOCK_SCREEN, fromLockScreen)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Error in navigateToCourseSearch")
            navigateToSpecificScreen(context)
        }
    }

    fun navigateToCourseSearchFromLockScreen(context: Context, sharedPreferences: SharedPreferences) {
        try {
            // "출발하기"를 누른 상태를 표시
            val editor = sharedPreferences.edit()
            editor.putBoolean("fromLockScreenDeparture", true)
            editor.apply()

            Timber.d("LockScreenNavigator navigateToCourseSearchFromLockScreen: navigating to home with departure flag")

            // 홈으로 이동
            navigateToSpecificScreen(context)
        } catch (e: Exception) {
            Timber.e(e, "Error in navigateToCourseSearchFromLockScreen, navigating to home")
            navigateToSpecificScreen(context)
        }
    }

    fun navigateToItineraryFromLockScreen(context: Context) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(EXTRA_NAVIGATE_TO_ITINERARY, true)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Error in navigateToItineraryFromLockScreen, navigating to home")
            navigateToSpecificScreen(context)
        }
    }

    companion object {
        const val EXTRA_TAXI_COST = "extra_taxi_cost"
        const val EXTRA_NAVIGATE_TO_COURSE_SEARCH = "extra_navigate_to_course_search"
        const val EXTRA_NAVIGATE_TO_ITINERARY = "extra_navigate_to_itinerary"
        const val EXTRA_DEPARTURE_POINT = "extra_departure_point"
        const val EXTRA_DESTINATION_POINT = "extra_destination_point"
        const val EXTRA_FROM_LOCK_SCREEN = "extra_from_lock_screen"
        const val EXTRA_ITINERARY_INFO = "extra_itinerary_info"
    }
}
