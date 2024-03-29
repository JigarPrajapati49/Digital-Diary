package com.rayo.digitaldiary.ui.order

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.FragmentManager
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.databinding.DialogSelectCustomerBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.helper.toast
import com.rayo.digitaldiary.ui.customer.CustomerData
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 23/05/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class SelectCustomerDialogFragment(
    private val customerList: MutableList<CustomerData>,
    private val createOrderPresenter: SelectCustomerPresenter,
    private val isShowDueAmount : Boolean
) : BaseDialogFragment<DialogSelectCustomerBinding, BaseViewModel?>(), SelectCustomerPresenter,
    SearchView.OnQueryTextListener {

    private val copyCustomerList: MutableList<CustomerData> = ArrayList()

    init {
        copyCustomerList.addAll(customerList)
    }

    override val viewModel: BaseViewModel? = null

    override fun getDialogFragmentId(): Int {
        return R.layout.dialog_select_customer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setErrorMessage()
        binding.selectCustomerPresenter = this
        binding.searchCustomer.setOnQueryTextListener(this)
        setCustomerListAdapter()
    }

    private fun setErrorMessage() {
        if (customerList.isEmpty()) {
            binding.errorMessage = Utils.getTranslatedValue(getString(R.string.no_customer_found))
        } else {
            binding.errorMessage = ""
        }
    }

    private fun setCustomerListAdapter() {
        binding.customerAdapter = SelectCustomerAdapter(
            customerList, preferenceManager.getPref(Constants.prefCountryCode, ""),
            preferenceManager.getPref(Constants.prefCurrencySymbol, ""),
            isShowDueAmount,
            this
        )
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        binding.searchCustomer.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        customerList.clear()
        for (i in 0 until copyCustomerList.size) {
            if (copyCustomerList[i].name.contains(newText.toString(), ignoreCase = true)) {
                if ((binding.customerAdapter?.lastCustomerSelectedId
                        ?: -1) == copyCustomerList[i].id
                ) {
                    copyCustomerList[i].isCustomerSelected = 1
                } else {
                    copyCustomerList[i].isCustomerSelected = 0
                }
                customerList.add(copyCustomerList[i])
            }
        }
        setErrorMessage()
        binding.customerAdapter?.notifyDataSetChanged()
        return false
    }

    override fun onCustomerSelected() {
        binding.searchCustomer.setQuery("", false)
        binding.customerAdapter?.getSelectedCustomer()?.let {
            createOrderPresenter.onCustomerSelected(it)
            dismiss()
        } ?: run {
            context?.toast(Utils.getTranslatedValue(getString(R.string.please_select_customer)))
        }
    }

    override fun onCustomerSelected(customerData: CustomerData) {
        for (item in customerList) {
            if(item.id == customerData.id) {
                item.isCustomerSelected = 1
            } else {
                item.isCustomerSelected = 0
            }
        }
        binding.customerAdapter?.notifyDataSetChanged()
    }

    override fun onCancelClick() {
        binding.searchCustomer.setQuery("", false)
        dismiss()
    }

    companion object {
        private const val TAG = "SelectCustomerDialog"
        fun show(
            fragmentManager: FragmentManager,
            customerList: MutableList<CustomerData>,
            createOrderPresenter: SelectCustomerPresenter,
            isShowDueAmount: Boolean
        ) {
            val selectCustomerDialog =
                SelectCustomerDialogFragment(customerList, createOrderPresenter, isShowDueAmount)
            selectCustomerDialog.show(fragmentManager, TAG)
        }
    }
}