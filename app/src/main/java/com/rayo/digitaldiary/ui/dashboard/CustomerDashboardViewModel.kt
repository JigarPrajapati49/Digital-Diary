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
class CustomerDashboardViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
) :
    BaseViewModel() {

    fun getDashBoardData(context: Context): MutableList<DashboardModel> {
        val dashBoardList: MutableList<DashboardModel> = ArrayList()

        val data2 = DashboardModel()
        data2.id = Constants.DASHBOARD_ORDER_HISTORY
        data2.icon = ContextCompat.getDrawable(context, R.drawable.ic_order_history)
        data2.title = Utils.getTranslatedValue(context.getString(R.string.order_history))

        val data3 = DashboardModel()
        data3.id = Constants.DASHBOARD_OWNER_DETAIL
        data3.icon = ContextCompat.getDrawable(context, R.drawable.ic_owner_detail)
        data3.title = preferenceManager.getPref(Constants.prefBusinessName, "")

        val data4 = DashboardModel()
        data4.id = Constants.DASHBOARD_ADD_PAYMENT
        data4.icon = ContextCompat.getDrawable(context, R.drawable.ic_payment)
        data4.title = Utils.getTranslatedValue(context.getString(R.string.payments))

        dashBoardList.add(data3)
        dashBoardList.add(data2)
        dashBoardList.add(data4)

        return dashBoardList
    }


}