package com.rayo.digitaldiary.ui.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.order.OrderData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val apiRepository: APIRepository,
    private val orderDao: OrderDao
) :
    BaseViewModel() {

    val historyDetailsData = MutableLiveData<CustomerWithHistory>()
    val cancelOrderSuccess = MutableLiveData<Event<CustomerWithHistory>>()

    private fun insertCancelOrderDetails(orderData: CustomerWithHistory) {
        Coroutines.ioThenMain({
            orderDao.insertOder(orderData.orderData)
        }, {
            cancelOrderSuccess.postValue(Event(orderData))
            if (it != null) {
                Log.e("TAG", "insertCancelOrderDetails: ---$it")
            }
        })
    }

    fun getOrderDetailsFromDB(localOrderId: String) {
        Coroutines.ioThenMain({
            orderDao.getOwnerOrStaffOrderHistory(localOrderId)
        }, {
            if (it != null) {
                historyDetailsData.postValue(it)
            }
        })
    }

    fun cancelOrder(orderData: CustomerWithHistory) {
        Coroutines.ioThenMain({
            orderDao.getOwnerOrStaffOrderHistory(orderData.orderData.localOrderId)
        }, { customerWithHistory ->
            customerWithHistory?.let {
                it.orderData.cancelled = 1
                it.orderData.cancelledById = preferenceManager.getPref(Constants.prefUserId, "")
                it.orderData.sync = 0
                val cancelledDate = Utils.getCurrentDateTime()
                it.orderData.cancelledDate = cancelledDate
                it.orderData.updatedAt = cancelledDate
                it.orderData.cancelReason = orderData.orderData.cancelReason
                insertCancelOrderDetails(it)
            }
        })
    }
}