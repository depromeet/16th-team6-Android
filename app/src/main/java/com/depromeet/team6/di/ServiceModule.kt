package com.depromeet.team6.di

import com.depromeet.team6.data.dataremote.service.AuthService
import com.depromeet.team6.data.dataremote.service.DummyService
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
}
