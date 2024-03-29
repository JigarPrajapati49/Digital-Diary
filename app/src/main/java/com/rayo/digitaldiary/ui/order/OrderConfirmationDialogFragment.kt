package com.rayo.digitaldiary.ui.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.databinding.DialogOrderConfirmFragmentBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.ui.history.OwnerOrStaffDetailAdapter
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.product.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderConfirmationDialogFragment(
    val orderData: OrderData,
    val createOrderPresenter: CreateOrderPresenter,
    val customerName: String,
    val currencySymbol: String,
) :
    BaseDialogFragment<DialogOrderConfirmFragmentBinding, ProductViewModel?>(),
    OrderConfirmationPresenter {

    override val viewModel: ProductViewModel by viewModels()
    val productList: MutableList<Product> = ArrayList()
    val languageList: MutableList<LanguageTranslationData> = ArrayList()

    override fun getDialogFragmentId(): Int {
        return R.layout.dialog_order_confirm_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createOrderPresenter = this
        binding.customerName = this.customerName
        binding.orderData = this.orderData
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        Coroutines.ioThenMain({
            viewModel.getLanguageTranslation().let {
                languageList.clear()
                languageList.addAll(it)
            }
        }, {
            setProductAdapter()
        })
    }

    private fun setProductAdapter() {
        productList.clear()
        productList.addAll(this.orderData.product)
        binding.productAdapter = OwnerOrStaffDetailAdapter(
            productList,
            preferenceManager.getPref(Constants.prefCurrencySymbol, ""),
            languageList
        )
    }

    companion object {
        private const val TAG = "OrderConfirmationDialog"
        fun show(
            fragmentManager: FragmentManager,
            orderData: OrderData,
            createOrderPresenter: CreateOrderPresenter,
            customerName: String,
            currencySymbol: String,
        ) {
            val orderConfirmationDialog =
                OrderConfirmationDialogFragment(
                    orderData,
                    createOrderPresenter,
                    customerName,
                    currencySymbol
                )
            orderConfirmationDialog.show(fragmentManager, TAG)
        }
    }

    override fun onConfirmOrderClick(orderData: OrderData) {
        dismiss()
        createOrderPresenter.onConfirmClick(orderData)
    }

    override fun onCancelClick() {
        dismiss()
    }

}