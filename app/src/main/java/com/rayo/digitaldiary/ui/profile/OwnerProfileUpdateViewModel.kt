package com.rayo.digitaldiary.ui.profile

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
import com.rayo.digitaldiary.ui.login.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnerProfileUpdateViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {

    val updateProfileSuccess = MutableLiveData<RegisterResponse>()
    var lastSelectedCountryName = preferenceManager.getPref(Constants.prefCurrencyCountry, "")

    fun callUpdateProfileAPI(
        businessName: String,
        contactNumber: String,
        countryCode: String,
        isoCode: String,
        currencyCountryName: String,
        currencySymbol: String
    ) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.updateOwnerProfile(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ),
                        preferenceManager.getPref(Constants.prefUserId, ""),
                        businessName,
                        contactNumber, countryCode, isoCode,
                        currencySymbol,
                        currencyCountryName,
                        preferenceManager.getPref(Constants.prefDeviceId, ""),
                        preferenceManager.getPref(Constants.prefDeviceName, ""),
                        OneSignal.getDeviceState()?.userId ?: ""
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
                    updateProfileSuccess.postValue(it)
                }
            }
        })
    }
}