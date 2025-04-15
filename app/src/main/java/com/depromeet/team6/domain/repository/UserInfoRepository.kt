package com.depromeet.team6.domain.repository

import com.google.android.gms.maps.model.LatLng

interface UserInfoRepository {
    fun setAccessToken(accessToken: String)

    fun getAccessToken(): String

    fun setRefreshToken(refreshToken: String)

    fun getRefreshToken(): String

    fun setFcmToken(fcmToken: String)

    fun getFcmToken(): String

    fun setUserHome(userHomeLocation: LatLng)

    fun getUserHome(): LatLng

    fun getUserID(): Int

    fun setUserId(userId: Int)

    fun clear()
}
