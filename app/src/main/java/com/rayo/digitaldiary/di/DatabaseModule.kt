package com.rayo.digitaldiary.di

import android.content.Context
import androidx.room.Room
import com.rayo.digitaldiary.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Mittal Varsani on 20/04/23.
 *
 * @author Mittal Varsani
 *
 */

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): DigitalDiaryDatabase {
        return Room.databaseBuilder(
            appContext,
            DigitalDiaryDatabase::class.java,
            "DigitalDiary"
        ).build()
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: DigitalDiaryDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideCustomerDao(appDatabase: DigitalDiaryDatabase): CustomerDao {
        return appDatabase.customerDao()
    }

    @Provides
    @Singleton
    fun provideStaffDao(appDatabase: DigitalDiaryDatabase): StaffDao {
        return appDatabase.staffDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(appDatabase: DigitalDiaryDatabase): OrderDao {
        return appDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun customerLoginDao(appDatabase: DigitalDiaryDatabase): CustomerLoginDao {
        return appDatabase.customerLoginDao()
    }

    @Provides
    @Singleton
    fun languageTranslation(appDatabase: DigitalDiaryDatabase): LanguageTranslationDao {
        return appDatabase.languageTranslationDao()
    }

    @Provides
    @Singleton
    fun languages(appDatabase: DigitalDiaryDatabase): LanguagesDao {
        return appDatabase.languagesDao()
    }

    @Provides
    @Singleton
    fun transactionDao(appDatabase: DigitalDiaryDatabase): PaymentDao {
        return appDatabase.paymentDao()
    }
}