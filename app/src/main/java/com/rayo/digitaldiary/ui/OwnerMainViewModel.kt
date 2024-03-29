package com.rayo.digitaldiary.ui

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.NetworkConnectionInterceptor
import com.rayo.digitaldiary.baseClasses.MainViewModel
import com.rayo.digitaldiary.database.*
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnerMainViewModel @Inject constructor(
    apiRepository: APIRepository,
    preferenceManager: PreferenceManager,
    private val customerDao: CustomerDao,
    private val staffDao: StaffDao,
    private val productDao: ProductDao,
    orderDao: OrderDao,
    transactionDao: PaymentDao,
    languageTranslationDao: LanguageTranslationDao,
    languagesDao: LanguagesDao,
    customerLoginDao: CustomerLoginDao,
    networkInterceptor: NetworkConnectionInterceptor
) : MainViewModel(
    apiRepository,
    preferenceManager,
    customerDao,
    staffDao,
    productDao,
    orderDao,
    transactionDao,
    languageTranslationDao,
    languagesDao,
    customerLoginDao,
    networkInterceptor
) {
    var totalStaff = 0
    var totalCustomer = 0
    var totalProduct = 0
    val staffAndCustomerCountSuccess = MutableLiveData<String>()

    init {
        if (networkInterceptor.isInternetAvailable()) {
            getAppSettings()
            syncInit(Constants.PRODUCT)
        }
        if (!networkInterceptor.isInternetAvailable()) {
            getCustomerAndStaffCount()
        }
    }

    fun getCustomerAndStaffCount() {
        Coroutines.ioThenMain({
            staffDao.getStaffActiveCount()
        }, { staffCount ->
            totalStaff = staffCount ?: 0
            Coroutines.ioThenMain({
                customerDao.getCustomerActiveCount()
            }, { customerCount ->
                totalCustomer = customerCount ?: 0
                Coroutines.ioThenMain({
                    productDao.getActiveProductCount()
                }, { productCount ->
                    totalProduct = productCount ?: 0
                    staffAndCustomerCountSuccess.postValue("Success")
                })
            })
        })
    }
}