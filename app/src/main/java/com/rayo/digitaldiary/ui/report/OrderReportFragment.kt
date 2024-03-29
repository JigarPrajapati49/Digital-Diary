package com.rayo.digitaldiary.ui.report

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.databinding.FragmentOrderReportBinding
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderReportFragment : BaseFragment<FragmentOrderReportBinding, BaseViewModel?>() {
    override val viewModel: BaseViewModel? = null

    override fun getFragmentId(): Int {
        return R.layout.fragment_order_report
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.orderPager.adapter = OrderViewPagerAdapter(this)
        TabLayoutMediator(binding.orderTab, binding.orderPager) { tab, position ->
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
        binding.orderPager.adapter = null
    }
}