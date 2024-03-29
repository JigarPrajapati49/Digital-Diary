package com.rayo.digitaldiary.ui.payment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.database.StaffWithPayment
import com.rayo.digitaldiary.databinding.FragmentAddPaymentBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.order.SelectCustomerDialogFragment
import com.rayo.digitaldiary.ui.order.SelectCustomerPresenter
import com.rayo.digitaldiary.ui.staff.StaffData
import com.rayo.digitaldiary.ui.staff.StaffMainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPaymentFragment :
    BaseFragment<FragmentAddPaymentBinding, AddPaymentViewModel>(), AddPaymentPresenter,
    SelectCustomerPresenter {

    override fun getFragmentId(): Int {
        return R.layout.fragment_add_payment
    }

    override val viewModel: AddPaymentViewModel by viewModels()
    private val staffWithPaymentList: ArrayList<StaffWithPayment> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.paymentData = PaymentData()
        val customerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.arguments?.getParcelable(Constants.customerData, CustomerData::class.java)
        } else {
            this.arguments?.getParcelable(Constants.customerData)
        }
        customerData?.let {
            viewModel.isShowCustomerDialog = false
            viewModel.getAllPayment()
            Handler(Looper.getMainLooper()).postDelayed({
                onCustomerSelected(it)
            }, 500)
        }
        binding.paymentPresenter = this
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")

        viewModel.paymentInsertedInDB.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { paymentData ->
                context?.let { it1 ->
                    if (networkInterceptor.isInternetAvailable()) {
                        when (activity) {
                            is OwnerMainActivity -> {
                                (activity as OwnerMainActivity).callSyncPaymentAPI()
                            }

                            is StaffMainActivity -> {
                                (activity as StaffMainActivity).callSyncPaymentAPI()
                            }
                        }
                    }
                    dismissProgressDialog()
                    dialogHelper.showOneButtonDialog(
                        it1,
                        Utils.getTranslatedValue(getString(R.string.payment_added))
                    ) { p0, p1 ->
                        dismissProgressDialog()
                        clearFormData()
                    }
                }
            }
        }

        viewModel.duePaymentList.observe(viewLifecycleOwner) {
            binding.isDueAmountListVisible = it.isNotEmpty()
            binding.duePaymentHistoryAdapter = DuePaymentHistoryAdapter(it, preferenceManager.getPref(Constants.prefCurrencySymbol, ""))
        }

        viewModel.customerList.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            if(!it.hasBeenHandled) {
                if (viewModel.isShowCustomerDialog) {
                    it.getContentIfNotHandled()?.let { customerList ->
                        SelectCustomerDialogFragment.show(childFragmentManager, customerList.toMutableList(), this, true)
                    }
                } else {
                    viewModel.isShowCustomerDialog = true
                }
            }
        }
    }

    private fun clearFormData() {
        binding.paymentData = PaymentData()
        binding.etCustomer.text?.clear()
        binding.totalDueAmount = 0f
        binding.isDuePaymentListVisible = false
        viewModel.duePaymentList.postValue(ArrayList())
        viewModel.selectedCustomerData = null
        binding.root.clearFocus()
    }

    override fun onSelectCustomerClick() {
        if (Utils.isSingleClick()){
            showProgressDialog()
            viewModel.getAllPayment()
        }
    }

    override fun onAddPaymentClick(paymentData: PaymentData) {
        if (Utils.isSingleClick()){
            if (viewModel.selectedCustomerData == null) {
                context?.let {
                    dialogHelper.showOneButtonDialog(
                        it,
                        Utils.getTranslatedValue(it.getString(R.string.please_select_customer))
                    )
                }
            } else if (paymentData.amount.trim().isEmpty()) {
                context?.let {
                    dialogHelper.showOneButtonDialog(
                        it,
                        Utils.getTranslatedValue(it.getString(R.string.please_enter_amount))
                    )
                }
            } else if (paymentData.amount.toFloat() <= 0f) {
                context?.let {
                    dialogHelper.showOneButtonDialog(
                        it,
                        Utils.getTranslatedValue(it.getString(R.string.amount_should_be_greater_than_zero))
                    )
                }
            } else {
                var selectedPaymentMode = ""
                if (binding.radioCash.isChecked) {
                    paymentData.modeOfPayment = Constants.CASH
                    selectedPaymentMode = Utils.getTranslatedValue(getString(R.string.cash))
                } else {
                    paymentData.modeOfPayment = Constants.ONLINE
                    selectedPaymentMode = Utils.getTranslatedValue(getString(R.string.online))
                }
                context?.let {
                    ConfirmPaymentDialogFragment.show(parentFragmentManager, paymentData, selectedPaymentMode,
                        binding.etCustomer.text.toString(),this)
                }
            }
        }
    }

    override fun onConfirmClick(paymentData: PaymentData) {
        val pData: PaymentData = paymentData
        var staffData: StaffData? = null
        val customerData = CustomerData()

        if (preferenceManager.getPref(Constants.prefUserType, "") == Constants.USER_TYPE_STAFF) {
            staffData = StaffData()
            staffData.id = preferenceManager.getPref(Constants.prefUserId, "")
            staffData.name = preferenceManager.getPref(Constants.prefName, "")
        }
        customerData.id = viewModel.selectedCustomerData?.id ?: ""
        customerData.name = viewModel.selectedCustomerData?.name ?: ""


        val staffWithPayment = StaffWithPayment(pData, customerData, staffData)
        staffWithPaymentList.add(staffWithPayment)
        //send details when back from this Fragment
        setFragmentResult(Constants.requestPaymentId, bundleOf(Constants.localPaymentId to staffWithPaymentList))
        viewModel.addPayment(paymentData)
    }

    override fun onCustomerSelected(customerData: CustomerData) {
        viewModel.selectedCustomerData = customerData
        binding.etCustomer.setText(customerData.name)
        binding.inputAmount.requestFocus()
        binding.isDuePaymentListVisible = true
        binding.totalDueAmount = customerData.dueAmount
        viewModel.getCustomerMonthDue(customerData.id, customerData.dueAmount)
    }
}
