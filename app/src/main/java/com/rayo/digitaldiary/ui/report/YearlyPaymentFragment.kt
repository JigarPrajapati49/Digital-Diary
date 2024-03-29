package com.rayo.digitaldiary.ui.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentYearlyPaymentBinding
import com.rayo.digitaldiary.helper.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YearlyPaymentFragment : BaseFragment<FragmentYearlyPaymentBinding, PaymentReportViewModel>(),
    PaymentReportPresenter {
    override val viewModel: PaymentReportViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_yearly_payment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initStartEndDate(Constants.YEARLY)
        binding.yearlyReportPresenter = this
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        setYearText()
        viewModel.paymentHistoryList.observe(viewLifecycleOwner) {
            binding.totalPayments = if(it.isNullOrEmpty()) "0" else it.size.toString()
            var totalAmount = 0f
            for(item in it) {
                totalAmount += item.amount.toFloat()
            }
            binding.totalAmount = totalAmount.toString()
        }
    }

    private fun setYearText() {
        binding.tvOrderYear.text = viewModel.getYearText()
    }

    override fun onNextButtonClick() {
        viewModel.nextYearClick()
        setYearText()
    }

    override fun onPreviousButtonClick() {
        viewModel.previousYearClick()
        setYearText()
    }
}