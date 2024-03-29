package com.rayo.digitaldiary.ui.product

interface ProductPresenter {
    fun onAddProductClick()
    fun onAllClick()
    fun onActiveClick()
    fun onInactiveClick()
    fun onProductClick(data: Product)
    fun onProductAddedSuccessfully(product: Product)

    fun onProductUpdatedSuccessfully()
}