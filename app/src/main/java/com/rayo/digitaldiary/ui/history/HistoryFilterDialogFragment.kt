package com.rayo.digitaldiary.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.databinding.FragmentHistoryFilterDialogBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.order.SelectCustomerAdapter
import com.rayo.digitaldiary.ui.order.SelectCustomerPresenter
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 02/06/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class HistoryFilterDialogFragment(
    private val historyPresenter: HistoryPresenter,
    private val setSelectedOrderStatus: String,
    private val setSelectedDateFilter: String,
    private val customerList: MutableList<CustomerData>,
) :
    BaseDialogFragment<FragmentHistoryFilterDialogBinding, BaseViewModel?>(),
    HistoryFilterPresenter, SelectCustomerPresenter, android.widget.SearchView.OnQueryTextListener {

    override val viewModel: BaseViewModel? = null
    private val copyCustomerList: MutableList<CustomerData> = ArrayList()

    override fun getDialogFragmentId(): Int {
        return R.layout.fragment_history_filter_dialog
    }

    init {
        copyCustomerList.addAll(customerList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setErrorMessage()
        binding.searchCustomer.setOnQueryTextListener(this)
        binding.selectedOrderStatus = setSelectedOrderStatus
        binding.filterPresenter = this
        setCustomerListAdapter()
        binding.isClearVisible = (setSelectedOrderStatus != Constants.ALL
                || setSelectedDateFilter != Constants.DEFAULT
                || binding.customerAdapter?.getSelectedCustomer()?.name?.lowercase() != Constants.ALL)
    }

    private fun setCustomerListAdapter() {
        binding.customerAdapter = SelectCustomerAdapter(
            customerList,
            preferenceManager.getPref(Constants.prefCountryCode, ""),
            preferenceManager.getPref(Constants.prefCurrencySymbol, ""),
            presenter = this
        )
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

    override fun onApplyClick() {
        val selectedCustomer = binding.customerAdapter?.getSelectedCustomer()
        val paymentStatus = when (binding.rgPaymentStatus.checkedRadioButtonId) {
            R.id.radioCompleted -> {
                Constants.COMPLETED
            }

            R.id.radioCancelled -> {
                Constants.CANCELLED
            }

            else -> {
                Constants.ALL
            }
        }
        historyPresenter.onFilterApplyClick(
            paymentStatus,
            selectedCustomer ?: customerList[0]
        )
        dismiss()
    }

    override fun onCancelClick() {
        dismiss()
    }

    override fun onClearFilterClick() {
        binding.radioAll.isChecked = true
        binding.customerAdapter?.setAllSelected()
        binding.isClearVisible = false
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

    private fun setErrorMessage() {
        if (customerList.isEmpty()) {
            binding.errorMessage = Utils.getTranslatedValue(getString(R.string.no_customer_found))
        } else {
            binding.errorMessage = ""
        }
    }

    companion object {
        private const val TAG = "HistoryFilterDialogFragment"
        fun show(
            fragmentManager: FragmentManager,
            customerList: MutableList<CustomerData>,
            filterOrderStatus: String,
            dateFilter: String,
            historyPresenter: HistoryPresenter
        ) {
            val historyFilterDialogBinding =
                HistoryFilterDialogFragment(
                    historyPresenter,
                    filterOrderStatus,
                    dateFilter,
                    customerList
                )
            historyFilterDialogBinding.show(fragmentManager, TAG)
        }
    }

}