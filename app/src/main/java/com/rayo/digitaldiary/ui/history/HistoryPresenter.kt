package com.rayo.digitaldiary.ui.history

import com.rayo.digitaldiary.baseClasses.BaseInterface
import com.rayo.digitaldiary.database.CustomerWithHistory
import com.rayo.digitaldiary.ui.customer.CustomerData

/**
 * Created by Mittal Varsani on 31/05/23.
 *
 * @author Mittal Varsani
 */
interface HistoryPresenter {

    fun onHistoryItemClick(customerWithHistory: CustomerWithHistory) {}

    fun onNextDateClick()

    fun onPreviousDateClick()

    fun onFilterApplyClick(
        paymentStatus: String,
        selectedCustomer: CustomerData
    ) {
    }

    fun onDateClick() {}
}

interface CustomerHistoryPresenter {
    fun onPreviousMonthClick()
    fun onNextMonthClick()
    fun onHistoryItemClick(customerWithHistory: CustomerWithHistory,position : Int)
}

interface HistoryFilterPresenter : BaseInterface {
    fun onClearFilterClick()
    fun onApplyClick()
}

interface CancelOrderPresenter :BaseInterface {
    fun onCancelOrderClick(orderData: CustomerWithHistory){}
    fun onOkyClick(orderData: CustomerWithHistory)
}