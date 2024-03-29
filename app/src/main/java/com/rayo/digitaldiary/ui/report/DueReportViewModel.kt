package com.rayo.digitaldiary.ui.report

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.CustomerDao
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.database.PaymentDao
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.order.OrderData
import com.rayo.digitaldiary.ui.payment.PaymentData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DueReportViewModel @Inject constructor(
    private val customerDao: CustomerDao,
    private val paymentDao: PaymentDao,
    private val orderDao: OrderDao
) : BaseViewModel() {

    var dueAmount = 0f
    private val paymentList: MutableList<PaymentData> = ArrayList()
    private val orderList: MutableList<OrderData> = ArrayList()
    val dueCustomerList = MutableLiveData<List<CustomerData>>()

    init {
        getAllPayment()
    }

    private fun getAllCustomerFromDatabase() {
        Coroutines.ioThenMain({
            customerDao.getActiveCustomers()
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
                val list: MutableList<CustomerData> = ArrayList()
                for (item in it) {
                    if (item.dueAmount > 0) {
                        dueAmount += item.dueAmount
                        list.add(item)
                    }
                }
                dueCustomerList.postValue(list)
            }
        })
    }

    private fun getAllPayment() {
        paymentList.clear()
        Coroutines.ioThenMain({
            paymentDao.getAllPayment()
        }, {
            it?.let { it1 -> paymentList.addAll(it1) }
            getAllOrders()
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
}