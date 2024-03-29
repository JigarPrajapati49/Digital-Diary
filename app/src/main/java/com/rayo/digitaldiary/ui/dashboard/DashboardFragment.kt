package com.rayo.digitaldiary.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentDashboardBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.report.ReportActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 24/02/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding, DashboardViewModel>(),
    DashboardPresenter {

    private val dashboardList: MutableList<DashboardModel> = ArrayList()

    override val viewModel: DashboardViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as OwnerMainActivity).setDashBoardListener = this
        context?.let {
            dashboardList.clear()
            dashboardList.addAll(viewModel.getDashBoardData(it))
        }
        binding.presenter = this@DashboardFragment
        binding.isCreateOrderVisible = preferenceManager.getPref(Constants.prefUserType, "") != Constants.USER_TYPE_CUSTOMER
        viewModel.syncPaymentOrderCount()

        viewModel.isSync.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            binding.isVisible = it > 0
            viewModel.syncPaymentOrderCount()
        }


        binding.dashBoardAdapter = DashboardAdapter(dashboardList, this)
        setStaffAndCustomerCount(
            viewModel.totalStaff, viewModel.totalCustomer, viewModel.totalProduct
        )

        /* Get updated count if this fragment onCreate called again */
        if (viewModel.isStaffAndCustomerCountFetched) {
            (requireActivity() as OwnerMainActivity).getCustomerAndStaffCount()
        }
    }

    override fun onItemClicked(dashBoardData: DashboardModel) {
        when (dashBoardData.id) {
            Constants.DASHBOARD_STAFF_ID -> {
                findNavController().navigate(R.id.staffListingFragment)
            }

            Constants.DASHBOARD_CUSTOMER_ID -> {
                (mActivity as OwnerMainActivity).moveToCustomer()
            }

            Constants.DASHBOARD_ADD_PRODUCT_ID -> {
                (mActivity as OwnerMainActivity).moveToProduct()
            }

            Constants.DASHBOARD_ADD_ORDER_ID -> {
                findNavController().navigate(R.id.createOrderFragment)
            }

            Constants.DASHBOARD_ORDER_HISTORY -> {
                Bundle().let {
                    findNavController().navigate(R.id.ownerOrStaffOrderHistoryFragment, it)
                }
            }

            Constants.DASHBOARD_ADD_PAYMENT -> {
                Bundle().let {
                    it.putString(Constants.historyType, Constants.ALL)
                    it.putString(Constants.customerId, Constants.ALL)
                    findNavController().navigate(R.id.paymentHistoryFragment, it)
                }
            }

            Constants.DASHBOARD_REPORT -> {
                startActivity(Intent(context, ReportActivity::class.java))
            }
        }
    }

    override fun setStaffAndCustomerCount(totalStaff: Int, totalCustomer: Int, totalProduct: Int) {
        viewModel.isStaffAndCustomerCountFetched = true
        viewModel.totalStaff = totalStaff
        viewModel.totalCustomer = totalCustomer
        viewModel.totalProduct = totalProduct
        dashboardList[1].count = totalStaff.toString()
        dashboardList[0].count = totalCustomer.toString()
        dashboardList[2].count = totalProduct.toString()
        binding.dashBoardAdapter?.notifyItemRangeChanged(0, 3)
    }

    override fun onCreateOrderClick() {
            findNavController().navigate(R.id.createOrderFragment)
    }
}