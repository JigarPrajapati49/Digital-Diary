package com.rayo.digitaldiary.ui.product

import com.rayo.digitaldiary.baseClasses.BaseInterface

interface AddProductPresenter: BaseInterface {
    fun onAddProductClick(product: Product)

}
