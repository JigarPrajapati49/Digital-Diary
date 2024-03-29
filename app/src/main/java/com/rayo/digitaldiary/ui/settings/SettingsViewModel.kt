package com.rayo.digitaldiary.ui.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.DigitalDiaryApplication
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.ApiException
import com.rayo.digitaldiary.api.NetworkConnectionInterceptor
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.LanguagesDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.ui.LanguageData
import com.rayo.digitaldiary.ui.SettingsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager,
    private val languagesDao: LanguagesDao,
    networkInterceptor: NetworkConnectionInterceptor
) : BaseViewModel() {

    val languagesList = MutableLiveData<MutableList<LanguageData>>()

    init {
        if(networkInterceptor.isInternetAvailable()) {
            getAppSettings()
        }
    }

    fun getLanguagesListFromDB() {
        Coroutines.ioThenMain({
            languagesDao.getLanguagesListFromDB(1)
        }, {
            languagesList.postValue(it as MutableList<LanguageData>)
        })

    }

    private fun getAppSettings() {
        Coroutines.ioThenMain({
            try {
                apiRepository.getAppSettings(
                    preferenceManager.getPref(
                        Constants.prefLanguageCode,
                        Constants.languageCodeDefault
                    ),
                    preferenceManager.getPref(Constants.prefUserId, ""),
                    preferenceManager.getPref(Constants.prefDeviceId, "")
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, { it ->
            if (it != null && it is SettingsResponse) {
                it.settingsData?.let {
                    preferenceManager.savePref(
                        Constants.prefProductUnits,
                        it.productUnitsList.toTypedArray().contentToString().replace("[", "")
                            .replace("]", "")
                    )
                    insertLanguagesListInDB(it.languageList)
                    if (DigitalDiaryApplication.currencyList.isEmpty()) {
                        DigitalDiaryApplication.currencyList.addAll(it.currencyList)
                    }
                }
            }
        })
    }

    private fun insertLanguagesListInDB(list: List<LanguageData>) {
        Log.e("TAG", "insertLanguagesListInDB: ------------${list.size}")
        Coroutines.ioThenMain({
            languagesDao.insertLanguagesListInDB(list)
        }, {
            Log.e("TAG", "insertLanguagesListInDB: $it")
        })
    }
}