package com.rayo.digitaldiary.ui.order

import com.rayo.digitaldiary.baseClasses.BaseInterface
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.product.Product

/**
 * Created by Mittal Varsani on 23/05/23.
 *
 * @author Mittal Varsani
 */

interface CreateOrderPresenter : BaseInterface {

    fun onSelectDateClick()

    fun onSelectCustomerClick()

    fun onProductSelected(mSelectedProductList: List<Product>)

    fun onProductQuantityUpdate(productList: List<Product>)
    fun onSelectProductClick()

    fun onCreateOrderClick()
    fun onAddQuantityClick(position: Int)
    fun onRemoveQuantityClick(position: Int)
    fun onConfirmClick(orderData: OrderData)
}

interface SelectCustomerPresenter : BaseInterface {

    fun onCustomerSelected() {}

    fun onCustomerSelected(customerData: CustomerData) {}
}

interface SelectProductPresenter : BaseInterface {

    fun onProductSelected()
}

interface OrderConfirmationPresenter : BaseInterface {
    fun onConfirmOrderClick(orderData: OrderData)
}