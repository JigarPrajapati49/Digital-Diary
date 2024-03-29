package com.rayo.digitaldiary.ui.profile

import com.rayo.digitaldiary.baseClasses.BaseInterface
import com.rayo.digitaldiary.ui.CurrencyData
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.staff.StaffData

interface ProfilePresenter {
    fun onEditClick()
    fun onProfileUpdated()
    fun onLoginDevice()
    fun onDeleteAccountClick()
}

interface UpdateProfilePresenter : BaseInterface {
    fun updateClick()
    fun onSelectCurrencyClick()
    fun onCurrencySelected(currencyData: CurrencyData)
    fun setLastSelectedCurrency(countryName: String)
}

interface StaffProfilePresenter {
    fun onEditClick(staffData: StaffData)
    fun onLoginDevicesClick(userId: String?)
}

interface UpdateStaffProfilePresenter : BaseInterface {
    fun onUpdateClick(staffData: StaffData)
}

interface CustomerProfilePresenter {

    fun editClick(customerData: CustomerData)

    fun onProfileUpdated()

    fun onLoginDevicesClick(userId: String)
}

interface UpdateCustomerProfilePresenter : BaseInterface {
    fun onUpdateClick(customerData: CustomerData)
}

interface OwnerProfilePresenter {
    fun onPhoneNumberClick(phoneNumber: String?)
    fun onProfileUpdated()
}

interface CurrencyPresenter : BaseInterface {
    fun onSelectCurrencyClick()
}

interface LogoutPresenter : BaseInterface {
    fun logoutClick(sessionData: SessionData)
    fun onLogoutAllDeviceClick(userId: String)
}