package com.rayo.digitaldiary.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Mittal Varsani on 21/12/21.
 *
 * @author Mittal Varsani
 */
@InstallIn(SingletonComponent::class)
@Module
object PreferenceManager {
    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("Digital Diary", Context.MODE_PRIVATE)
    }

    @Provides
    fun providePrefEditor(preferences: SharedPreferences) : SharedPreferences.Editor {
        return preferences.edit()
    }
}
@EntryPoint
@InstallIn(SingletonComponent::class)
interface PreferenceEntryPoint {
    val preference: com.rayo.digitaldiary.helper.PreferenceManager
}