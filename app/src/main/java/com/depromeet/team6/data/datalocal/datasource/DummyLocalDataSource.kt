package com.depromeet.team6.data.datalocal.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyLocalDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var token: String
        get() = sharedPreferences.getString(TOKEN, INITIAL_VALUE).toString()
        set(value) = sharedPreferences.edit { putString(TOKEN, value) }

    var nickname: String
        get() = sharedPreferences.getString(NICKNAME, INITIAL_VALUE).toString()
        set(value) = sharedPreferences.edit { putString(NICKNAME, value) }

    fun clear() = sharedPreferences.edit { clear() }

    companion object {
        private const val PREFERENCES_NAME = "user_preferences"
        private const val TOKEN = "token"
        private const val NICKNAME = "nickname"
        private const val INITIAL_VALUE = ""
    }
}
