package com.rayo.digitaldiary

import android.app.Application
import com.onesignal.OneSignal
import com.rayo.digitaldiary.database.LanguageTranslationDao
import com.rayo.digitaldiary.helper.Constants.ONESIGNAL_APP_ID
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.notification.NotificationHelper
import com.rayo.digitaldiary.ui.CurrencyData
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


/**
 * Created by Mittal Varsani on 09/02/23.
 *
 * @author Mittal Varsani
 */

@HiltAndroidApp
class DigitalDiaryApplication: Application() {

    @Inject
    lateinit var languageTranslationDao: LanguageTranslationDao

    @Inject
    lateinit var preferenceManager: PreferenceManager

    var currentCustomerId: String = ""
    var isShowSyncProgress = false
    var isLogoutFromSettings = false

    companion object {
        lateinit var instance: DigitalDiaryApplication
        var currencyList: MutableList<CurrencyData> = ArrayList()
        var languageTranslationList: ArrayList<LanguageTranslationData> = ArrayList()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        }
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        OneSignal.setNotificationOpenedHandler(NotificationHelper(this,preferenceManager))

    }
}


