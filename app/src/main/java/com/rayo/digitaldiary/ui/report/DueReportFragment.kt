package com.rayo.digitaldiary.ui.report

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.baseClasses.CallMessageBottomSheetDialog
import com.rayo.digitaldiary.databinding.FragmentDueReportBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.CustomerData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DueReportFragment : BaseFragment<FragmentDueReportBinding, DueReportViewModel>(),
    DueReportPresenter, SearchView.OnQueryTextListener {

    private val dueAmountList: MutableList<CustomerData> = ArrayList()
    private val copyDueAmountList: MutableList<CustomerData> = ArrayList()

    override val viewModel: DueReportViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_due_report
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDueReportAdapter()
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        binding.searchCustomerName.setOnQueryTextListener(this)

        showProgressDialog()
        viewModel.dueCustomerList.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            if (it.isNullOrEmpty()) {
                binding.errorMessage =
                    Utils.getTranslatedValue(getString(R.string.no_customer_found))
            }
            binding.totalDueAmount = viewModel.dueAmount.toString()
            dueAmountList.clear()
            copyDueAmountList.clear()
            dueAmountList.addAll(it)
            copyDueAmountList.addAll(dueAmountList)
            binding.dueReportAdapter?.notifyDataSetChanged()
        }
    }

    private fun setDueReportAdapter() {
        binding.dueReportAdapter = DueReportAdapter(
            dueAmountList,
            this,
            preferenceManager.getPref(Constants.prefCountryCode, ""),
            preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        )
    }

    override fun onPhoneNumberClick(phoneNumber: String?) {
        phoneNumber?.let { CallMessageBottomSheetDialog.display(childFragmentManager, it) }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        binding.searchCustomerName.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        dueAmountList.clear()
        for (i in 0 until copyDueAmountList.size) {
            if (copyDueAmountList[i].name.contains(newText.toString(), ignoreCase = true)) {
                dueAmountList.add(copyDueAmountList[i])
            }
        }
        setErrorMessage()
        binding.dueReportAdapter?.notifyDataSetChanged()
        return false
    }

    private fun setErrorMessage() {
        if (dueAmountList.isEmpty()) {
            binding.errorMessage = Utils.getTranslatedValue(getString(R.string.no_customer_found))
        } else {
            binding.errorMessage = ""
        }
    }
}