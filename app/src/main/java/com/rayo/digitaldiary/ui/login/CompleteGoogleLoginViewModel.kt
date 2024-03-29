package com.rayo.digitaldiary.ui.login

import androidx.lifecycle.MutableLiveData
import com.onesignal.OneSignal
import com.rayo.digitaldiary.R
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
 * Created by Mittal Varsani on 11/04/23.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class CompleteGoogleLoginViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager
) :
    BaseViewModel() {
    val registerSuccess = MutableLiveData<GoogleLoginResponse>()



    fun callCompleteGoogleRegistrationAPI(
        businessName: String,
        contactNumber: String,
        countryCode: String,
        isoCode: String
    ) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.completeLoginWithGoogle(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ),
                        businessName,
                        contactNumber,
                        countryCode,
                        isoCode,
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
            if (it != null && it is GoogleLoginResponse) {
                it.googleLoginData?.let { data ->
                    preferenceManager.savePref(
                        Constants.prefIsNewUser,
                        it.googleLoginData?.isNewUser
                    )
                    saveUserDetails(preferenceManager, data)
                    registerSuccess.postValue(it)
                }
            }
        })
    }
}