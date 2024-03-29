package com.rayo.digitaldiary.ui.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentMonthlyPaymentBinding
import com.rayo.digitaldiary.helper.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonthlyPaymentFragment :
    BaseFragment<FragmentMonthlyPaymentBinding, PaymentReportViewModel>(), PaymentReportPresenter {
    override val viewModel: PaymentReportViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_monthly_payment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initStartEndDate(Constants.MONTHLY)
        binding.monthlyReportPresenter = this
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        setMonthText()
        viewModel.paymentHistoryList.observe(viewLifecycleOwner) {
            binding.totalPayments = if(it.isNullOrEmpty()) "0" else it.size.toString()
            var totalAmount = 0f
            for(item in it) {
                totalAmount += item.amount.toFloat()
            }
            binding.totalAmount = totalAmount.toString()
        }
    }

    override fun onNextButtonClick() {
        viewModel.nextMonthClick()
        setMonthText()
    }

    override fun onPreviousButtonClick() {
        viewModel.previousMonthClick()
        setMonthText()
    }

    private fun setMonthText() {
        binding.tvOrderMonth.text = viewModel.getMonthText()
    }
}