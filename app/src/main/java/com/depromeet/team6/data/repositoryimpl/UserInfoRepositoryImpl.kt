package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.datalocal.datasource.UserInfoLocalDataSource
import com.depromeet.team6.domain.repository.UserInfoRepository
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

    override fun setNickname(nickname: String) {
        userInfoLocalDataSource.nickname = nickname
    }

    override fun getNickname(): String = userInfoLocalDataSource.nickname

    override fun clear() {
        userInfoLocalDataSource.clear()
    }
}
