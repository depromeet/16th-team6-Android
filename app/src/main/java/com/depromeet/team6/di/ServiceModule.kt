package com.depromeet.team6.di

import com.depromeet.team6.data.dataremote.service.AlarmService
import com.depromeet.team6.data.dataremote.service.AuthService
import com.depromeet.team6.data.dataremote.service.DummyService
import com.depromeet.team6.data.dataremote.service.HomeService
import com.depromeet.team6.data.dataremote.service.LocationsService
import com.depromeet.team6.data.dataremote.service.TaxiCostService
import com.depromeet.team6.data.dataremote.service.TimeLeftService
import com.depromeet.team6.data.dataremote.service.TransitsService
import com.depromeet.team6.di.qualifier.Team6
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun providesService(@Team6 retrofit: Retrofit): DummyService =
        retrofit.create(DummyService::class.java)

    @Provides
    @Singleton
    fun providesAuthService(@Team6 retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun providesLocationsService(@Team6 retrofit: Retrofit): LocationsService =
        retrofit.create(LocationsService::class.java)

    @Provides
    @Singleton
    fun providesHomeService(@Team6 retrofit: Retrofit): HomeService =
        retrofit.create(HomeService::class.java)

    @Provides
    @Singleton
    fun providesAlarmService(@Team6 retrofit: Retrofit): AlarmService =
        retrofit.create(AlarmService::class.java)

    @Provides
    @Singleton
    fun providesLockService(@Team6 retrofit: Retrofit): TaxiCostService =
        retrofit.create(TaxiCostService::class.java)

    @Provides
    @Singleton
    fun providesTimeLeftService(@Team6 retrofit: Retrofit): TimeLeftService =
        retrofit.create(TimeLeftService::class.java)

    @Provides
    @Singleton
    fun providesCourseService(@Team6 retrofit: Retrofit): TransitsService =
        retrofit.create(TransitsService::class.java)
}
