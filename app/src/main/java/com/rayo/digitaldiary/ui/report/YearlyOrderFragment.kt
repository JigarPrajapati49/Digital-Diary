package com.rayo.digitaldiary.ui.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentYearlyOrderBinding
import com.rayo.digitaldiary.helper.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YearlyOrderFragment : BaseFragment<FragmentYearlyOrderBinding, OrderReportViewModel>(),
    OrderReportPresenter {
    override val viewModel: OrderReportViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_yearly_order
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initStartEndDate(Constants.YEARLY)
        binding.dailyOrderPresenter = this
        binding.currencySymbol = preferenceManager.getPref(Constants.prefCurrencySymbol, "")
        setYearText()
        viewModel.orderHistoryList.observe(viewLifecycleOwner) {
            binding.totalOrders = if(it.isNullOrEmpty()) "0" else it.size.toString()
            var totalAmount = 0f
            for(item in it) {
                totalAmount += item.orderData.orderAmount.toFloat()
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