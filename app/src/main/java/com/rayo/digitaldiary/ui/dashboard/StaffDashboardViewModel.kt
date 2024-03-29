package com.rayo.digitaldiary.ui.dashboard

import android.content.Context
import androidx.core.content.ContextCompat
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 29/04/22.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class StaffDashboardViewModel @Inject constructor(val preferenceManager: PreferenceManager) :
    BaseViewModel() {
    var totalCustomer = 0
    var totalProduct = 0
    var isCustomerFetchCount = false

    fun getDashBoardData(context: Context): MutableList<DashboardModel> {
        val dashBoardList: MutableList<DashboardModel> = ArrayList()

        val ownerDetail = DashboardModel()
        ownerDetail.id = Constants.DASHBOARD_CUSTOMER_ID
        ownerDetail.icon = ContextCompat.getDrawable(context, R.drawable.ic_customers)
        ownerDetail.title = Utils.getTranslatedValue(context.getString(R.string.customers))
        ownerDetail.count = "0"

        val data2 = DashboardModel()
        data2.id = Constants.DASHBOARD_OWNER_DETAIL
        data2.icon = ContextCompat.getDrawable(context, R.drawable.ic_owner_detail)
        data2.title = preferenceManager.getPref(Constants.prefBusinessName, "")

        val data3 = DashboardModel()
        data3.id = Constants.DASHBOARD_ADD_PRODUCT_ID
        data3.icon = ContextCompat.getDrawable(context, R.drawable.ic_product)
        data3.title = Utils.getTranslatedValue(context.getString(R.string.products))
        data3.count = "0"

        val data4 = DashboardModel()
        data4.id = Constants.DASHBOARD_ADD_ORDER_ID
        data4.icon = ContextCompat.getDrawable(context, R.drawable.ic_create_order)
        data4.title = Utils.getTranslatedValue(context.getString(R.string.create_order))

        val data5 = DashboardModel()
        data5.id = Constants.DASHBOARD_ORDER_HISTORY
        data5.icon = ContextCompat.getDrawable(context, R.drawable.ic_order_history)
        data5.title = Utils.getTranslatedValue(context.getString(R.string.order_history))

        val data6 = DashboardModel()
        data6.id = Constants.DASHBOARD_ADD_PAYMENT
        data6.icon = ContextCompat.getDrawable(context, R.drawable.ic_payment)
        data6.title = Utils.getTranslatedValue(context.getString(R.string.payments))

        dashBoardList.add(ownerDetail)
        dashBoardList.add(data2)
        dashBoardList.add(data3)
        dashBoardList.add(data5)
        dashBoardList.add(data6)

        return dashBoardList
    }
}