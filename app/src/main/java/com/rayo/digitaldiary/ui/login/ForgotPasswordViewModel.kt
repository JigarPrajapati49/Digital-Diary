package com.rayo.digitaldiary.ui.login

import androidx.lifecycle.MutableLiveData
import com.onesignal.OneSignal
import com.rayo.digitaldiary.api.*
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 06/06/22.
 *
 * @author Mittal Varsani
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {
    val forgotPasswordSuccess = MutableLiveData<Event<CommonResponse>>()
    val verifyOtpSuccess = MutableLiveData<CommonResponse>()
    val resetPasswordSuccess = MutableLiveData<CommonResponse>()
    val loginSuccess = MutableLiveData<RegisterResponse>()

    fun callForgotPasswordAPI(email: String) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.forgotPassword(
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ),
                        email
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
            if (it != null && it is CommonResponse) {
                forgotPasswordSuccess.postValue(Event(it))
            }
        })
    }

    fun callVerifyOtp(email: String, otp: String) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.verifyOtp(
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ), email, otp
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
            if (it != null && it is CommonResponse) {
                verifyOtpSuccess.postValue(it)
            }
        })
    }

    fun callResetPassword(email: String, password: String) {
        Coroutines.ioThenMain({
            try {
                callAPI(apiRepository.resetPassword(preferenceManager.getPref(Constants.prefLanguageCode,Constants.languageCodeDefault),email, password))
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is CommonResponse) {
                resetPasswordSuccess.postValue(it)
            }
        })
    }

    fun callLoginAPI(
        email: String,
        password: String,
    ) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.login(
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ),
                        email,
                        password,
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
                    loginSuccess.postValue(it)
                }
            }
        })
    }
}