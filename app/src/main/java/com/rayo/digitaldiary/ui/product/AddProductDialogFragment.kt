package com.rayo.digitaldiary.ui.product


import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseBottomSheetDialogFragment
import com.rayo.digitaldiary.databinding.DialogAddProductBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddProductDialogFragment(
    private val mProductData: Product?,
    private val productPresenter: ProductPresenter,
) :
    BaseBottomSheetDialogFragment<DialogAddProductBinding, AddProductViewModel>(),
    AddProductPresenter, AdapterView.OnItemSelectedListener {

    private val copyProductData = mProductData?.copy(mProductData)
    private var selectedItem = ""
    private val translatedValueList: ArrayList<String> = ArrayList()
    private var languageList: ArrayList<LanguageTranslationData> = ArrayList()
    override val viewModel: AddProductViewModel by viewModels()
    override fun getDialogFragmentId() = R.layout.dialog_add_product
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        binding.presenter = this
        binding.productData = copyProductData ?: Product()


        // match the term of language Translation  and Pref If match it show translated value
        Coroutines.ioThenMain({
            viewModel.getLanguageTranslation().let { it ->
                for (data in it) {
                    preferenceManager.getPref(Constants.prefProductUnits, "").split(",").let {
                        for (i in it.indices) {
                            if (it[i].trim() == data.term) {
                                languageList.add(data)
                                translatedValueList.add(data.translatedValue)
                            }
                        }
                    }
                }
            }
        }, {
            setUnitSpinnerAdapter()
            for (i in 0 until languageList.size) {
                if (languageList[i].term == copyProductData?.unit) {
                    binding.spinner.setSelection(i) // to make product unit selected when coming for product update
                }
            }
        })

        viewModel.addProductSuccess.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            if (copyProductData != null) {
                mProductData?.update(mProductData, copyProductData)
                productPresenter.onProductUpdatedSuccessfully()
            } else {
                it.productData?.let {
                    it1 -> productPresenter.onProductAddedSuccessfully(it1) }
            }
            context?.let { it1 ->
                dialogHelper.showOneButtonDialog(it1, Utils.getTranslatedValue(it.message.toString()))
            }
            dismiss()
        }
    }

    private fun setUnitSpinnerAdapter() {
        binding.spinner.onItemSelectedListener = this
        binding.spinner.adapter = context?.let { it1 ->
            ArrayAdapter(
                it1,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                translatedValueList
            )
        } as SpinnerAdapter
    }

    override fun onAddProductClick(product: Product) {
        if (Utils.isSingleClick()){
            if (product.weight.isNotEmpty()) {
                product.weight = product.weight.toInt().toString()
            }
            if (product.price.isNotEmpty()) {
                product.price = product.price.toFloat().toString()
            }
            product.unit = selectedItem
            product.active = if (binding.radioActive.isChecked) {
                Constants.STATES_ACTIVE
            } else {
                Constants.STATES_INACTIVE
            }
            isValidAddProduct(product)?.let {
                context?.let { it1 ->
                    dialogHelper.showOneButtonDialog(it1, Utils.getTranslatedValue(resources.getString(it)))
                }
            } ?: run {
                showProgressDialog()
                viewModel.callAddOrUpdateProduct(product)
            }
        }
    }

    override fun onCancelClick() {
        dismiss()
    }

    private fun isValidAddProduct(product: Product): Int? {
        return when {
            product.name.trim().isEmpty() -> {
                binding.etProductName.requestFocus()
                R.string.enter_product
            }
            product.weight.trim().isEmpty() -> {
                binding.etWeight.requestFocus()
                R.string.enter_weight
            }
            product.weight.trim().toInt() == 0 -> {
                binding.etWeight.requestFocus()
                R.string.weight_should_be_greater_than_zero
            }
            product.price.trim().isEmpty() -> {
                binding.etPriceName.requestFocus()
                R.string.enter_price
            }
            product.price.trim().toFloat() == 0f -> {
                binding.etPriceName.requestFocus()
                R.string.price_should_be_greater_than_zero
            }
            else -> {
                null
            }
        }
    }

    companion object {
        private const val TAG = "AddProductDialog"
        fun show(
            fragmentManager: FragmentManager,
            productPresenter: ProductPresenter,
            productData: Product? = null,
        ) {
            val addProductDialogFragment =
                AddProductDialogFragment(productData, productPresenter)
            addProductDialogFragment.show(fragmentManager, TAG)
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        selectedItem = languageList[position].term
        if (selectedItem == "quantity") {
            binding.inputWeight.hint = Utils.getTranslatedValue("Quantity")
        }else{
            binding.inputWeight.hint = Utils.getTranslatedValue("Weight")
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.e(TAG, "onNothingSelected: ------$p0")
    }

}
