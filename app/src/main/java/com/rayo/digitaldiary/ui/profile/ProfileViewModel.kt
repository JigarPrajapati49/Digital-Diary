package com.rayo.digitaldiary.ui.profile

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.CustomerLoginDao
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.ui.login.ScanQRData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val customerLoginDao: CustomerLoginDao
) :
    BaseViewModel() {

    val currentCustomerData = MutableLiveData<ScanQRData>()

    fun getCurrentCustomerData(customerId: String) {
        Coroutines.ioThenMain({
            customerLoginDao.getCurrentCustomerData(customerId)
        }, {
            currentCustomerData.postValue(it)
        })
    }
}