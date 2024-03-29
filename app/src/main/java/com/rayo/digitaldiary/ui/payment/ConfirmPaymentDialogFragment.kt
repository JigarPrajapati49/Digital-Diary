package com.rayo.digitaldiary.ui.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseDialogFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.databinding.DialogConfirmPaymentFragmentBinding
import com.rayo.digitaldiary.helper.Constants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 23/05/23.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class ConfirmPaymentDialogFragment(
    private val paymentData: PaymentData,
    private val selectedPaymentMode: String,
    private val customerName: String,
    private val addPaymentPresenter: AddPaymentPresenter
) : BaseDialogFragment<DialogConfirmPaymentFragmentBinding, BaseViewModel?>(),
    PaymentConfirmationPresenter {

    override val viewModel: BaseViewModel? = null

    override fun getDialogFragmentId(): Int {
        return R.layout.dialog_confirm_payment_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            paymentData = this@ConfirmPaymentDialogFragment.paymentData
            currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
            customerName = this@ConfirmPaymentDialogFragment.customerName
            paymentMode = this@ConfirmPaymentDialogFragment.selectedPaymentMode
            addPaymentPresenter = this@ConfirmPaymentDialogFragment
        }
    }

    override fun onConfirmPaymentClick(paymentData: PaymentData) {
        dismiss()
        addPaymentPresenter.onConfirmClick(paymentData)
    }

    override fun onCancelClick() {
        dismiss()
    }

    companion object {
        private const val TAG = "ConfirmPaymentDialogFragment"
        fun show(
            fragmentManager: FragmentManager,
            paymentData: PaymentData,
            selectedPaymentMode: String,
            customerName: String,
            paymentPresenter: AddPaymentPresenter
        ) {
            val selectCustomerDialog =
                ConfirmPaymentDialogFragment(
                    paymentData,
                    selectedPaymentMode,
                    customerName,
                    paymentPresenter
                )
            selectCustomerDialog.show(fragmentManager, TAG)
        }
    }
}