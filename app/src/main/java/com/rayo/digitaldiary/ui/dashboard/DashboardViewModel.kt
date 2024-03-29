package com.rayo.digitaldiary.ui.dashboard

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.database.PaymentDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 29/04/22.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val orderDao: OrderDao,
    private val paymentDao: PaymentDao,
) : BaseViewModel() {
    var totalStaff = 0
    var totalCustomer = 0
    var totalProduct = 0
    var isStaffAndCustomerCountFetched = false
    var isSync = MutableLiveData<Int>()

    fun getDashBoardData(context: Context): MutableList<DashboardModel> {
        val dashBoardList: MutableList<DashboardModel> = ArrayList()

        val data1 = DashboardModel()
        data1.id = Constants.DASHBOARD_CUSTOMER_ID
        data1.icon = ContextCompat.getDrawable(context, R.drawable.ic_customers)
        data1.title = context.getString(R.string.customers)
        data1.count = "0"

        val data2 = DashboardModel()
        data2.id = Constants.DASHBOARD_STAFF_ID
        data2.icon = ContextCompat.getDrawable(context, R.drawable.ic_staff)
        data2.title = context.getString(R.string.total_staff)
        data2.count = "0"

        val data3 = DashboardModel()
        data3.id = Constants.DASHBOARD_ADD_PRODUCT_ID
        data3.icon = ContextCompat.getDrawable(context, R.drawable.ic_product)
        data3.title = context.getString(R.string.products)
        data3.count = "0"

        val data4 = DashboardModel()
        data4.id = Constants.DASHBOARD_ADD_ORDER_ID
        data4.icon = ContextCompat.getDrawable(context, R.drawable.ic_create_order)
        data4.title = context.getString(R.string.create_order)

        val data5 = DashboardModel()
        data5.id = Constants.DASHBOARD_ORDER_HISTORY
        data5.icon = ContextCompat.getDrawable(context, R.drawable.ic_order_history)
        data5.title = context.getString(R.string.order_history)

        val data6 = DashboardModel()
        data6.id = Constants.DASHBOARD_ADD_PAYMENT
        data6.icon = ContextCompat.getDrawable(context, R.drawable.ic_payment)
        data6.title = context.getString(R.string.payments)

        val data7 = DashboardModel()
        data7.id = Constants.DASHBOARD_REPORT
        data7.icon = ContextCompat.getDrawable(context, R.drawable.ic_report)
        data7.title = context.getString(R.string.report)

        dashBoardList.add(data1)
        dashBoardList.add(data2)
        dashBoardList.add(data3)
        dashBoardList.add(data5)
        dashBoardList.add(data6)
        dashBoardList.add(data7)

        return dashBoardList
    }

    fun syncPaymentOrderCount() {
        Coroutines.ioThenMain({
            orderDao.getOrderSyncCount()
        }, { orderSyncCount ->
            Coroutines.ioThenMain({
                paymentDao.getPaymentSyncCount()
            }, { paymentSyncCount ->
                isSync.postValue(paymentSyncCount?.plus(orderSyncCount ?: 0))
            })
        })
    }
}