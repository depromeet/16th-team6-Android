package com.depromeet.team6.di

import com.depromeet.team6.data.repositoryimpl.AlarmRepositoryImpl
import com.depromeet.team6.data.repositoryimpl.AuthRepositoryImpl
import com.depromeet.team6.data.repositoryimpl.DummyRepositoryImpl
import com.depromeet.team6.data.repositoryimpl.HomeRepositoryImpl
import com.depromeet.team6.data.repositoryimpl.LocationsRepositoryImpl
import com.depromeet.team6.data.repositoryimpl.TaxiCostRepositoryImpl
import com.depromeet.team6.data.repositoryimpl.TimeLeftRepositoryImpl
import com.depromeet.team6.data.repositoryimpl.TransitsRepositoryImpl
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.repository.AlarmRepository
import com.depromeet.team6.domain.repository.AuthRepository
import com.depromeet.team6.domain.repository.DummyRepository
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.repository.LocationsRepository
import com.depromeet.team6.domain.repository.TaxiCostRepository
import com.depromeet.team6.domain.repository.TimeLeftRepository
import com.depromeet.team6.domain.repository.TransitsRepository
import com.depromeet.team6.domain.repository.UserInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDummyRepository(dummyRepositoryImpl: DummyRepositoryImpl): DummyRepository

    @Binds
    @Singleton
    abstract fun bindsUserInfoRepository(userInfoRepositoryImpl: UserInfoRepositoryImpl): UserInfoRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationsRepositoryImpl: LocationsRepositoryImpl): LocationsRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository7(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(transitsRepositoryImpl: TransitsRepositoryImpl): TransitsRepository

    @Binds
    @Singleton
    abstract fun bindAlarmRepository(alarmRepositoryImpl: AlarmRepositoryImpl): AlarmRepository

    @Binds
    @Singleton
    abstract fun bindLockRepository(taxiCostRepositoryImpl: TaxiCostRepositoryImpl): TaxiCostRepository

    @Binds
    @Singleton
    abstract fun bindTimeLeftRepository(timeLeftRepository: TimeLeftRepositoryImpl): TimeLeftRepository
}
