package com.rayo.digitaldiary.ui.login

import androidx.lifecycle.MutableLiveData
import com.onesignal.OneSignal
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.ApiException
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 29/04/22.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager
) :
    BaseViewModel() {
    val registerSuccess = MutableLiveData<RegisterResponse>()

    fun callRegisterAPI(
        email: String,
        password: String,
        countryCode:String,
        isoCode:String,
        contactNumber: String,
        businessName: String,
    ) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.register(
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ),
                        email,
                        password,
                        contactNumber, countryCode, isoCode,
                        businessName,
                        Utils.generateUUIDForDeviceId(preferenceManager),
                        Utils.getDeviceName(), OneSignal.getDeviceState()?.userId ?: ""
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is RegisterResponse) {
                it.data?.let { data ->
                    saveUserDetails(preferenceManager, data)
                    registerSuccess.postValue(it)
                }
            }
        })
    }
}