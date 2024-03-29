package com.rayo.digitaldiary.ui.dashboard

import com.rayo.digitaldiary.baseClasses.BaseInterface

interface DashboardPresenter : BaseInterface{
    fun onItemClicked(dashBoardData: DashboardModel)

    fun setStaffAndCustomerCount(totalStaff: Int, totalCustomer: Int, totalProduct: Int)

    fun onCreateOrderClick()

}