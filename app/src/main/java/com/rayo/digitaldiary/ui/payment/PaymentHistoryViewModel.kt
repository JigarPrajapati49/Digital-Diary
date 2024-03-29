package com.rayo.digitaldiary.ui.payment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.CustomerDao
import com.rayo.digitaldiary.database.PaymentDao
import com.rayo.digitaldiary.database.StaffWithPayment
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.CustomerData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class PaymentHistoryViewModel @Inject constructor(
    private val transactionDao: PaymentDao,
    private val customerDao: CustomerDao
) :
    BaseViewModel() {

    var currentPage = 0
    var totalPage = 0
    var startPage = 0
    var endPage = Constants.PAYMENT_RECORD_PER_PAGE
    val paymentList = MutableLiveData<List<StaffWithPayment>>()
    val customerList = MutableLiveData<List<CustomerData>>()
    var isFilterApplied = false
    var filterStartDate = 0L
    var filterEndDate = 0L
    var filterSelectedCustomer: CustomerData = CustomerData().apply { name = Constants.ALL }
    var historyType: String = Constants.ALL

    fun getAllCustomerFromDatabase() {
        Coroutines.ioThenMain({
            customerDao.getActiveCustomers()
        }, {
            customerList.postValue(it)
        })
    }

    fun resetPagingData() {
        currentPage = 0
        startPage = 0
        endPage = Constants.PAYMENT_RECORD_PER_PAGE
    }

    fun getTransactionData(historyType: String, customerId: String, staffId: String) {
        Coroutines.ioThenMain({
            when(historyType) {
                Constants.CUSTOMER -> {
                    transactionDao.getCustomerPayment(customerId)
                }
                Constants.STAFF -> {
                    transactionDao.getStaffCollectedPayment(staffId)
                }
                else -> {
                    transactionDao.getAllPaymentByDescending()
                }
            }
        }, {
            it?.let {
                paymentList.postValue(it)
                val page = it.size / Constants.PAYMENT_RECORD_PER_PAGE.toDouble()
                totalPage = ceil(page).toInt()
            }
        })
    }

    fun getCurrentPageData(): List<StaffWithPayment> {
        endPage = paymentList.value?.size ?: 0
        if (((currentPage + 1) * Constants.PAYMENT_RECORD_PER_PAGE) < endPage) {
            endPage = ((currentPage + 1) * Constants.PAYMENT_RECORD_PER_PAGE)
        }
        startPage = currentPage * Constants.PAYMENT_RECORD_PER_PAGE
        val currentPageList: MutableList<StaffWithPayment> = ArrayList()
        paymentList.value?.let {
            for (i in startPage until endPage) {
                currentPageList.add(it[i])
            }
        }
        Log.d(
            "TAG",
            "getCurrentPageData: totalRecord ${paymentList.value?.size} totalPage $totalPage currentPage= $currentPage listSize ${currentPageList.size} startPage $startPage endPage $endPage"
        )
        currentPage++
        return currentPageList
    }

    fun filterHistory(): List<StaffWithPayment> {
        val list: MutableList<StaffWithPayment> = ArrayList()
        for (item in paymentList.value!!) {
            val dateInLong = Utils.getOrderDateLong(item.paymentData.createdAt)
            var calculatedEndDate = filterEndDate
            if(calculatedEndDate > 0) {
                val c = Calendar.getInstance()
                c.timeInMillis = calculatedEndDate
                c.add(Calendar.DATE, 1)
                calculatedEndDate = c.timeInMillis
            }
            Log.e(
                "staffHistory",
                "filterHistory: startDate $filterStartDate endDate $filterEndDate customerData $filterSelectedCustomer calculatedEndDate $calculatedEndDate"
            )
            if (filterStartDate > 0 && calculatedEndDate > 0 && filterSelectedCustomer.id.isNotEmpty() && dateInLong in filterStartDate..calculatedEndDate && filterSelectedCustomer.id == item.paymentData.customerId) {
                list.add(item)
            } else if (filterStartDate == 0L && calculatedEndDate == 0L && filterSelectedCustomer.id.isNotEmpty() && item.paymentData.customerId == filterSelectedCustomer.id) {
                list.add(item)
            } else if(filterStartDate > 0 && calculatedEndDate > 0 && dateInLong in filterStartDate..calculatedEndDate && filterSelectedCustomer.id.isEmpty()){
                list.add(item)
            }
        }
        return list
    }
}