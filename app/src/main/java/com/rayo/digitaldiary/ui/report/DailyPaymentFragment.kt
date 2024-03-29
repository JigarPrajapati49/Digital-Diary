package com.rayo.digitaldiary.ui.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentDailyReportBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyPaymentFragment : BaseFragment<FragmentDailyReportBinding, PaymentReportViewModel>(), PaymentReportPresenter {
    override val viewModel: PaymentReportViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_daily_report
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initStartEndDate(Constants.DAILY)
        binding.dailyPaymentPresenter = this
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        setDateText()
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
        viewModel.nextDayClick()
        setDateText()
    }

    override fun onPreviousButtonClick() {
        viewModel.previousDayClick()
        setDateText()
    }

    override fun onPaymentDateClick() {
        if (Utils.isSingleClick()){
            val datePicker =
                MaterialDatePicker.Builder.datePicker().setSelection(viewModel.calendar.timeInMillis).build()
            datePicker.show(childFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                viewModel.calendar.timeInMillis = it
                viewModel.initStartEndDate(Constants.DAILY)
                setDateText()
            }
        }
    }

    private fun setDateText() {
        binding.tvPaymentDate.text = viewModel.getDayText()
    }
}