package com.depromeet.team6.data.datalocal.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.depromeet.team6.BuildConfig
import com.google.android.gms.maps.model.LatLng
import com.kakao.sdk.auth.Constants.ACCESS_TOKEN
import com.kakao.sdk.auth.Constants.REFRESH_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoLocalDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = if (BuildConfig.DEBUG) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    } else {
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var accessToken: String
        get() = getValue(ACCESS_TOKEN)
        set(value) = setValue(ACCESS_TOKEN, value)

    var refreshToken: String
        get() = getValue(REFRESH_TOKEN)
        set(value) = setValue(REFRESH_TOKEN, value)

    var fcmToken: String
        get() = getValue(FCM_TOKEN)
        set(value) = setValue(FCM_TOKEN, value)

    var alarmSound: Boolean
        get() = getBooleanValue(ALARM_SOUND_SETTING, true)
        set(value) = setBooleanValue(ALARM_SOUND_SETTING, value)

    var userHome: LatLng
        get() {
            val latLngStr = getValue(USER_HOME)
            val parts = latLngStr.split(",")
            return if (parts.size == 2) {
                LatLng(
                    parts[0].toDoubleOrNull() ?: 0.0,
                    parts[1].toDoubleOrNull() ?: 0.0
                )
            } else {
                LatLng(0.0, 0.0)
            }
        }
        set(value) {
            setValue(USER_HOME, "${value.latitude},${value.longitude}")
        }

    var userId: Int
        get() = getIntValue(USER_ID)
        set(value) = setIntValue(USER_ID, value)

    fun clear() {
        setValue(REFRESH_TOKEN, "")
        setValue(ACCESS_TOKEN, "")
        setValue(FCM_TOKEN, "")
        setIntValue(USER_ID, INITIAL_INT)
    }

    private fun getValue(key: String): String =
        sharedPreferences.getString(key, INITIAL_VALUE).orEmpty()

    private fun setValue(key: String, value: String) =
        sharedPreferences.edit { putString(key, value) }

    private fun getBooleanValue(key: String, defaultValue: Boolean = true): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    private fun setBooleanValue(key: String, value: Boolean) =
        sharedPreferences.edit { putBoolean(key, value) }

    private fun getIntValue(key: String, default: Int = INITIAL_INT): Int =
        sharedPreferences.getString(key, null)?.toIntOrNull() ?: default

    private fun setIntValue(key: String, value: Int) =
        sharedPreferences.edit { putString(key, value.toString()) }

    companion object {
        private const val FILE_NAME = "AtChaLocalDataSource"
        private const val INITIAL_VALUE = ""
        private const val FCM_TOKEN = "fcm_token"
        private const val USER_HOME = "user_home"
        private const val ALARM_SOUND_SETTING = "alarm_sound_setting"
        private const val USER_ID = "user_id"
        private const val INITIAL_INT = -1
    }
}
