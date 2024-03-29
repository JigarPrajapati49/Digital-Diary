package com.rayo.digitaldiary.ui.order

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.databinding.DialogSelectProductBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.product.Product
import com.rayo.digitaldiary.ui.product.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 23/05/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class SelectProductDialogFragment(
    private val productList: MutableList<Product>,
    private val createOrderPresenter: CreateOrderPresenter
) :
    BaseDialogFragment<DialogSelectProductBinding, ProductViewModel?>(),
    SelectProductPresenter, SearchView.OnQueryTextListener {

    private val copyProductList: MutableList<Product> = ArrayList()
    private var languageList: ArrayList<LanguageTranslationData> = ArrayList()

    init {
        copyProductList.addAll(productList)
    }

    override val viewModel: ProductViewModel by viewModels()

    override fun getDialogFragmentId(): Int {
        return R.layout.dialog_select_product
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setErrorMessage()
        binding.selectProductPresenter = this
        binding.searchProduct.setOnQueryTextListener(this)
        Coroutines.ioThenMain({
            viewModel.getLanguageTranslation().let {
                languageList.clear()
                languageList.addAll(it)
            }
        }, {
            setProductListAdapter()
        })
    }

    private fun setProductListAdapter() {
        binding.productAdapter = SelectProductAdapter(
            productList,
            preferenceManager.getPref(Constants.prefCurrencySymbol, ""), languageList
        )
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        binding.searchProduct.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        productList.clear()
        for (i in 0 until copyProductList.size) {
            if (copyProductList[i].name.contains(newText.toString(), ignoreCase = true)) {
                productList.add(copyProductList[i])
            }
        }
        setErrorMessage()
        binding.productAdapter?.notifyDataSetChanged()
        return false
    }

    private fun setErrorMessage() {
        if (productList.isEmpty()) {
            binding.errorMessage = Utils.getTranslatedValue(getString(R.string.no_product_found))
        } else {
            binding.errorMessage = ""
        }
    }

    override fun onProductSelected() {
        binding.searchProduct.setQuery("", false)
        binding.productAdapter?.getSelectedProduct()?.let {
            if(it.isEmpty()) {
                context?.toast(Utils.getTranslatedValue(getString(R.string.please_select_Product)))
            } else {
                createOrderPresenter.onProductSelected(it)
                dismiss()
            }
        } ?: run {
            context?.toast(Utils.getTranslatedValue(getString(R.string.please_select_Product)))
        }
    }

    override fun onCancelClick() {
        binding.searchProduct.setQuery("", false)
        dismiss()
    }

    companion object {
        private const val TAG = "SelectProductDialog"
        fun show(
            fragmentManager: FragmentManager,
            productList: MutableList<Product>,
            createOrderPresenter: CreateOrderPresenter
        ) {
            val selectProductDialog =
                SelectProductDialogFragment(productList, createOrderPresenter)
            selectProductDialog.show(fragmentManager, TAG)
        }
    }
}