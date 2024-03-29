package com.rayo.digitaldiary.ui.payment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.util.Util
import com.google.gson.Gson
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.CustomerDao
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.database.PaymentDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.order.OrderData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddPaymentViewModel @Inject constructor(
    private val customerDao: CustomerDao,
    private val orderDao: OrderDao,
    private val paymentDao: PaymentDao,
    private val preferenceManager: PreferenceManager
) :
    BaseViewModel() {
    val customerList = MutableLiveData<Event<List<CustomerData>>>()
    private val paymentList: MutableList<PaymentData> = ArrayList()
    private val orderList: MutableList<OrderData> = ArrayList()
    var selectedCustomerData: CustomerData? = null
    var paymentInsertedInDB = MutableLiveData<Event<PaymentData>>()
    var duePaymentList = MutableLiveData<List<DuePaymentData>>()
    var isShowCustomerDialog = true

    fun getAllPayment() {
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

    private fun getAllCustomerFromDatabase() {
        Coroutines.ioThenMain({
            customerDao.getActiveCustomers()
        }, {
            it?.let {
                for (item in it) {
                    var totalAmount = 0f
                    var paymentAmount = 0f
                    item.isCustomerSelected = 0
                    if (item.id == selectedCustomerData?.id) {
                        item.isCustomerSelected = 1
                    }
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
                customerList.postValue(Event(it))
            }
        })
    }

    fun getCustomerMonthDue(customerId: String, dueAmount: Float) {
        var totalPayment = getCustomerTotalPayment(customerId)
        Log.d("TAG", "getCustomerMonthDue: totalPayment $totalPayment dueAmount $dueAmount")
        val dueMonthsList: MutableList<DuePaymentData> = ArrayList()
        Coroutines.ioThenMain({
            orderDao.getDataMonthYearWise(customerId)
        }, {
            Log.d("TAG", "getCustomerMonthDue: ${Gson().toJson(it)}")
            var duePaymentData = DuePaymentData()
            if (it != null) {
                for (i in it.indices) {
                    if (duePaymentData.monthYear != Utils.getMonthAndYear(it[i].orderDate)) {
                        if(duePaymentData.dueAmount > 0f) {
                            if((totalPayment - duePaymentData.dueAmount) < 0f) {
                                if(totalPayment > 0f) {
                                        duePaymentData.dueAmount =
                                            duePaymentData.dueAmount - totalPayment
                                        totalPayment = 0f
                                }
                                dueMonthsList.add(duePaymentData)
                            }
                            totalPayment -= duePaymentData.dueAmount
                            if(totalPayment < 0f) {
                                totalPayment = 0f
                            }
                            Log.d(
                                "TAG",
                                "getCustomerMonthDue: - month ${duePaymentData.monthYear} orderTotalAmount ${duePaymentData.dueAmount} payment $totalPayment"
                            )
                        }
                        duePaymentData = DuePaymentData()
                        duePaymentData.monthYear = Utils.getMonthAndYear(it[i].orderDate)
                    }
                    duePaymentData.dueAmount += it[i].orderAmount.toFloat()
                    Log.d("TAG", "getCustomerMonthDue: orderAmount ${it[i].orderAmount} dueAmount ${duePaymentData.dueAmount}")
                    if(i == (it.size-1)) {
                        if(duePaymentData.dueAmount > 0f) {
                            if((totalPayment - duePaymentData.dueAmount) < 0f) {
                                if(totalPayment > 0f) {
                                    duePaymentData.dueAmount = duePaymentData.dueAmount - totalPayment
                                    totalPayment = 0f
                                }
                                dueMonthsList.add(duePaymentData)
                            }
                            totalPayment -= duePaymentData.dueAmount
                            if(totalPayment < 0f) {
                                totalPayment = 0f
                            }
                            Log.d(
                                "TAG",
                                "getCustomerMonthDue: - month ${duePaymentData.monthYear} dueAmount ${duePaymentData.dueAmount} payment $totalPayment"
                            )
                        }
                    }
                }
            }
            duePaymentList.postValue(dueMonthsList)
        })
    }

    fun addPayment(paymentData: PaymentData) {
        with(paymentData) {
            localPaymentId = Utils.generateUUID()
            customerId = selectedCustomerData?.id ?: "0"
            addByUserId = preferenceManager.getPref(Constants.prefUserId, "")
            createdAt = Utils.getCurrentDateTime()
            updatedAt = createdAt
            sync = 0
            Coroutines.ioThenMain({
                paymentDao.insetPayment(this)
            }, {
                paymentInsertedInDB.postValue(Event(this))
            })
        }
    }

    private fun getCustomerTotalPayment(customerId: String): Float {
        var totalPayment = 0f
        for(item in paymentList) {
            if(item.customerId == customerId) {
                totalPayment += item.amount.toFloat()
            }
        }
        return totalPayment
    }
}