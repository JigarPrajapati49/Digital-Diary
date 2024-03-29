package com.rayo.digitaldiary.ui.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.database.PaymentDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class CustomerHistoryViewModel @Inject constructor(
    private val orderDao: OrderDao,
    private val paymentDao: PaymentDao) :
    BaseViewModel() {

    private val calendar = Calendar.getInstance()
    var month = calendar.get(Calendar.MONTH)
    var year = calendar.get(Calendar.YEAR)
    private var currentMonth: Int = 0
    private var currentYear: Int = 0
    private val d = Date()
    internal var customerId = ""
    var filterOrderStatus = Constants.ALL
    val orderHistoryList = MutableLiveData<List<CustomerWithHistory>>()
    private var filterList: MutableList<CustomerWithHistory> = ArrayList()
    var totalDueAmount = 0f
    var isPaymentDataFetched = false
    var scrollPosition = -1

    init {
        currentMonth = Utils.getStringFromDate("MM", d).toInt()
        currentYear = Utils.getStringFromDate("yyyy", d).toInt()
    }

    suspend fun getAllPayment(month: String, year: String) {
        var totalPayment = 0f
        var totalAmount = 0f
        Coroutines.ioThenMain({
            paymentDao.getPaymentByCustomerId(customerId)
        }, { paymentList ->
            isPaymentDataFetched = true
            if (paymentList != null) {
                for(item in paymentList) {
                    totalPayment += item.amount.toFloat()
                }
            }
            Coroutines.ioThenMain({
                orderDao.getCustomerAllOrder(customerId)
            }, { allOrderList ->
                if (allOrderList != null) {
                    for(item in allOrderList) {
                        totalAmount += item.orderAmount.toFloat()
                    }
                }
                totalDueAmount = totalAmount - totalPayment
                getCustomerOrderHistoryFromDB(month, year)
            })
        })
    }


    fun nextMonthClick(): Boolean {
        month++
        if (month == 12) {
            month = 0
            year++
            calendar.roll(Calendar.YEAR, true)
        }
        calendar.roll(Calendar.MONTH, true)
        return if (currentYear == year) {
            (month + 1) < currentMonth
        } else {
            true
        }
    }

    fun previousMonthClick(): Boolean {
        month--
        if (month == -1) {
            month = 11
            year--
            calendar.roll(Calendar.YEAR, false)
        }
        calendar.roll(Calendar.MONTH, false)
        return if (currentYear == year) {
            (month + 1) < currentMonth
        } else {
            true
        }
    }

    fun getMonthText(): String {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
            ?.toString() + " " +
                calendar.get(Calendar.YEAR).toString()
    }

    fun getCustomerOrderHistoryFromDB(
        month: String,
        year: String
    ) {
        Coroutines.ioThenMain({
            orderDao.getMonthDetailsHistory(customerId, month, year)
        }, {
            if (it != null) {
                filterList.clear()
                filterList.addAll(it)
                filterHistory(getOrderStatusInInt(filterOrderStatus), customerId)
                Log.e("TAG", "getCustomerOrderHistoryFromDB: ${it.size}")
            }
        })
    }

    fun filterHistory(status: Int, customerId: String) {
        Log.e("TAG", "filterHistory: ---------------FilterList-SIZE -----------${filterList.size}")
        val list =
            if (customerId.isNotEmpty() && status != Constants.STATUS_ALL) { // status = Cancel Or Completed  && CustomerId
                filterList.filter { it.orderData.cancelled == status && it.orderData.customerId == customerId }
            } else if (customerId.isNotEmpty() && status == Constants.STATUS_ALL) { // "ID" && 2 status = All && CustomerId
                filterList.filter { it.orderData.customerId == customerId }
            } else if (customerId.isEmpty() && status != Constants.STATUS_ALL) { // "" && 0,1 status = Completed && All
                filterList.filter { it.orderData.cancelled == status }
            } else {
                filterList // status = All && All
            }
        orderHistoryList.postValue(list)
    }

    fun getOrderStatusInInt(orderStatusString: String): Int {
        var orderStatus = 0
        when (orderStatusString) {
            Constants.CANCELLED -> {
                orderStatus = 1
            }

            Constants.COMPLETED -> {
                orderStatus = 0
            }

            Constants.ALL -> {
                orderStatus = Constants.STATUS_ALL
            }
        }
        return orderStatus
    }
}