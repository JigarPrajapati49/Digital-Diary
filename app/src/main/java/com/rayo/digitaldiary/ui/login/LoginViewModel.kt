package com.rayo.digitaldiary.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.onesignal.OneSignal
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.ApiException
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.CustomerLoginDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 29/04/22.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
open class LoginViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager,
    private val customerLoginDao: CustomerLoginDao
) :
    BaseViewModel() {
    val loginSuccess = MutableLiveData<RegisterResponse>()
    val loginWithGoogleSuccess = MutableLiveData<Event<GoogleLoginResponse>>()
    val scanQRSuccess = MutableLiveData<Event<ScanQRResponse>>()

    fun callLoginAPI(
        email: String,
        password: String
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

    fun callLoginWithGoogleAPI(googleId: String, email: String) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.loginWithGoogle(
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ),
                        googleId,
                        email,
                        Utils.generateUUIDForDeviceId(preferenceManager),
                        Utils.getDeviceName(),
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
            if (it != null && it is GoogleLoginResponse) {
                if (it.googleLoginData?.isNewUser == 1) {
                    with(preferenceManager) {
                        savePref(Constants.prefIsNewUser, it.googleLoginData?.isNewUser)
                        savePref(Constants.prefToken, it.googleLoginData?.token)
                    }
                } else {
                    saveUserDetails(preferenceManager, it.googleLoginData as RegisterData)
                }
                loginWithGoogleSuccess.postValue(Event(it))
            }
        })
    }

    fun callScanQRCodeAPI(
        jsonString: String,
        isCustomerAlreadyLogin: Boolean = false
    ) {
        try {
            val userId = getUserIdFromJson(jsonString)
            val expiredTime = getExpireTimeFromJson(jsonString)
            Coroutines.ioThenMain({
                try {
                    callAPI(
                        apiRepository.scanQRCode(
                            preferenceManager.getPref(
                                Constants.prefLanguageCode,
                                Constants.languageCodeDefault
                            ),
                            userId, expiredTime,
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
                if (it != null && it is ScanQRResponse) {
                    if (it.scanQRData?.userType == Constants.USER_TYPE_CUSTOMER) {
                        preferenceManager.savePref(Constants.prefBusinessName, it.scanQRData.businessName)
                        preferenceManager.savePref(Constants.prefOwnerEmail, it.scanQRData.ownerEmail)
                        preferenceManager.savePref(Constants.prefOwnerContactNumber, it.scanQRData.ownerContactNumber)
                        addCustomerLoginDetailsToDatabase(it)
                    }
                    if (isCustomerAlreadyLogin && it.scanQRData?.userType != Constants.USER_TYPE_CUSTOMER) {
                        errorMessage.postValue(Event("Only Customer login allow"))
                    } else {
                        it.scanQRData?.let { it1 ->
                            saveCustomerOrStaffDetails(preferenceManager, it1)
                        }
                        scanQRSuccess.postValue(Event(it))
                    }
                }
            })
        } catch (e: Exception) {
            errorMessage.postValue(Event(Utils.getTranslatedValue("invalid_qr_code")))
        }
    }

    private fun getUserIdFromJson(jsonString: String): String {
        val jsonObject = JSONObject(jsonString)
        return jsonObject.getString(Constants.userId)
    }

    private fun getExpireTimeFromJson(jsonString: String): String {
        val jsonObject = JSONObject(jsonString)
        return jsonObject.getString(Constants.expireTime)
    }

    private fun addCustomerLoginDetailsToDatabase(scanQRResponse: ScanQRResponse) {
        Coroutines.io {
            scanQRResponse.scanQRData.let {
                it?.let { it1 ->
                    customerLoginDao.insertCustomerLoginDetails(it1)
                }
            }
        }
    }
}