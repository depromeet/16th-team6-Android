package com.depromeet.team6.data.datalocal.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeInfoLocalDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

    var isAlarmRegistered: Boolean
        get() = getBooleanValue(ALARM_REGISTERED, false)
        set(value) = setBooleanValue(ALARM_REGISTERED, value)

    var lastRouteId: String
        get() = getValue(LAST_ROUTE_ID)
        set(value) = setValue(LAST_ROUTE_ID, value)

    var departurePoint: Address?
        get() {
            val json = getValue(DEPARTURE_POINT)
            return if (json.isNotEmpty()) {
                try {
                    gson.fromJson(json, Address::class.java)
                } catch (e: Exception) {
                    Timber.e("DeparturePoint 불러오기 실패: ${e.message}")
                    null
                }
            } else {
                null
            }
        }
        set(value) {
            val json = if (value != null) {
                try {
                    gson.toJson(value)
                } catch (e: Exception) {
                    Timber.e("DeparturePoint 저장 실패: ${e.message}")
                    ""
                }
            } else {
                ""
            }
            setValue(DEPARTURE_POINT, json)
        }

    var lastCourseInfo: CourseInfo?
        get() {
            val json = getValue(LAST_COURSE_INFO)
            return if (json.isNotEmpty()) {
                try {
                    gson.fromJson(json, CourseInfo::class.java)
                } catch (e: Exception) {
                    Timber.e("CourseInfo 불러오기 실패: ${e.message}")
                    null
                }
            } else {
                null
            }
        }
        set(value) {
            val json = if (value != null) {
                try {
                    gson.toJson(value)
                } catch (e: Exception) {
                    Timber.e("CourseInfo 저장 실패: ${e.message}")
                    ""
                }
            } else {
                ""
            }
            setValue(LAST_COURSE_INFO, json)
        }

    var userDeparture: Boolean
        get() = getBooleanValue(USER_DEPARTURE, false)
        set(value) = setBooleanValue(USER_DEPARTURE, value)

    var destinationPoint: String
        get() = getValue(DESTINATION_POINT)
        set(value) = setValue(DESTINATION_POINT, value)

    var busArrivalParameter: BusArrivalParameter?
        get() {
            val json = getValue(BUS_ARRIVAL_PARAMETER)
            return if (json.isNotEmpty()) {
                try {
                    gson.fromJson(json, BusArrivalParameter::class.java)
                } catch (e: Exception) {
                    Timber.e("BusArrivalParameter 불러오기 실패: ${e.message}")
                    null
                }
            } else {
                null
            }
        }
        set(value) {
            val json = if (value != null) {
                try {
                    gson.toJson(value)
                } catch (e: Exception) {
                    Timber.e("BusArrivalParameter 저장 실패: ${e.message}")
                    ""
                }
            } else {
                ""
            }
            setValue(BUS_ARRIVAL_PARAMETER, json)
        }

    fun clearAlarmData() {
        sharedPreferences.edit {
            remove(DEPARTURE_POINT)
            remove(LAST_COURSE_INFO)
            remove(LAST_ROUTE_ID)
            remove(ALARM_REGISTERED)
            remove(USER_DEPARTURE)
            remove(BUS_ARRIVAL_PARAMETER)
        }
    }

    fun clearUserDeparture() {
        setBooleanValue(USER_DEPARTURE, false)
    }

    private fun getValue(key: String): String =
        sharedPreferences.getString(key, INITIAL_VALUE).orEmpty()

    private fun setValue(key: String, value: String) =
        sharedPreferences.edit { putString(key, value) }

    private fun getBooleanValue(key: String, defaultValue: Boolean = false): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    private fun setBooleanValue(key: String, value: Boolean) =
        sharedPreferences.edit { putBoolean(key, value) }

    companion object {
        private const val FILE_NAME = "MyPreferences"
        private const val INITIAL_VALUE = ""
        private const val ALARM_REGISTERED = "alarmRegistered"
        private const val LAST_ROUTE_ID = "lastRouteId"
        private const val DEPARTURE_POINT = "departurePoint"
        private const val DESTINATION_POINT = "destinationPoint"
        private const val LAST_COURSE_INFO = "lastCourseInfo"
        private const val USER_DEPARTURE = "userDeparture"
        private const val BUS_ARRIVAL_PARAMETER = "busArrivalParameter"
    }
}
