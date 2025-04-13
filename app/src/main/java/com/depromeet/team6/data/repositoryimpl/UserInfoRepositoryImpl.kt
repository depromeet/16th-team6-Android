package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.datalocal.datasource.UserInfoLocalDataSource
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor(
    private val userInfoLocalDataSource: UserInfoLocalDataSource
) : UserInfoRepository {
    override fun setAccessToken(accessToken: String) {
        userInfoLocalDataSource.accessToken = accessToken
    }

    override fun getAccessToken(): String = userInfoLocalDataSource.accessToken

    override fun setRefreshToken(refreshToken: String) {
        userInfoLocalDataSource.refreshToken = refreshToken
    }

    override fun getRefreshToken(): String = userInfoLocalDataSource.refreshToken

    override fun setFcmToken(fcmToken: String) {
        userInfoLocalDataSource.fcmToken = fcmToken
    }

    override fun getFcmToken(): String = userInfoLocalDataSource.fcmToken

    override fun setUserHome(userHomeLocation: LatLng) {
        userInfoLocalDataSource.userHome = userHomeLocation
    }

    override fun getUserHome(): LatLng = userInfoLocalDataSource.userHome

    override fun clear() {
        userInfoLocalDataSource.clear()
    }

    override fun saveAlarmSound(isSound: Boolean) {
        userInfoLocalDataSource.alarmSound = isSound
    }

    override fun getAlarmSound(): Boolean = userInfoLocalDataSource.alarmSound

}
