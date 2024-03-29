package com.rayo.digitaldiary.ui.payment

import com.rayo.digitaldiary.baseClasses.BaseInterface
import com.rayo.digitaldiary.ui.customer.CustomerData

interface AddPaymentPresenter : BaseInterface {
    fun onSelectCustomerClick()

    fun onAddPaymentClick(paymentData: PaymentData)

    fun onConfirmClick(paymentData: PaymentData)

}

interface PaymentConfirmationPresenter : BaseInterface {
    fun onConfirmPaymentClick(paymentData: PaymentData)
}

interface PaymentHistoryPresenter {
    fun onAddPaymentClick()

    fun onFilterApplyClick(startDate: Long, endDate: Long, customerData: CustomerData)
}

interface PaymentFilterPresenter : BaseInterface {
    fun onClearFilterClick()

    fun onApplyClick()

    fun onStartDateClick()
    fun onEndDateClick()
}