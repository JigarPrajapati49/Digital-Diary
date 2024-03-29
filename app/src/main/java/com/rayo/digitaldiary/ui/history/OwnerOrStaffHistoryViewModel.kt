package com.rayo.digitaldiary.ui.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.CustomerDao
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.ui.customer.CustomerData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class OwnerOrStaffHistoryViewModel @Inject constructor(
    private val customerDao: CustomerDao,
    private val orderDao: OrderDao
) :
    BaseViewModel() {

    internal var createdById = ""
    val customerList = MutableLiveData<List<CustomerData>>()
    var filterOrderStatus = Constants.ALL
    var filterDate = Constants.DEFAULT
    var filterSelectedCustomer: CustomerData = CustomerData().apply { name = Constants.ALL }
    val orderHistoryList = MutableLiveData<Event<List<CustomerWithHistory>>>()
    private var filterList: MutableList<CustomerWithHistory> = ArrayList()
    private val utcTimeFormat =
        SimpleDateFormat(Constants.yearFormat, Locale.ENGLISH)
    private var startTime = ""
    private var endTime = ""
    var staffCreatedById = ""
    val calendar: Calendar = Calendar.getInstance()

    init {
        getAllCustomerFromDatabase()
    }

    private fun getAllCustomerFromDatabase() {
        Coroutines.ioThenMain({
            customerDao.getActiveCustomers()
        }, {
            customerList.postValue(it)
        })
    }

    fun getOrderHistory() {
        if(staffCreatedById.isNotEmpty()) {
            getStaffOrderHistoryFromDB(staffCreatedById)
        } else {
            getAllOrderHistoryFromDB()
        }
    }

    private fun getAllOrderHistoryFromDB() {
        Log.d("staffHistory", "getOrderHistoryFromDB: startTime $startTime endTime $endTime")
        Coroutines.ioThenMain({
            orderDao.getOrders(startTime, endTime)
        }, {
            if (it != null) {
                Log.d("staffHistory", "getOrderHistoryFromDB: result size ${it.size}")
                filterList.clear()
                filterList.addAll(it)
                filterHistory(getOrderStatusInInt(filterOrderStatus), filterSelectedCustomer)
            }
        })
    }

    private fun getStaffOrderHistoryFromDB(staffId: String) {
        Log.d("staffHistory", "getStaffOrderHistoryFromDB: startTime $startTime endTime $endTime staffId $staffId")
        Coroutines.ioThenMain({
            orderDao.getStaffOrderHistory(startTime, endTime, staffId)
        }, {
            if (it != null) {
                filterList.clear()
                filterList.addAll(it)
                filterHistory(getOrderStatusInInt(filterOrderStatus), filterSelectedCustomer)
            }
        })
    }

    fun filterHistory(status: Int, customerData: CustomerData): List<CustomerWithHistory> {

        Log.e("staffHistory", "filterHistory: status $status customerData ${customerData.name} ${filterList.size}")

        val list =
            if (customerData.id.isNotEmpty() && status != Constants.STATUS_ALL) { // status = Cancel Or Completed  && CustomerName
                filterList.filter { it.orderData.cancelled == status && it.orderData.customerId == customerData.id }
            } else if (customerData.id.isNotEmpty() && status == Constants.STATUS_ALL) { // "ID" && 2 status = All && CustomerName
                filterList.filter { it.orderData.customerId == customerData.id }
            } else if (customerData.id.isEmpty() && status != Constants.STATUS_ALL) { // "" && 0,1 status = Completed && All
                filterList.filter { it.orderData.cancelled == status }
            } else {
                filterList // status = All && All
            }
        Log.d("staffHistory", "getOrderHistoryFromDB: result size ${list.size}")
        orderHistoryList.postValue(Event(list))
        return list
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

    fun getDayText(): String {
        return SimpleDateFormat(Constants.orderDisplayDateFormat, Locale.ENGLISH).format(
            calendar.time
        )
    }

    fun initStartEndDate() {
        startTime = utcTimeFormat.format(calendar.time) + "T00:00:00.000Z"
        endTime = utcTimeFormat.format(calendar.time) + "T23:59:00.000Z"
    }

    fun nextDayClick() {
        calendar.add(Calendar.DATE, 1)
        initStartEndDate()
    }

    fun previousDayClick() {
        calendar.add(Calendar.DATE, -1)
        initStartEndDate()
    }
}