package com.depromeet.team6.domain.repository

interface UserInfoRepository {
    fun setAccessToken(accessToken: String)

    fun getAccessToken(): String

    fun setRefreshToken(refreshToken: String)

    fun getRefreshToken(): String

    fun setFcmToken(fcmToken: String)

    fun getFcmToken(): String

    fun clear()
}
