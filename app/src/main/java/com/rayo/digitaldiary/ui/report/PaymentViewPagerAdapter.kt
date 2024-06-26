package com.rayo.digitaldiary.ui.report

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PaymentViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> {
                MonthlyPaymentFragment()
            }

            2 -> {
                YearlyPaymentFragment()
            }

            else -> {
                DailyPaymentFragment()
            }
        }
    }


}