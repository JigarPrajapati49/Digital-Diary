package com.rayo.digitaldiary.ui.customer

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.database.CustomerLoginDao
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.ui.login.LoginViewModel
import com.rayo.digitaldiary.ui.login.ScanQRData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomerBusinessViewModel @Inject constructor(
    preferenceManager: PreferenceManager,
    apiRepository: APIRepository,
    private val customerLoginDao: CustomerLoginDao
) : LoginViewModel(apiRepository, preferenceManager, customerLoginDao) {

    val customerLoginDetailsList = MutableLiveData<List<ScanQRData>>()

    fun getCustomerLoginDetailsFromDB() {
        Coroutines.ioThenMain({
            customerLoginDao.getCustomerLoginDetails()
        }, {
            customerLoginDetailsList.postValue(it)
        })
    }

    fun getCurrentCustomerData(customerId: String): ScanQRData? {
        customerLoginDetailsList.value?.let {
            for(item in it) {
                if(item.userId == customerId) {
                    return item
                }
            }
        }
        return null
    }
}
