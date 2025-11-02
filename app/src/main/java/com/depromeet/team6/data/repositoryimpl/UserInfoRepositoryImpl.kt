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

    override fun getUserID(): Int = userInfoLocalDataSource.userId

    override fun setUserId(userId: Int) {
        userInfoLocalDataSource.userId = userId
    }

    override fun clear() {
        userInfoLocalDataSource.clear()
    }

    override fun saveIsAlarmSound(isSound: Boolean) {
        userInfoLocalDataSource.isAlarmSound = isSound
    }

    override fun getIsAlarmSound(): Boolean = userInfoLocalDataSource.isAlarmSound

    override fun saveIsAlarmVibrate(isVibrate: Boolean) {
        userInfoLocalDataSource.isAlarmVibrate = isVibrate
    }

    override fun getIsAlarmVibrate(): Boolean = userInfoLocalDataSource.isAlarmVibrate

    override fun saveAlarmVolume(volume: Int) {
        userInfoLocalDataSource.alarmVolume = volume
    }

    override fun getAlarmVolume(): Int = userInfoLocalDataSource.alarmVolume
}
