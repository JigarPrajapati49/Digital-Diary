package com.rayo.digitaldiary.ui.report

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseActivity
import com.rayo.digitaldiary.databinding.ActivityReportBinding
import com.rayo.digitaldiary.helper.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportActivity : BaseActivity<ReportViewModel>(), ReportPresenter {
    override val viewModel: ReportViewModel by viewModels()
    lateinit var binding: ActivityReportBinding
    lateinit var navController: NavController
    var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setDataBindingView(R.layout.activity_report)
        binding.reportPresenter = this
        binding.toolbar.topAppBar.title = Utils.getTranslatedValue(getString(R.string.report))
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
        setUpNavigation()
    }

    private fun setUpNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentReportContainer) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onOrderClick() {
        if (isSingleClick()) {
            binding.fragmentReportContainer.findNavController().navigate(R.id.orderReportFragment)
        }
    }

    override fun onPaymentClick() {
        if (isSingleClick()) {
            binding.fragmentReportContainer.findNavController().navigate(R.id.paymentReportFragment)
        }
    }

    override fun onDueClick() {
        if (isSingleClick()) {
            binding.fragmentReportContainer.findNavController().navigate(R.id.paymentDueFragment)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun isSingleClick(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }

}