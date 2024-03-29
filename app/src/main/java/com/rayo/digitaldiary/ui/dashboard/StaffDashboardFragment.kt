package com.rayo.digitaldiary.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentStaffDashboardBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.ui.staff.StaffMainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaffDashboardFragment :
    BaseFragment<FragmentStaffDashboardBinding, StaffDashboardViewModel>(),
    StaffDashboardPresenter {

    override val viewModel: StaffDashboardViewModel by viewModels()
    private val dashboardList: MutableList<DashboardModel> = ArrayList()

    override fun getFragmentId(): Int {
        return R.layout.fragment_staff_dashboard
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as StaffMainActivity).setDashBoardListener = this
        context?.let {
            dashboardList.clear()
            dashboardList.addAll(viewModel.getDashBoardData(it))
        }
        binding.presenter = this
        binding.isCreateOrderVisible = preferenceManager.getPref(Constants.prefUserType, "") != Constants.USER_TYPE_CUSTOMER
        binding.staffDashBoardAdapter = StaffDashboardAdapter(dashboardList, this)

        setCustomerCount(viewModel.totalCustomer)
        setProductCount(viewModel.totalProduct)

        if (viewModel.isCustomerFetchCount) {
            (requireActivity() as StaffMainActivity).getCustomerCount()
        }
    }

    override fun onItemClicked(dashBoardData: DashboardModel) {
        when (dashBoardData.id) {
            Constants.DASHBOARD_OWNER_DETAIL -> {
                findNavController().navigate(R.id.ownerProfileFragment)
            }
            Constants.DASHBOARD_CUSTOMER_ID -> {
                (mActivity as StaffMainActivity).moveToCustomer()
            }
            Constants.DASHBOARD_ADD_ORDER_ID -> {
                findNavController().navigate(R.id.createOrderFragment)
            }
            Constants.DASHBOARD_ADD_PRODUCT_ID -> {
                findNavController().navigate(R.id.productListFragment)
            }
            Constants.DASHBOARD_ORDER_HISTORY -> {
                Bundle().let {
                    it.putString(
                        Constants.createdById,
                        preferenceManager.getPref(Constants.prefUserId)
                    )
                    findNavController().navigate(R.id.ownerOrStaffOrderHistoryFragment, it)
                }
            }

            Constants.DASHBOARD_ADD_PAYMENT -> {
                Bundle().let {
                    it.putString(Constants.historyType, Constants.STAFF)
                    it.putString(Constants.staffId, preferenceManager.getPref(Constants.prefUserId))
                    findNavController().navigate(R.id.paymentHistoryFragment, it)
                }
            }
        }
    }

    override fun setCustomerCount(totalCustomer: Int) {
        viewModel.isCustomerFetchCount = true
        viewModel.totalCustomer = totalCustomer
        dashboardList[0].count = totalCustomer.toString()
        binding.staffDashBoardAdapter?.notifyItemRangeChanged(0, 2)
    }

    override fun setProductCount(totalProduct: Int) {
        viewModel.isCustomerFetchCount = true
        viewModel.totalProduct = totalProduct
        dashboardList[2].count = totalProduct.toString()
        binding.staffDashBoardAdapter?.notifyItemRangeChanged(1, 2)
    }

    override fun onCreateOrderClick() {
        Log.e("TAG", "onCreateOrderClick: ")
        findNavController().navigate(R.id.createOrderFragment)
    }
}