package com.rayo.digitaldiary.ui.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentPaymentReportBinding
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentReportFragment : BaseFragment<FragmentPaymentReportBinding, PaymentReportViewModel>() {
    override val viewModel: PaymentReportViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_payment_report
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.paymentPager.adapter = PaymentViewPagerAdapter(this)
        TabLayoutMediator(binding.paymentTab, binding.paymentPager) { tab, position ->
            when (position) {
                1 -> {
                    tab.text = Utils.getTranslatedValue(getString(R.string.monthly))
                }

                2 -> {
                    tab.text = Utils.getTranslatedValue(getString(R.string.yearly))
                }

                else -> {
                    tab.text = Utils.getTranslatedValue(getString(R.string.daily))
                }
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.paymentPager.adapter = null
    }
}