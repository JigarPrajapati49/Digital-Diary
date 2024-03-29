package com.rayo.digitaldiary.di

import com.rayo.digitaldiary.api.APIInterface
import com.rayo.digitaldiary.api.NetworkConnectionInterceptor
import com.rayo.digitaldiary.api.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object APIModule {

    @Provides
    @Singleton
    fun provideAPIInterface(networkConnectionInterceptor: NetworkConnectionInterceptor): APIInterface {
        return RetrofitClient.webservice(networkConnectionInterceptor)
    }
}