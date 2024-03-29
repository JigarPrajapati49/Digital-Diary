package com.rayo.digitaldiary.ui.report

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OrderViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> {
                MonthlyOrderFragment()
            }

            2 -> {
                YearlyOrderFragment()
            }

            else -> {
                DailyOrderFragment()
            }
        }
    }


}