package com.rayo.digitaldiary.ui.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.databinding.FragmentPaymentFilterDialogBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.order.SelectCustomerAdapter
import com.rayo.digitaldiary.ui.order.SelectCustomerPresenter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class PaymentFilterDialogFragment(
    private val startDate: Long,
    private val endDate: Long,
    private val customerList: MutableList<CustomerData>,
    private val paymentPresenter: PaymentHistoryPresenter,
) :
    BaseDialogFragment<FragmentPaymentFilterDialogBinding, PaymentFilterViewModel>(),
    PaymentFilterPresenter, SelectCustomerPresenter, android.widget.SearchView.OnQueryTextListener {

    private val copyCustomerList: MutableList<CustomerData> = ArrayList()

    override val viewModel: PaymentFilterViewModel by viewModels()

    init {
        copyCustomerList.addAll(customerList)
    }

    override fun getDialogFragmentId(): Int {
        return R.layout.fragment_payment_filter_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFilterData()
        binding.isClearVisible =
            (startDate != 0L || endDate != 0L || binding.customerAdapter?.getSelectedCustomer()?.name?.lowercase() != Constants.ALL)
        binding.filterPresenter = this
        binding.searchCustomer.setOnQueryTextListener(this)
    }

    override fun onClearFilterClick() {
        binding.customerAdapter?.setAllSelected()
        binding.etStartDate.text?.clear()
        binding.etEndDate.text?.clear()
        viewModel.selectedStartDate = 0L
        viewModel.selectedEndDate = 0L
        binding.isClearVisible = false
    }

    override fun onApplyClick() {
        val startDate = binding.etStartDate.text.toString()
        val endDate = binding.etEndDate.text.toString()
        if (startDate.isEmpty() && endDate.isEmpty()) {
            val selectedCustomer = binding.customerAdapter?.getSelectedCustomer()
            paymentPresenter.onFilterApplyClick(
                viewModel.selectedStartDate,
                viewModel.selectedEndDate,
                selectedCustomer ?: customerList[0]
            )
            dismiss()
        } else {
            isValidDate(startDate, endDate)?.let {
                context?.let { it1 ->
                    dialogHelper.showOneButtonDialog(
                        it1,
                        Utils.getTranslatedValue(resources.getString(it))
                    )
                }
            } ?: run {
                val selectedCustomer = binding.customerAdapter?.getSelectedCustomer()
                paymentPresenter.onFilterApplyClick(
                    viewModel.selectedStartDate,
                    viewModel.selectedEndDate,
                    selectedCustomer ?: customerList[0]
                )
                dismiss()
            }
        }

    }

    override fun onStartDateClick() {
        if (Utils.isSingleClick()){
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(Utils.getTranslatedValue(getString(R.string.select_date)))
                    .setSelection(if (viewModel.selectedStartDate > 0) viewModel.selectedStartDate else Calendar.getInstance().timeInMillis)
                    .build()
            datePicker.show(childFragmentManager, "CreateOrder")
            datePicker.addOnPositiveButtonClickListener {
                viewModel.selectedStartDate = it
                if (viewModel.selectedEndDate > 0 && viewModel.selectedEndDate < viewModel.selectedStartDate) {
                    binding.etEndDate.text?.clear()
                    viewModel.selectedEndDate = 0
                }
                binding.etStartDate.setText(Utils.getFormattedDate(viewModel.selectedStartDate))
            }
        }
    }

    override fun onEndDateClick() {
        if (Utils.isSingleClick()){
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(Utils.getTranslatedValue(getString(R.string.select_date)))
                    .setSelection(
                        if (viewModel.selectedEndDate > 0) {
                            viewModel.selectedEndDate
                        } else {
                            if (viewModel.selectedStartDate > 0) {
                                viewModel.selectedStartDate
                            } else {
                                Calendar.getInstance().timeInMillis
                            }
                        }
                    )
            if (viewModel.selectedStartDate > 0) {
                val calendarConstraints = CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.from(viewModel.selectedStartDate)).build()
                datePicker.setCalendarConstraints(calendarConstraints)
            }
            datePicker.build().let {
                it.show(childFragmentManager, "CreateOrder")
                it.addOnPositiveButtonClickListener {
                    viewModel.selectedEndDate = it
                    binding.etEndDate.setText(Utils.getFormattedDate(viewModel.selectedEndDate))
                }
            }
        }
    }

    override fun onCancelClick() {
        dismiss()
    }

    override fun onCustomerSelected(customerData: CustomerData) {
        for (item in customerList) {
            if (item.id == customerData.id) {
                item.isCustomerSelected = 1
            } else {
                item.isCustomerSelected = 0
            }
        }
        binding.customerAdapter?.notifyDataSetChanged()
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
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

    private fun initFilterData() {
        viewModel.selectedStartDate = startDate
        viewModel.selectedEndDate = endDate
        if (viewModel.selectedStartDate > 0) {
            binding.etStartDate.setText(Utils.getFormattedDate(viewModel.selectedStartDate))
        }
        if (viewModel.selectedEndDate > 0) {
            binding.etEndDate.setText(Utils.getFormattedDate(viewModel.selectedEndDate))
        }
        binding.customerAdapter = SelectCustomerAdapter(
            customerList,
            preferenceManager.getPref(Constants.prefCountryCode, ""),
            preferenceManager.getPref(Constants.prefCurrencySymbol, ""),
            presenter = this
        )
    }

    companion object {
        private const val TAG = "PaymentFilterDialog"
        fun show(
            fragmentManager: FragmentManager,
            startDate: Long,
            endDate: Long,
            customerList: List<CustomerData>,
            paymentPresent: PaymentHistoryPresenter,
        ) {
            val dialogPaymentFilter =
                PaymentFilterDialogFragment(
                    startDate,
                    endDate,
                    customerList.toMutableList(),
                    paymentPresent
                )
            dialogPaymentFilter.show(fragmentManager, TAG)
        }
    }

    private fun isValidDate(startDate: String, endDate: String): Int? {
        return when {
            startDate.trim().isEmpty() -> {
                binding.etStartDate.requestFocus()
                R.string.select_start_date
            }
            endDate.trim().isEmpty() -> {
                binding.etEndDate.requestFocus()
                R.string.select_end_date
            }
            else -> {
                null
            }
        }
    }
}