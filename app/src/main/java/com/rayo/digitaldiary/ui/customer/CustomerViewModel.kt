package com.rayo.digitaldiary.ui.customer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.NetworkConnectionInterceptor
import com.rayo.digitaldiary.baseClasses.MainViewModel
import com.rayo.digitaldiary.database.*
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.ui.login.ScanQRData
import com.rayo.digitaldiary.ui.order.OrderData
import com.rayo.digitaldiary.ui.payment.PaymentData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class CustomerViewModel @Inject constructor(
    apiRepository: APIRepository,
    preferenceManager: PreferenceManager,
    private val customerDao: CustomerDao,
    staffDao: StaffDao,
    productDao: ProductDao,
    private val orderDao: OrderDao,
    private val paymentDao: PaymentDao,
    languageTranslationDao: LanguageTranslationDao,
    languagesDao: LanguagesDao,
    private val customerLoginDao: CustomerLoginDao,
    networkInterceptor: NetworkConnectionInterceptor,
) :
    MainViewModel(
        apiRepository,
        preferenceManager,
        customerDao,
        staffDao,
        productDao,
        orderDao,
        paymentDao,
        languageTranslationDao,
        languagesDao,
        customerLoginDao,
        networkInterceptor
    ) {
    val customerList = MutableLiveData<MutableList<CustomerData>>()
    var filterList: MutableList<CustomerData> = ArrayList()
    private val paymentList: MutableList<PaymentData> = ArrayList()
    private val orderList: MutableList<OrderData> = ArrayList()
    var filterCustomerList = Constants.CREATED_AT
    val userId = MutableLiveData<ScanQRData>()

    init {
        if (networkInterceptor.isInternetAvailable()) {
            syncInit(Constants.STAFF, preferenceManager.getPref(Constants.prefUserId, ""))
            getAppSettings()
        }
    }

    fun getAllPayment() {
        paymentList.clear()
        Coroutines.ioThenMain({
            paymentDao.getAllPayment()
        }, {
            it?.let { it1 -> paymentList.addAll(it1) }
            getAllOrders()
        })
    }

     fun getCustomerId(customerId: String) {
        Coroutines.ioThenMain({
            customerLoginDao.getCustomerId(customerId)
        }, {
            userId.postValue(it)
        })
    }

    private fun getAllOrders() {
        orderList.clear()
        Coroutines.ioThenMain({
            orderDao.getCompletedOrders()
        }, {
            it?.let { it1 -> orderList.addAll(it1) }
            getAllCustomerFromDatabase()
        })
    }

    private fun getAllCustomerFromDatabase() {
        Coroutines.ioThenMain({
            customerDao.getAllCustomers()
        }, {
            it?.let {
                for (item in it) {
                    var totalAmount = 0f
                    var paymentAmount = 0f
                    item.isCustomerSelected = 0
                    for (orderData in orderList) {
                        if (orderData.customerId == item.id) {
                            totalAmount += orderData.orderAmount.toFloat()
                        }
                    }
                    for (paymentData in paymentList) {
                        if (paymentData.customerId == item.id) {
                            paymentAmount += paymentData.amount.toFloat()
                        }
                    }
                    item.dueAmount = totalAmount - paymentAmount
                }
                filterList.clear()
                filterList.addAll(it)
                filterCustomer(filterCustomerList)
            }
        })
    }

    fun filterCustomer(status: String): List<CustomerData> {
        Log.e("TAG", "filterHistory: ---------------FilterList-SIZE -----------${filterList.size}")
        val list: List<CustomerData> =
            when (status) {
                Constants.DUE_AMOUNT_FIRST -> { // Due amount first
                    filterList.sortedByDescending { it.dueAmount }
                }
                Constants.DUE_AMOUNT_LAST -> { // Due amount last
                    filterList.sortedByDescending { it.dueAmount }.reversed()
                }
                Constants.A_TO_Z -> { // Name A to Z
                    filterList.sortedBy { it.name }
                }
                Constants.Z_TO_A -> { // Name Z to A
                    filterList.sortedBy { it.name }.reversed()
                }
                else -> {
                    filterList // created - at
                }
            }
        customerList.postValue(list.toMutableList())
        return list
    }
}