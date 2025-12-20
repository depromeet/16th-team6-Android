package com.depromeet.team6.data.datalocal.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmFiredLocalDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var push5fired: Boolean
        get() = getValue(PUSH_5_MINUTE)
        set(value) = setValue(PUSH_5_MINUTE, value)

    var push10fired: Boolean
        get() = getValue(PUSH_10_MINUTE)
        set(value) = setValue(PUSH_10_MINUTE, value)

    var push15fired: Boolean
        get() = getValue(PUSH_15_MINUTE)
        set(value) = setValue(PUSH_15_MINUTE, value)

    var push30fired: Boolean
        get() = getValue(PUSH_30_MINUTE)
        set(value) = setValue(PUSH_30_MINUTE, value)

    var push60fired: Boolean
        get() = getValue(PUSH_60_MINUTE)
        set(value) = setValue(PUSH_60_MINUTE, value)

    /** 전체 fired 플래그 false 로 초기화 */
    fun resetAll() {
        sharedPreferences.edit {
            putBoolean(PUSH_5_MINUTE, false)
            putBoolean(PUSH_10_MINUTE, false)
            putBoolean(PUSH_15_MINUTE, false)
            putBoolean(PUSH_30_MINUTE, false)
            putBoolean(PUSH_60_MINUTE, false)
        }
    }

    private fun getValue(key: String): Boolean =
        sharedPreferences.getBoolean(key, false)

    private fun setValue(key: String, value: Boolean) =
        sharedPreferences.edit { putBoolean(key, value) }
    companion object {
        const val FILE_NAME = "AlarmFiredLocalDataSource"
        const val PUSH_5_MINUTE = "push_5_minute"
        const val PUSH_10_MINUTE = "push_10_minute"
        const val PUSH_15_MINUTE = "push_15_minute"
        const val PUSH_30_MINUTE = "push_30_minute"
        const val PUSH_60_MINUTE = "push_60_minute"
    }
}
