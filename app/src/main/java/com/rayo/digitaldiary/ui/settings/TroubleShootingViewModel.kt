package com.rayo.digitaldiary.ui.settings

import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.database.PaymentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TroubleShootingViewModel @Inject constructor(
    private val orderDao: OrderDao,
    private val paymentDao: PaymentDao,
) : BaseViewModel() {


    suspend fun unSyncRecordOrder(): Int {
        return orderDao.getNotSyncOrder().size
    }

    suspend fun unSyncRecordPayment(): Int {
        return paymentDao.getNotSyncPaymentOrder().size
    }

    suspend fun getTotalRecordOrder(): Int {
        return orderDao.getTotalRecord().size
    }

    suspend fun getTotalRecordPayment(): Int {
        return paymentDao.getTotalRecord().size
    }

    suspend fun getTotalRecordOfStaffOrder(userId: String): Int {
        return orderDao.getTotalRecordOfStaff(userId).size
    }

    suspend fun getTotalRecordOfStaffPayment(userId: String): Int {
        return paymentDao.getTotalRecordOfStaff(userId).size
    }

    suspend fun getTotalRecordOfStaffUnSyncPayment(userId: String): Int {
        return paymentDao.getTotalUnsyncRecordOfStaff(userId).size
    }

    suspend fun getTotalRecordOfStaffUnSyncOrder(userId: String): Int {
        return orderDao.getTotalRecordOfStaffUnSync(userId).size
    }
}