package com.rayo.digitaldiary.baseClasses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onesignal.OneSignal
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.ApiException
import com.rayo.digitaldiary.api.CommonResponse
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.database.DigitalDiaryDatabase
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.login.RegisterData
import com.rayo.digitaldiary.ui.login.ScanQRData

abstract class BaseViewModel : ViewModel() {
    val errorMessage = MutableLiveData<Event<String>>()
    val unauthorizedUserHandler = MutableLiveData<Event<String>>()
    val logoutSuccess = MutableLiveData<CommonResponse>()

    fun <T : CommonResponse> callAPI(callAPI: T?): T {
        when (callAPI?.success) {
            Constants.STATUS_SUCCESS -> {
                return callAPI
            }

            Constants.STATUS_UNAUTHORIZED -> {
                throw UnauthorizedUserException(callAPI.message ?: "Unauthorized user")
            }

            else -> {
                val message = StringBuilder()
                if (callAPI != null) {
                    val error = callAPI.message
                    if (error.isNullOrEmpty()) {
                        message.append("Something went wrong")
                    } else {
                        message.append(error)
                    }
                } else {
                    message.append("Something went wrong")
                }
                throw ApiException(message.toString())
            }
        }
    }

    fun saveUserDetails(preferenceManager: PreferenceManager, data: RegisterData) {
        with(preferenceManager) {
            savePref(Constants.prefToken, data.token)
            savePref(Constants.prefUserId, data.userId)
            savePref(Constants.prefEmail, data.email)
            savePref(Constants.prefContactNumber, data.contactNumber)
            savePref(Constants.prefCountryCode, data.countryCode)
            savePref(Constants.prefIsoCode, data.isoCode)
            savePref(Constants.prefBusinessName, data.businessName)
            savePref(Constants.prefDeviceName, data.deviceName)
            savePref(Constants.prefDeviceId, data.deviceId)
            savePref(Constants.prefCurrencySymbol, data.currencySymbol)
            savePref(Constants.prefCurrencyCountry, data.currencyCountryName)
            savePref(Constants.prefUserType, data.userType)
            savePref(Constants.prefLoginType, data.loginType)

            setOneSignalTags(preferenceManager)
        }
    }

    private fun setOneSignalTags(preferenceManager: PreferenceManager) {
        with(preferenceManager) {
            OneSignal.setExternalUserId(getPref(Constants.prefUserId, ""))
        }
    }

    fun saveCustomerOrStaffDetails(preferenceManager: PreferenceManager, data: ScanQRData) {
        with(preferenceManager) {
            savePref(Constants.prefToken, data.token)
            savePref(Constants.prefDeviceName, data.deviceName)
            savePref(Constants.prefDeviceId, data.deviceId)
            savePref(Constants.prefUserType, data.userType)
            savePref(Constants.prefCurrencySymbol, data.currencySymbol)
            savePref(Constants.prefCurrencyCountry, data.currencyCountryName)
            savePref(Constants.prefUserId, data.userId) // discuss for multiple customer login

            setOneSignalTags(preferenceManager)
            if (data.userType == Constants.USER_TYPE_STAFF) {
                savePref(Constants.prefUserId, data.userId)
                savePref(Constants.prefEmail, data.email)
                savePref(Constants.prefName, data.name)
                savePref(Constants.prefContactNumber, data.contactNumber)
                savePref(Constants.prefAddress, data.address)
                savePref(Constants.prefIsActive, data.active)
                savePref(Constants.prefOwnerEmail, data.ownerEmail)
                savePref(Constants.prefOwnerContactNumber, data.ownerContactNumber)
                savePref(Constants.prefBusinessName, data.businessName)
                savePref(Constants.prefCountryCode, data.countryCode)
            }
        }
    }

    fun callLogoutAPI(apiRepository: APIRepository, preferenceManager: PreferenceManager, isMultipleDeviceLogout: Boolean = false) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.logout(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        Utils.generateUUIDForDeviceId(preferenceManager),
                        preferenceManager.getPref(Constants.prefUserId, ""),
                        isMultipleDeviceLogout
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
                logoutSuccess.postValue(it)
            }
        })
    }

    fun clearAllTableFromDatabase(localDatabaseInstance: DigitalDiaryDatabase) {
        Coroutines.io {
            localDatabaseInstance.staffDao().deleteTable()
            localDatabaseInstance.customerDao().deleteTable()
            localDatabaseInstance.customerLoginDao().deleteTable()
            localDatabaseInstance.orderDao().deleteTable()
            localDatabaseInstance.productDao().deleteTable()
            localDatabaseInstance.paymentDao().deleteTable()
        }
    }
}