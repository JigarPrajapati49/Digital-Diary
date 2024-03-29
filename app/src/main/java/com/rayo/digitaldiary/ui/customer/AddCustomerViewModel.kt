package com.rayo.digitaldiary.ui.customer

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.ApiException
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.CustomerDao
import com.rayo.digitaldiary.database.CustomerLoginDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val apiRepository: APIRepository,
    private val customerDao: CustomerDao,
    private val customerLoginDao: CustomerLoginDao
) :
    BaseViewModel() {

    val addCustomerSuccess = MutableLiveData<AddCustomerResponse>()

    fun callAddOrUpdateCustomerAPI(customerData: CustomerData) {
        if (customerData.id.isEmpty()) {
            callAddCustomer(customerData)
        } else {
            callUpdateCustomerProfile(customerData)
        }
    }

    private fun callAddCustomer(
        customerData: CustomerData
    ) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.createOrUpdateCustomer(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        customerData
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
            if (it != null && it is AddCustomerResponse) {
                addCustomerToDatabase(it)
            }
        })
    }

    fun callUpdateCustomerProfile(
        customerData: CustomerData,
        isCustomerUpdateProfile: Boolean = false
    ) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.createOrUpdateCustomer(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault),
                        customerData
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
            if (it != null && it is AddCustomerResponse) {
                if (isCustomerUpdateProfile) {
                    updateCustomerProfileDataToDatabase(it)
                } else {
                    addCustomerToDatabase(it)
                }
            }
        })
    }

    private fun addCustomerToDatabase(addCustomerResponse: AddCustomerResponse) {
        Coroutines.ioThenMain({
            addCustomerResponse.customerData?.let { customerDao.insertCustomer(it) }
        }, {
            addCustomerSuccess.postValue(addCustomerResponse)
        })
    }

    private fun updateCustomerProfileDataToDatabase(addCustomerResponse: AddCustomerResponse) {
        Coroutines.ioThenMain({
            addCustomerResponse.customerData?.let {
                customerLoginDao.updateCustomerProfileData(
                    it.name,
                    it.contactNumber,
                    it.address,
                    it.id
                )
            }
        }, {
            addCustomerSuccess.postValue(addCustomerResponse)
        })
    }
}