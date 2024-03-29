package com.rayo.digitaldiary.ui.customer

import com.rayo.digitaldiary.baseClasses.BaseInterface

/**
 * Created by Mittal Varsani on 18/04/23.
 *
 * @author Mittal Varsani
 */
interface CustomerPresenter {

    fun onAddCustomerClick() {}

    fun onAllClick() {}

    fun onActiveClick() {}

    fun onInactiveClick() {}

    fun onItemClick(customerData: CustomerData) {}

    fun onCustomerAddedSuccessfully(customerData: CustomerData)

    fun onCustomerUpdatedSuccessfully(customerData: CustomerData) {}

    fun onPhoneNumberClick(phoneNumber: String?)
}

interface AddCustomerPresenter : BaseInterface {
    fun onAddCustomerClick(customerData: CustomerData)
}

interface CustomerDetailsPresenter {

    fun onGenerateQRClick(customerId: String)

    fun onEditClick(customerData: CustomerData)

    fun onShowHistoryClick(customerId: String)

    fun onPaymentsClick(customerData: CustomerData)

    fun onPhoneNumberClick(phoneNumber: String?)

    fun onCreateOrderClick(customerData: CustomerData)

    fun onCustomerLoginDevicesClick(userId: String?)

    fun onQRClick(customerId: String)
}

interface GenerateQRCodePresenter {
    fun onGenerateQRClick(customerId: String)
}