package com.rayo.digitaldiary.ui.history

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.CustomerDao
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.ui.customer.CustomerData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 02/06/23.
 *
 * @author Mittal Varsani
 */
@HiltViewModel
class HistoryFilterViewModel @Inject constructor(private val customerDao: CustomerDao) :
    BaseViewModel() {

    val customerList = MutableLiveData<List<CustomerData>>()

    init {
        getAllCustomerFromDatabase()
    }

    private fun getAllCustomerFromDatabase() {
        Coroutines.ioThenMain({
            customerDao.getCustomers()
        }, {
            customerList.postValue(it)
        })
    }
}