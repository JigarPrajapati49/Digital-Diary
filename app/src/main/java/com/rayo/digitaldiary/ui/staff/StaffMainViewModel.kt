package com.rayo.digitaldiary.ui.staff

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.NetworkConnectionInterceptor
import com.rayo.digitaldiary.baseClasses.MainViewModel
import com.rayo.digitaldiary.database.CustomerDao
import com.rayo.digitaldiary.database.CustomerLoginDao
import com.rayo.digitaldiary.database.LanguageTranslationDao
import com.rayo.digitaldiary.database.LanguagesDao
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.database.ProductDao
import com.rayo.digitaldiary.database.StaffDao
import com.rayo.digitaldiary.database.PaymentDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StaffMainViewModel @Inject constructor(
    apiRepository: APIRepository,
    preferenceManager: PreferenceManager,
    private val customerDao: CustomerDao,
    staffDao: StaffDao,
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
    val customerCountSuccess = MutableLiveData<String>()
    val productCountSuccess = MutableLiveData<String>()
    var totalCustomer = 0
    var totalProduct = 0

    init {
        if (networkInterceptor.isInternetAvailable()) {
            getAppSettings()
            syncInit(Constants.PRODUCT)
        }
        if (!networkInterceptor.isInternetAvailable()) {
            getCustomerCount()
        }
    }

    fun getCustomerCount() {
        Coroutines.ioThenMain({
            productDao.getActiveProductCount()
        },{
            productCount ->
            totalProduct = productCount ?: 0
            productCountSuccess.postValue("Success")

        })

        Coroutines.ioThenMain({
            customerDao.getCustomerActiveCount()
        }, { customerCount ->
            totalCustomer = customerCount ?: 0
            customerCountSuccess.postValue("Success")
        })
    }
}

