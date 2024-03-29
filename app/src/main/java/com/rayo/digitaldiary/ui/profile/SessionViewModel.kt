package com.rayo.digitaldiary.ui.profile

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.CommonResponse
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.DigitalDiaryDatabase
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionVewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager,
    private val localDatabaseInstance: DigitalDiaryDatabase,
) : BaseViewModel() {

    val sessionList = MutableLiveData<List<SessionData>>()
    val sessionLogoutSuccess = MutableLiveData<String>()
    val logoutAllDeviceSuccess = MutableLiveData<String>()

    fun getSessionData(userId: String) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.getSessionDetail(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(
                            Constants.prefLanguageCode, Constants.languageCodeDefault
                        ),
                        userId
                    )
                )
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            } catch (e: Exception) {
                errorMessage.postValue(Event(e.localizedMessage))
            }
        }, {
            if (it != null && it is SessionDataResponse) {
                sessionList.postValue(it.sessionData)
            }
        })
    }

    fun logout(sessionData: SessionData) {
        if (sessionData.deviceId == preferenceManager.getPref(Constants.prefDeviceId,"")) {
            // call common logout api if call from current device
            callLogoutAPI(apiRepository, preferenceManager)
            return
        }
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.logout(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(
                            Constants.prefLanguageCode, Constants.languageCodeDefault
                        ),
                        sessionData.deviceId,
                        sessionData.userId
                    )
                )
            } catch (e: Exception) {
                errorMessage.postValue(Event(e.localizedMessage))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is CommonResponse) {
                sessionLogoutSuccess.postValue(sessionData.deviceId)
            }
        })
    }

    fun logoutAllDevice(userId: String) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.logoutAllDevice(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(
                            Constants.prefLanguageCode, Constants.languageCodeDefault
                        ),
                        userId
                    )
                )
            } catch (e: Exception) {
                errorMessage.postValue(Event(e.localizedMessage))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null) {
                if (userId == preferenceManager.getPref(Constants.prefUserId, "")) {
                    logoutSuccess.postValue(it as CommonResponse?)
                } else {
                    logoutAllDeviceSuccess.postValue(it.toString())
                }
            }
        })
    }
}