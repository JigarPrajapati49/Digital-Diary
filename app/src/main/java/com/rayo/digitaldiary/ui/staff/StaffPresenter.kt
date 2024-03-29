package com.rayo.digitaldiary.ui.staff

import com.rayo.digitaldiary.baseClasses.BaseInterface
import com.rayo.digitaldiary.ui.customer.CustomerData

/**
 * Created by Mittal Varsani on 12/04/23.
 *
 * @author Mittal Varsani
 */
interface StaffPresenter {
    fun onStaffMemberAddedSuccessfully(staffData: StaffData)

    fun onStaffMemberUpdatedSuccessfully(staffData: StaffData) {}

    fun onAddStaffClick() {}

    fun onAllClick() {}

    fun onActiveClick() {}

    fun onInactiveClick() {}

    fun onItemClick(staffData: StaffData) {}

    fun onPhoneNumberClick(phoneNumber: String?)
}

interface AddStaffPresenter : BaseInterface {
    fun onAddStaffClick(staffData: StaffData)
}

interface StaffDetailsPresenter {

    fun onGenerateQRClick(staffId: String)

    fun onEditClick(staffData: StaffData)

    fun onShowHistoryClick(staffId: String)

    fun onPhoneNumberClick(phoneNumber: String?)

    fun onStaffLoginDevicesClick(userId:String?)

    fun onPaymentsClick(staffData: StaffData)

    fun onQrClick(customerId:String)
}