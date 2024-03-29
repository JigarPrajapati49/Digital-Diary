package com.rayo.digitaldiary.ui.dashboard

import com.rayo.digitaldiary.ui.product.Product

interface StaffDashboardPresenter {
    fun onItemClicked(dashBoardData: DashboardModel)
    fun setCustomerCount(totalCustomer: Int)
    fun setProductCount(totalProduct: Int)
    fun onCreateOrderClick()
}